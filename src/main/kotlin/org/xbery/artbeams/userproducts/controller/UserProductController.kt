package org.xbery.artbeams.userproducts.controller

import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.error.NotFoundException
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.common.pdf.PdfSigner
import org.xbery.artbeams.consents.domain.ConsentType
import org.xbery.artbeams.consents.service.ConsentService
import org.xbery.artbeams.media.repository.MediaRepository
import org.xbery.artbeams.members.controller.MemberSectionController.Companion.MEMBER_SECTION_PATH
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.userproducts.service.UserProductService
import org.xbery.artbeams.users.service.UserService
import org.xbery.artbeams.web.FreeProductController
import java.io.ByteArrayOutputStream
import jakarta.servlet.http.HttpServletRequest
import org.xbery.artbeams.common.error.UnauthorizedException
import org.xbery.artbeams.orders.service.OrderService
import java.time.Instant

/**
 * User product detail.
 *
 * @author Radek Beran
 */
@Controller
class UserProductController(
    private val userProductService: UserProductService,
    private val productService: ProductService,
    private val userService: UserService,
    private val consentService: ConsentService,
    private val orderService: OrderService,
    private val mediaRepository: MediaRepository,
    private val pdfSigner: PdfSigner,
    // TBD RBe: Separate common component/controller
    private val freeProductController: FreeProductController,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping("$MEMBER_SECTION_PATH/{productSlug}")
    fun userProductDetail(@PathVariable productSlug: String, request: HttpServletRequest): Any {
        val userProduct = userProductService.findUserProductBySlug(productSlug, request)
        val model = createModel(
            request,
            "userProduct" to userProduct
        )
        return ModelAndView("member/userProduct", model)
    }

    /**
     * Serves binary data of product to user logged in client zone.
     */
    @GetMapping("$MEMBER_SECTION_PATH/{productSlug}/download")
    fun serveProductData(request: HttpServletRequest, @PathVariable productSlug: String): Any {
        return tryOrErrorResponse(request) {
            val login = userService.findCurrentUserLogin() ?: throw NotFoundException("Currently logged user was not found")
            val product = productService.requireBySlug(productSlug)

            val productFileName = requireFound(product.fileName) { "Product $productSlug has no file name" }

            // User must exist and must confirm the consent before he/she can download the product (free or paid)
            val user = userService.requireByLogin(login)

            if (!consentService.hasValidConsent(login, ConsentType.NEWS)) {
                throw UnauthorizedException("User with login $login has not confirmed the consent yet, product $productSlug cannot be downloaded")
            }

            // Check an order (any order) of the product for given user exists
            val orderItems = orderService.findOrderItemsOfUserAndProduct(user.id, product.id)
            if (orderItems.isEmpty()) throw UnauthorizedException("User ${user.login} has not ordered product ${product.slug}")
            val orders = orderItems.map { orderService.requireByOrderId(it.orderId) }
            if (orders.isEmpty()) throw UnauthorizedException("Order of the product ${product.slug} was not found for user ${user.login}")
            
            val completedOrders = orders.filter { it.state.isAfterPayment() || product.priceRegular.isZero() }
            if (completedOrders.isEmpty()) throw UnauthorizedException("User ${user.login} has not paid for the product ${product.slug} that requires payment")
            val completedOrder = completedOrders.first()        
            val orderItem = orderItems.first()

            val fileData = requireFound(mediaRepository.findFile(productFileName, null)) {
                "File $productFileName was not found"
            }
            val mediaType = fileData.getMediaType()
            val documentOutputStream = if (MediaType.APPLICATION_PDF == mediaType) {
                pdfSigner.addUserMetadataToPdf(
                    fileData.data,
                    product.title,
                    "Radek Beran", // TBD: Author
                    user.login,
                    user.fullName,
                    completedOrder.orderNumber
                )
            } else {
                val os = ByteArrayOutputStream()
                os.write(fileData.data)
                os
            }

            // Update order item as downloaded
            orderService.updateOrderItemDownloaded(orderItem.id, Instant.now())
            freeProductController.sendProductDownloadedNotification(product, user)

            ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(documentOutputStream.size().toLong())
                .cacheControl(CacheControl.noStore()) // prevent browsers and proxies to cache the request
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + fileData.filename
                )
                .body(documentOutputStream.toByteArray())
        }
    }
}
