package org.xbery.artbeams.web

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.error.ForbiddenException
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.userproducts.service.UserProductService
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.service.UserService
import org.xbery.artbeams.users.service.UserSubscriptionService
import jakarta.servlet.http.HttpServletRequest

/**
 * Paid product routes.
 * @author Radek Beran
 */
@Controller
class PaidProductController(
    private val controllerComponents: ControllerComponents,
    private val articleService: ArticleService,
    private val productService: ProductService,
    private val userSubscriptionService: UserSubscriptionService,
    private val userService: UserService,
    private val orderService: OrderService,
    private val userProductService: UserProductService,
    private val appConfig: AppConfig,
    private val simpleShopApiClient: org.xbery.artbeams.simpleshop.service.SimpleShopApiClient
) : BaseController(controllerComponents) {

    /**
     * Shows order page for given product (with possible details of order/integration of invoicing system).
     */
    @GetMapping("/produkt/{slug}$ORDER_SUB_PATH")
    fun showProductOrder(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.requireBySlug(slug)
        return renderProductArticle(request, "productArticle", product, product.slug + "-objednavka")
    }

    /**
     * "Thank you" page for given product, displayed AFTER confirmation of order form and possible related online
     * payment. This page is also shown in the case when an offline payment (such as bank transfer) should be performed yet,
     * thus it is NOT a page offering access to the paid product.
     */
    @GetMapping("/produkt/{slug}/podekovani")
    fun showProductThankYouPage(
        request: HttpServletRequest,
        @PathVariable slug: String,
        @RequestParam(name = "cislo_objednavky", required = false) orderNumber: String?
    ): Any {
        val product = productService.findBySlug(slug)
        return if (product != null) {
            // Use the order number from request parameter, or try to find the latest order for this user and product
            val variableSymbol = orderNumber ?: run {
                val currentUser = controllerComponents.getLoggedUser(request)
                if (currentUser != null) {
                    try {
                        val orderItems = orderService.findOrderItemsOfUserAndProduct(currentUser.common.id, product.id)
                        if (orderItems.isNotEmpty()) {
                            // Find the order for the most recent order item
                            val latestOrderItem = orderItems.maxByOrNull { it.common.created }
                            latestOrderItem?.orderId?.let { orderId ->
                                orderService.requireByOrderId(orderId).orderNumber
                            }
                        } else null
                    } catch (e: Exception) {
                        logger.warn("Could not find order for user ${currentUser.login} and product ${product.slug}", e)
                        null
                    }
                } else null
            } ?: "123456" // Fallback to default if order number cannot be determined

            renderProductArticle(
                request,
                "products/productOrderConfirmed",
                product,
                product.slug + "-podekovani",
                null,
                "accountNumber" to appConfig.requireConfig("bankTransfer.accountNumber"),
                "bankCode" to appConfig.requireConfig("bankTransfer.bankCode"),
                "amount" to product.price.price,
                "currency" to product.price.currency,
                "variableSymbol" to variableSymbol,
                "message" to "Order of product ${product.title}"
            )
        } else {
            notFound(request)
        }
    }

    @GetMapping("/produkt/{slug}/ordered")
    fun createOrder(
        request: HttpServletRequest,
        @PathVariable slug: String,
        @RequestParam("mail") mail: String,
        @RequestParam("cislo_objednavky") orderNumber: String,
        @RequestParam("state") state: String,
    ): Any {
        checkInvoicingSystemSecret(state)
        val product = productService.requireBySlug(slug)

        // Validate order data against SimpleShop API if SimpleShop product ID is configured
        product.simpleShopProductId?.let { simpleShopProductId ->
            try {
                val simpleShopProduct = simpleShopApiClient.getProduct(simpleShopProductId)
                if (simpleShopProduct == null) {
                    logger.error("SimpleShop product validation failed: Product $simpleShopProductId not found in SimpleShop")
                    return ResponseEntity
                        .badRequest()
                        .body("Order validation failed: Product not found in payment system")
                }

                // Validate product name/title matches
                val productMatches = simpleShopProduct.name == product.slug ||
                                    simpleShopProduct.title?.contains(product.title, ignoreCase = true) == true
                if (!productMatches) {
                    logger.warn("SimpleShop product validation: Product name/title mismatch. " +
                               "Expected slug='${product.slug}' or title containing '${product.title}', " +
                               "but got name='${simpleShopProduct.name}', title='${simpleShopProduct.title}'")
                }

                // Validate price matches (if available in SimpleShop)
                simpleShopProduct.price?.let { simpleShopPrice ->
                    if (simpleShopPrice.compareTo(product.price.price) != 0) {
                        logger.warn("SimpleShop product validation: Price mismatch. " +
                                   "Expected ${product.price.price}, but SimpleShop has $simpleShopPrice")
                    }
                }

                logger.info("SimpleShop product validation successful for product $simpleShopProductId, order $orderNumber")
            } catch (e: Exception) {
                logger.error("SimpleShop product validation error for product $simpleShopProductId: ${e.message}", e)
                // Don't block the order on validation errors, just log them
                // The invoicing system has already confirmed the order
            }
        }

        // Creates or updates user (possible new registration can be created). Adds consent to user.
        // Pull first and last name of the user.
        val user = userSubscriptionService.createOrUpdateUserWithOrderAndConsent(
            null,
            mail,
            product,
            orderNumber,
            OrderState.CONFIRMED
        )

        // Add product to user's library if not already there
        userProductService.addProductToUserLibrary(user.id, product.id)
        return ResponseEntity.ok("Order created")
    }

    @GetMapping("/produkt/{slug}/paid")
    fun updateOrderPaid(
        request: HttpServletRequest,
        @PathVariable slug: String,
        @RequestParam("mail") mail: String,
        @RequestParam("cislo_objednavky") orderNumber: String,
        @RequestParam("state") state: String,
    ): Any {
        checkInvoicingSystemSecret(state)
        val product = productService.requireBySlug(slug)
        val user = userService.requireByLogin(mail)
        val order = orderService.requireByOrderNumber(orderNumber)
        requireOrderItemOfProductAndUser(order, product, user)
        orderService.updateOrderPaid(order.id)
        return ResponseEntity.ok("Order updated as paid")
    }

    /**
     * Shows instructions about product retrieval after product payment.
     * Used by invoicing system after the product was paid.
     */
    @GetMapping("/produkt/{slug}/doruceni")
    fun showDeliveryInfo(
        request: HttpServletRequest,
        @PathVariable slug: String,
        @RequestParam("mail") mail: String,
        @RequestParam("cislo_objednavky") orderNumber: String,
        @RequestParam("state") state: String,
    ): Any {
        checkInvoicingSystemSecret(state)
        val product = productService.requireBySlug(slug)
        val user = userService.requireByLogin(mail)
        val order = orderService.requireByOrderNumber(orderNumber)
        requireOrderItemOfProductAndUser(order, product, user)
        return renderProductArticle(request, "productArticle", product, product.slug + "-doruceni")
    }

    private fun checkInvoicingSystemSecret(state: String) {
        if (state != appConfig.requireConfig("invoicingSystem.secret")) {
            throw ForbiddenException("Invalid state parameter for integration with invoicing system")
        }
    }

    private fun requireOrderItemOfProductAndUser(
        order: Order,
        product: Product,
        user: User
    ) {
        requireFound(order.items.find { it.productId == product.id && it.quantity > 0 && it.createdBy == user.id }) {
            "Order item of user ${user.login} was not found for product ${product.slug} and orderNumber ${order.orderNumber}"
        }
    }

    private fun renderProductArticle(
        request: HttpServletRequest,
        viewName: String,
        product: Product,
        articleSlug: String,
        errorMessage: String? = null,
        vararg args: Pair<String, Any?>
    ): Any {
        val article = articleService.findBySlug(articleSlug)
        return if (article != null) {
            // Checks user device capabilities.
            val userAccessReport = controllerComponents.userAccessService.getUserAccessReport(request)

            val params = mapOf(
                "product" to product,
                "article" to article,
                "userAccessReport" to userAccessReport,
                "errorMessage" to errorMessage
            ) + args.toMap()
            val model = createModel(
                request,
                *params.toList().toTypedArray()
            )
            ModelAndView(viewName, model)
        } else {
            logger.error("Article $articleSlug not found")
            notFound(request)
        }
    }

    companion object {
        const val ORDER_SUB_PATH = "/objednavka"
    }
}
