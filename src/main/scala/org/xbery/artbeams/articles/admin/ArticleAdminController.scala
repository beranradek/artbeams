package org.xbery.artbeams.articles.admin

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, PostMapping, RequestMapping}
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.{Article, EditedArticle}
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}

/**
  * Article administration routes.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/admin/articles"))
class ArticleAdminController @Inject() (
  articleService: ArticleService,
  categoryRepository: CategoryRepository,
  common: ControllerComponents)
  extends BaseController(common) {

  private val logger = LoggerFactory.getLogger(getClass)
  private val TplBasePath = "admin/articles"
  private val editFormDef = new ArticleForm().definition

  @GetMapping
  def list(request: HttpServletRequest): Any = {
    // TODO RBe: Pagination
    val articles = articleService.findArticles()
    val model = createModel(request, "articles" -> articles, "emptyId" -> AssetAttributes.EmptyId)
    new ModelAndView(TplBasePath + "/articleList", model)
  }

  @GetMapping(value = Array("/{id}/edit"), produces = Array(MediaType.TEXT_HTML_VALUE))
  def editForm(request: HttpServletRequest, @PathVariable("id") id: String): Any = {
    if (AssetAttributes.EmptyId == id) {
      renderEditForm(request, Article.EmptyEdited, ValidationResult.empty, None)
    } else {
      articleService.findEditedArticle(id) match {
        case Some(edited) =>
          renderEditForm(request, edited, ValidationResult.empty, None)
        case _ =>
          notFound()
      }
    }
  }

  @PostMapping(value = Array("/save"))
  def save(request: HttpServletRequest): Any = {
    val params = new ServletRequestParams(request)
    val formData = editFormDef.bind(params)
    if (!formData.isValid()) {
      logger.warn("Form with validation errors: " + formData.getValidationResult())
      renderEditForm(request, formData.getData(), formData.getValidationResult(), None)
    } else {
      val edited = formData.getData()
      val articleOrError = articleService.saveArticle(edited)(requestToOperationCtx(request))
      articleOrError match {
        case Left(ex) =>
          renderEditForm(request, formData.getData(), formData.getValidationResult(), Option(ex.toString()))
        case Right(articleOpt) =>
          articleOpt match {
            case Some(article) =>
              if (params.getParamValue("save") != null) {
                // Save without leave
                redirect(s"/admin/articles/${article.id}/edit")
              } else {
                redirect("/admin/articles")
              }
            case _ =>
              notFound()
          }
      }
    }
  }

  private def renderEditForm(request: HttpServletRequest, edited: EditedArticle, validationResult: ValidationResult, errorMessage: Option[String]): Any = {
    val editForm = editFormDef.fill(new FormData(edited, validationResult))
    // Fetch categories codebook
    val categories = categoryRepository.findCategories()
    val model = createModel(request, "editForm" -> editForm, "errorMessage" -> errorMessage.orNull, "categories" -> categories)
    new ModelAndView(TplBasePath + "/articleEdit", model)
  }
}
