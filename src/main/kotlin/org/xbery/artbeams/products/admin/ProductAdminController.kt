package org.xbery.artbeams.products.admin

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.products.domain.EditedProduct
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.simpleshop.service.SimpleShopSyncService

/**
 * Product administration routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/products")
class ProductAdminController(
    private val productRepository: ProductRepository,
    private val productService: ProductService,
    private val simpleShopSyncService: SimpleShopSyncService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val tplBasePath = "admin/products"
    private val editFormDef: FormMapping<EditedProduct> = ProductForm.definition

    @GetMapping
    fun list(request: HttpServletRequest): Any {
        // TODO: Pagination
        val products: List<Product> = productRepository.findProducts()
        val model = createModel(
            request, "products"
                    to products, "emptyId"
                    to AssetAttributes.EMPTY_ID
        )
        return ModelAndView("$tplBasePath/productList", model)
    }

    @GetMapping(value = ["/{id}/edit"], produces = [MediaType.TEXT_HTML_VALUE])
    fun editForm(request: HttpServletRequest, @PathVariable id: String?): Any {
        return if (id == null || AssetAttributes.EMPTY_ID == id) {
            renderEditForm(request, Product.Empty.toEdited(), ValidationResult.empty, null)
        } else {
            val product = productRepository.requireById(id)
            renderEditForm(
                request, product.toEdited(),
                ValidationResult.empty,
                null
            )
        }
    }

    @PostMapping("/save")
    fun save(request: HttpServletRequest): Any {
        val params = ServletRequestParams(request)
        val formData = editFormDef.bind(params)
        return if (!formData.isValid) {
            logger.warn("Form with validation errors: " + formData.validationResult)
            renderEditForm(request, formData.data, formData.validationResult, null)
        } else {
            val edited = formData.data
            try {
                val product = productService.saveProduct(edited, requestToOperationCtx(request))
                if (product != null) {
                    redirect("/admin/products")
                } else {
                    notFound(request)
                }
            } catch (ex: Exception) {
                renderEditForm(request, formData.data, formData.validationResult, ex.toString())
            }
        }
    }

    @PostMapping("/{id}/sync-from-simpleshop")
    fun syncFromSimpleShop(request: HttpServletRequest, @PathVariable id: String): Any {
        return try {
            val product = productRepository.requireById(id)
            val ctx = requestToOperationCtx(request)
            val result = simpleShopSyncService.syncProduct(product, ctx)

            if (result.success) {
                logger.info("Product synced successfully: ${result.message}")
                redirect("/admin/products/$id/edit?syncSuccess=true&message=${result.message}&fields=${result.updatedFields.joinToString(",")}")
            } else {
                logger.warn("Product sync failed: ${result.message}")
                redirect("/admin/products/$id/edit?syncError=true&message=${result.message}")
            }
        } catch (ex: Exception) {
            logger.error("Error syncing product: ${ex.message}", ex)
            redirect("/admin/products/$id/edit?syncError=true&message=${ex.message}")
        }
    }

    @PostMapping("/sync-all-from-simpleshop")
    fun syncAllFromSimpleShop(request: HttpServletRequest): Any {
        return try {
            val ctx = requestToOperationCtx(request)
            val results = simpleShopSyncService.syncAllProducts(ctx)

            val successCount = results.count { it.success }
            val updatedCount = results.count { it.updatedFields.isNotEmpty() }

            logger.info("Bulk sync completed: $successCount/${results.size} successful, $updatedCount updated")
            redirect("/admin/products?syncSuccess=true&total=${results.size}&success=$successCount&updated=$updatedCount")
        } catch (ex: Exception) {
            logger.error("Error during bulk sync: ${ex.message}", ex)
            redirect("/admin/products?syncError=true&message=${ex.message}")
        }
    }

    private fun renderEditForm(
        request: HttpServletRequest,
        edited: EditedProduct,
        validationResult: ValidationResult,
        errorMessage: String?
    ): Any {
        val editForm = editFormDef.fill(FormData(edited, validationResult))
        val model = createModel(
            request, "editForm"
                    to editForm, "errorMessage"
                    to errorMessage
        )
        return ModelAndView("$tplBasePath/productEdit", model)
    }
}
