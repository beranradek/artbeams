package org.xbery.artbeams.articles.admin

import net.formio.FormData
import net.formio.FormMapping
import net.formio.validation.ValidationResult
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.agent.ArticleEditingAgent
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.domain.EditedArticle
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.error.ForbiddenException
import org.xbery.artbeams.common.form.SpringHttpServletRequestParams
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.error.OperationException
import org.xbery.artbeams.google.error.GoogleErrorCode
import org.xbery.artbeams.media.repository.ArticleImageRepository
import java.nio.channels.Channels
import jakarta.servlet.http.HttpServletRequest

/**
 * Article administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/articles")
class ArticleAdminController(
    private val articleService: ArticleService,
    private val categoryRepository: CategoryRepository,
    private val articleImageRepository: ArticleImageRepository,
    private val applicationContext: ApplicationContext,
    common: ControllerComponents
) : BaseController(common) {
    private val tplBasePath: String = "admin/articles"
    private val paramSaveWithinEditor: String = "saveWithinEditor"
    private val editFormDef: FormMapping<EditedArticle> = ArticleForm.definition

    @GetMapping
    fun list(
        @RequestParam("offset", defaultValue = "0") offset: Int,
        @RequestParam("limit", defaultValue = "20") limit: Int,
        request: HttpServletRequest
    ): Any {
        val isAdmin = hasAuthority(request, "admin")
        val pagination = Pagination(offset, limit)
        val resultPage = if (isAdmin) articleService.findArticles(pagination) else articleService.findDraftArticles(pagination)
        val model = createModel(
            request,
            "resultPage"
                to resultPage,
            "emptyId"
                to AssetAttributes.EMPTY_ID,
            "canPublish"
                to isAdmin
        )
        return ModelAndView("$tplBasePath/articleList", model)
    }

    @GetMapping(value = ["/{id}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(request: HttpServletRequest, @PathVariable id: String?): Any {
        return if (id == null || AssetAttributes.EMPTY_ID == id) {
            renderEditForm(request, Article.EmptyEdited, ValidationResult.empty, null)
        } else {
            try {
                // We do not want to update article's content with external data right after saving its data
                // to external storage without leaving editor for further editing (expensive and useless operation).
                val updateWithExternalData = hasAuthority(request, "admin") && request.getParameter(paramSaveWithinEditor) == null
                val edited = articleService.findEditedArticle(id, updateWithExternalData)
                if (!hasAuthority(request, "admin") && !edited.draft) {
                    throw ForbiddenException("Redactor cannot edit non-draft articles")
                }
                return renderEditForm(request, edited, ValidationResult.empty, null)
            } catch (ex: OperationException) {
                if (ex.errorCode == GoogleErrorCode.UNAUTHORIZED) {
                    redirect("/admin/google-docs/authorization")
                } else {
                    throw ex
                }
            }
        }
    }

    @PostMapping(value = ["/save"])
    fun save(request: HttpServletRequest): Any {
        val params = SpringHttpServletRequestParams(request)
        val formData: FormData<EditedArticle> = editFormDef.bind(params)
        return try {
            if (!formData.isValid) {
                logger.warn("Form with validation errors: " + formData.validationResult)
                renderEditForm(request, formData.data, formData.validationResult, null)
            } else {
                var edited: EditedArticle = formData.data
                if (!hasAuthority(request, "admin")) {
                    if (edited.id != AssetAttributes.EMPTY_ID) {
                        val existing = articleService.findEditedArticle(edited.id, false)
                        if (!existing.draft) {
                            throw ForbiddenException("Redactor cannot modify non-draft articles")
                        }
                    }
                    // Redactor (and any non-admin) can write drafts, but cannot publish to public blog.
                    // Also disallow external sync identifiers (Evernote / Google Docs) for non-admins.
                    edited = edited.copy(draft = true, externalId = null)
                }

                val uploadedFile = edited.file
                val originalFileName = uploadedFile?.fileName
                if (uploadedFile != null && !originalFileName.isNullOrEmpty()) {
                    val imageName = articleImageRepository.storeArticleImage(Channels.newInputStream(uploadedFile.content), originalFileName)
                    imageName?.let { edited = edited.copy(image = it) }
                }

                try {
                    val article = articleService.saveArticle(edited, requestToOperationCtx(request))
                    if (article != null) {
                        if (params.getParamValue("save") != null) {
                            // Save without leave
                            redirect("/admin/articles/${article.id}/edit?$paramSaveWithinEditor=1")
                        } else {
                            redirect("/admin/articles")
                        }
                    } else {
                        notFound(request)
                    }
                } catch (ex: Exception) {
                    renderEditForm(request, formData.data, formData.validationResult, ex.toString())
                }
            }
        } finally {
            formData.data?.file?.deleteTempFile()
        }
    }

    @PostMapping(value = ["/{id}/delete"])
    fun delete(request: HttpServletRequest, @PathVariable id: String?): Any {
        if (id.isNullOrBlank() || id == AssetAttributes.EMPTY_ID) {
            return badRequest(request)
        }
        if (!hasAuthority(request, "admin")) {
            val existing = articleService.findEditedArticle(id, false)
            if (!existing.draft) {
                throw ForbiddenException("Redactor cannot delete non-draft articles")
            }
        }
        articleService.deleteArticle(id, requestToOperationCtx(request))
        return redirect("/admin/articles")
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: EditedArticle,
        validationResult: ValidationResult,
        errorMessage: String?
    ): Any {
        val editForm: FormMapping<EditedArticle> = editFormDef.fill(FormData(edited, validationResult))
        // Fetch categories codebook
        val categories: List<Category> = categoryRepository.findCategories()
        val model = createModel(
            request,
            "editForm" to editForm,
            "errorMessage" to errorMessage,
            "categories" to categories,
            "articleAgentAvailable" to isArticleAgentAvailable(),
            "canPublish" to hasAuthority(request, "admin")
        )
        return ModelAndView("$tplBasePath/articleEdit", model)
    }

    private fun hasAuthority(request: HttpServletRequest, authority: String): Boolean {
        val principal = request.userPrincipal
        if (principal is UsernamePasswordAuthenticationToken) {
            val authorities: Collection<GrantedAuthority> = principal.authorities ?: emptyList()
            return authorities.any { it.authority == authority }
        }
        return false
    }

    /**
     * Checks if ArticleEditingAgent bean is available in the application context.
     * The agent is only available when openai.enabled=true configuration property is set.
     */
    private fun isArticleAgentAvailable(): Boolean = try {
        applicationContext.getBean(ArticleEditingAgent::class.java)
        true
    } catch (e: Exception) {
        false
    }
}
