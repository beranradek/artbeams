package org.xbery.artbeams.web

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.common.access.domain.EntityKey
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
    private val appConfig: AppConfig
) : BaseController(controllerComponents) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Shows order page for given product (with possible details of order/integration of invoicing system).
     */
    @GetMapping("/produkt/{slug}$ORDER_SUB_PATH")
    fun showProductOrder(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.requireBySlug(slug)
        return renderProductArticle(request, product, product.slug + "-objednavka", false)
    }

    /**
     * "Thank you" page for given product, displayed AFTER confirmation of order form and possible related online
     * payment. This page is also shown in the case when an offline payment (such as bank transfer) should be performed yet,
     * thus it is NOT a page offering access to the paid product.
     */
    @GetMapping("/produkt/{slug}/podekovani")
    fun showProductThankYouPage(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.findBySlug(slug)
        return if (product != null) {
            renderProductArticle(request, product, product.slug + "-podekovani", false)
        } else {
            notFound(request)
        }
    }

    @PostMapping("/produkt/{slug}/ordered")
    fun createOrder(
        request: HttpServletRequest,
        @PathVariable slug: String,
        @RequestParam("mail") mail: String,
        @RequestParam("cislo_objednavky") orderNumber: String,
        @RequestParam("state") state: String,
    ): Any {
        checkInvoicingSystemSecret(state)
        val product = productService.requireBySlug(slug)
        // Creates or updates user (possible new registration can be created). Adds consent to user.
        // TBD: Validate orderNumber, user's and product's data against SimpleShop API?
        // Pull first and last name of the user.
        val user = userSubscriptionService.createOrUpdateUserWithOrderAndConsent(
            null,
            mail,
            product,
            orderNumber,
            OrderState.CONFIRMED
        )

        // TODO: make user an member directly after this order confirmation (?)
        // Add product to user's library if not already there
        userProductService.addProductToUserLibrary(user.id, product.id)
        return ResponseEntity.ok("Order created")
    }

    @PostMapping("/produkt/{slug}/paid")
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
        return ResponseEntity.ok("Order created")
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
        return renderProductArticle(request, product, product.slug + "-doruceni", false)
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
        product: Product,
        articleSlug: String,
        saveUserAccess: Boolean,
        errorMessage: String? = null
    ): Any {
        val article = articleService.findBySlug(articleSlug)
        return if (article != null) {
            // Checks user device capabilities.
            val userAccessReport = if (saveUserAccess) {
                // Logs user access
                val entityKey = EntityKey.fromClassAndId(Article::class.java, article.id)
                controllerComponents.userAccessService.saveUserAccess(entityKey, request)
            } else {
                controllerComponents.userAccessService.getUserAccessReport(request)
            }

            val model = createModel(
                request,
                "product" to product,
                "article" to article,
                "userAccessReport" to userAccessReport,
                "errorMessage" to errorMessage
            )
            ModelAndView("productArticle", model)
        } else {
            logger.error("Article $articleSlug not found")
            notFound(request)
        }
    }

    companion object {
        const val ORDER_SUB_PATH = "/objednavka"
    }
}
