package org.xbery.artbeams.products.admin

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
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.products.domain.{EditedProduct, Product}
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.products.service.ProductService

/**
  * Product administration routes.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/admin/products"))
class ProductAdminController @Inject() (productRepository: ProductRepository, productService: ProductService, common: ControllerComponents) extends BaseController(common) {
  private val logger = LoggerFactory.getLogger(getClass)
  private val TplBasePath = "admin/products"
  private val editFormDef = new ProductForm().definition

  @GetMapping
  def list(request: HttpServletRequest): Any = {
    // TODO RBe: Pagination
    val products = productRepository.findProducts()
    val model = createModel(request, "products" -> products, "emptyId" -> AssetAttributes.EmptyId)
    new ModelAndView(TplBasePath + "/productList", model)
  }

  @GetMapping(value = Array("/{id}/edit"), produces = Array(MediaType.TEXT_HTML_VALUE))
  def editForm(request: HttpServletRequest, @PathVariable("id") id: String): Any = {
    if (AssetAttributes.EmptyId == id) {
      renderEditForm(request, Product.Empty.toEdited(), ValidationResult.empty, None)
    } else {
      val productOpt = productRepository.findByIdAsOpt(id)
      productOpt match {
        case Some(product) =>
          renderEditForm(request, product.toEdited(), ValidationResult.empty, None)
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
      val productOrError = productService.saveProduct(edited)(requestToOperationCtx(request))
      productOrError match {
        case Left(ex) =>
          renderEditForm(request, formData.getData(), formData.getValidationResult(), Option(ex.toString()))
        case Right(productOpt) =>
          productOpt match {
            case Some(_) =>
              redirect("/admin/products")
            case _ =>
              notFound()
          }
      }
    }
  }

  private def renderEditForm(request: HttpServletRequest, edited: EditedProduct, validationResult: ValidationResult, errorMessage: Option[String]): Any = {
    val editForm = editFormDef.fill(new FormData(edited, validationResult))
    val model = createModel(request, "editForm" -> editForm, "errorMessage" -> errorMessage.orNull)
    new ModelAndView(TplBasePath + "/productEdit", model)
  }
}
