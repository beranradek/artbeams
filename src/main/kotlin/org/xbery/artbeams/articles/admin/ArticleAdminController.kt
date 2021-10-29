package org.xbery.artbeams.articles.admin

import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.domain.EditedArticle
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import javax.servlet.http.HttpServletRequest

/**
 * Article administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/articles")
open class ArticleAdminController(
    private val articleService: ArticleService,
    private val categoryRepository: CategoryRepository,
    private val common: ControllerComponents
) : BaseController(common) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val TplBasePath: String = "admin/articles"
    private val editFormDef: FormMapping<EditedArticle> = ArticleForm.definition

    @GetMapping
    fun list(request: HttpServletRequest): Any {
        // TODO: Pagination
        val articles: List<Article> = articleService.findArticles()
        val model = createModel(
            request, "articles"
                    to articles, "emptyId"
                    to AssetAttributes.EmptyId
        )
        return ModelAndView(TplBasePath + "/articleList", model)
    }

    @GetMapping(value = ["/{id}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(request: HttpServletRequest, @PathVariable id: String?): Any {
        return if (id == null || AssetAttributes.EmptyId == id) {
            renderEditForm(request, Article.EmptyEdited, ValidationResult.empty, null)
        } else {
            val edited = articleService.findEditedArticle(id)
            return if (edited != null) {
                renderEditForm(request, edited, ValidationResult.empty, null)
            } else {
                notFound()
            }
        }
    }

    @PostMapping(value = ["/save"])
    fun save(request: HttpServletRequest): Any {
        val params: ServletRequestParams = ServletRequestParams(request)
        val formData: FormData<EditedArticle> = editFormDef.bind(params)
        return if (!formData.isValid) {
            logger.warn("Form with validation errors: " + formData.validationResult)
            renderEditForm(request, formData.data, formData.validationResult, null)
        } else {
            val edited: EditedArticle = formData.data
            try {
                val article = articleService.saveArticle(edited, requestToOperationCtx(request))
                if (article != null) {
                    if (params.getParamValue("save") != null) {
                        // Save without leave
                        redirect("/admin/articles/${article.id}/edit")
                    } else {
                        redirect("/admin/articles")
                    }
                } else {
                    notFound()
                }
            } catch (ex: Exception) {
                renderEditForm(request, formData.data, formData.validationResult, ex.toString())
            }
        }
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: EditedArticle,
        validationResult: ValidationResult,
        errorMessage: String?
    ): Any {
        val editForm: FormMapping<EditedArticle> = editFormDef.fill(FormData<EditedArticle>(edited, validationResult))
        // Fetch categories codebook
        val categories: List<Category> = categoryRepository.findCategories()
        val model = createModel(
            request, "editForm" to editForm, "errorMessage" to errorMessage, "categories" to categories
        )
        return ModelAndView(TplBasePath + "/articleEdit", model)
    }
}
