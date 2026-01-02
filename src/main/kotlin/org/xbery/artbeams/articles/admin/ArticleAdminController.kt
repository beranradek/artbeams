package org.xbery.artbeams.articles.admin

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.validation.ValidationResult
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
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
import org.xbery.artbeams.common.form.SpringHttpServletRequestParams
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.error.OperationException
import org.xbery.artbeams.google.error.GoogleErrorCode
import org.xbery.artbeams.media.repository.ArticleImageRepository
import java.nio.channels.Channels

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
        val pagination = Pagination(offset, limit)
        val resultPage = articleService.findArticles(pagination)
        val model = createModel(
            request, "resultPage"
                    to resultPage, "emptyId"
                    to AssetAttributes.EMPTY_ID
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
                val updateWithExternalData = request.getParameter(paramSaveWithinEditor) == null
                val edited = articleService.findEditedArticle(id, updateWithExternalData)
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
                            redirect("/admin/articles/${article.id}/edit?${paramSaveWithinEditor}=1")
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
            "articleAgentAvailable" to isArticleAgentAvailable()
        )
        return ModelAndView("$tplBasePath/articleEdit", model)
    }

    /**
     * Checks if ArticleEditingAgent bean is available in the application context.
     * The agent is only available when openai.enabled=true configuration property is set.
     */
    private fun isArticleAgentAvailable(): Boolean {
        return try {
            applicationContext.getBean(ArticleEditingAgent::class.java)
            true
        } catch (e: Exception) {
            false
        }
    }
}
