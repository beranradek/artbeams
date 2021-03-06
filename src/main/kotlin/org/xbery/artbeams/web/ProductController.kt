package org.xbery.artbeams.web

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDDocumentInformation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.mailer.service.Mailer
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.media.domain.FileData
import org.xbery.artbeams.media.repository.MediaRepository
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.service.UserService
import org.xbery.artbeams.users.service.UserSubscriptionService
import java.io.ByteArrayOutputStream
import java.time.Instant
import javax.servlet.http.HttpServletRequest

/**
 * Product routes.
 * @author Radek Beran
 */
@Controller
open class ProductController(
    private val controllerComponents: ControllerComponents,
    private val articleService: ArticleService,
    private val productService: ProductService,
    private val userSubscriptionService: UserSubscriptionService,
    private val userService: UserService,
    private val orderService: OrderService,
    private val mediaRepository: MediaRepository,
    private val mailer: Mailer
) : BaseController(controllerComponents) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val normalizationHelper: NormalizationHelper = NormalizationHelper()

    @GetMapping("/produkt/{slug}/potvrzeni")
    fun productConfirmInfo(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.findBySlug(slug)
        return if (product != null) {
            val fullNameOpt = findNameInRequest(request)
            val emailOpt = findEmailInRequest(request)
            emailOpt?.let { email ->
                userSubscriptionService.subscribe(fullNameOpt, email, product.id)
            }
            renderProductArticle(request, product, product.slug + "-potvrzeni", false)
        } else {
            notFound()
        }
    }

    @GetMapping("/produkt/{slug}/odeslano")
    fun productSent(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.findBySlug(slug)
        return if (product != null) {
            val fullNameOpt = findNameInRequest(request)
            val emailOpt = findEmailInRequest(request)
            emailOpt?.let { email ->
                userSubscriptionService.confirmConsent(fullNameOpt, email, product.id)
            }
            renderProductArticle(request, product, product.slug + "-odeslano", false)
        } else {
            notFound()
        }
    }

    @GetMapping("/produkt/{slug}/download")
    fun findFile(request: HttpServletRequest, @PathVariable slug: String): ResponseEntity<*> {
        val product = productService.findBySlug(slug)
        val productFileName = product?.fileName
        return if (product != null && productFileName != null) {
            val email = findEmailInRequest(request)
            if (email != null) {
                // User must exist and confirm the consent before he/she can download the product
                val user = userService.findByEmail(email)
                if (user != null) {
                    // Update user with full name from request if it is present (and not set in user entity yet)
                    updateUserWithFullName(request, user)
                    if (user.consent != null) {
                        val orderItem = orderService.findOrderItemOfUser(user.id, product.id)
                        if (orderItem != null && orderItem.quantity > 0) {
                            // An order of the product for given user exists
                            // TODO: In case of paid product, check the order was already paid
                            val fileData = mediaRepository.findFile(productFileName, null)
                            if (fileData != null) {
                                val mediaType = fileData.getMediaType()
                                val documentOutputStream = if (MediaType.APPLICATION_PDF == mediaType) {
                                    writeMetadataToPdf(request, product, user, fileData)
                                } else {
                                    val os = ByteArrayOutputStream()
                                    os.write(fileData.data)
                                    os
                                }

                                orderService.updateOrderItemDownloaded(orderItem.id, Instant.now())
                                sendProductDownloadedNotification(product, user)

                                ResponseEntity.ok()
                                    .contentType(mediaType)
                                    .contentLength(documentOutputStream.size().toLong())
                                    .cacheControl(CacheControl.noStore()) // prevent browsers and proxies to cache the request
                                    .header(
                                        HttpHeaders.CONTENT_DISPOSITION,
                                        "attachment; filename=" + fileData.filename
                                    )
                                    .body(documentOutputStream.toByteArray())
                            } else {
                                notFound()
                            }
                        } else {
                            unauthorized()
                        }
                    } else {
                        unauthorized()
                    }
                } else {
                    unauthorized()
                }
            } else {
                unauthorized()
            }
        } else {
            notFound()
        }
    }

    /**
     * GET product detail.
     */
    @GetMapping("/produkt/{slug}")
    fun productDetail(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.findBySlug(slug)
        return if (product != null) {
            renderProductArticle(request, product, product.slug, true)
        } else {
            notFound()
        }
    }

    private fun writeMetadataToPdf(
        request: HttpServletRequest,
        product: Product,
        customer: User,
        fileData: FileData
    ): ByteArrayOutputStream {
        val pdfDocument: PDDocument = PDDocument.load(fileData.data)
        val pdfOutputStream = ByteArrayOutputStream()
        try {
            val nowCalendar: java.util.Calendar = java.util.Calendar.getInstance()
            val pdfMetadata: PDDocumentInformation = pdfDocument.documentInformation
            val productAuthorOpt = userService.findById(product.common.createdBy)
            pdfMetadata.author = ("")
            pdfMetadata.creationDate = nowCalendar
            pdfMetadata.modificationDate = nowCalendar
            pdfMetadata.title = product.title
            val urlBase: String = getUrlBase(request)
            val customerInfo =
                customer.email + (if (customer.fullName.isEmpty()) "" else " (" + customer.fullName + ")")
            pdfMetadata.creator = urlBase + " for " + customerInfo
            pdfDocument.documentInformation = pdfMetadata
            pdfDocument.save(pdfOutputStream)
        } finally {
            pdfDocument.close()
        }
        return pdfOutputStream
    }

    private fun updateUserWithFullName(request: HttpServletRequest, user: User): User {
        val fullName = findNameInRequest(request) ?: ""
        return if (user.firstName == "" && user.lastName == "" && !fullName.isEmpty()) {
            val names = User.namesFromFullName(fullName)
            userService.updateUser(user.copy(firstName = names.first, lastName = names.second))
        } else {
            user
        }
    }

    private fun renderProductArticle(
        request: HttpServletRequest,
        product: Product,
        articleSlug: String,
        saveUserAccess: Boolean
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
                Pair("product", product),
                Pair("article", article),
                Pair("userAccessReport", userAccessReport)
            )
            ModelAndView("productArticle", model)
        } else {
            logger.error("Article ${articleSlug} not found")
            notFound()
        }
    }

    private fun findEmailInRequest(request: HttpServletRequest): String? {
        val email = request.getParameter("email")
        return if (email != null && !email.isEmpty()) {
            email.replace(' ', '+')
        } else {
            null
        }
    }

    private fun findNameInRequest(request: HttpServletRequest): String? {
        val name = request.getParameter("name")
        return if (name != null && !name.isEmpty()) {
            name
        } else {
            null
        }
    }

    /**
     * Sends email notification to product's author that an user has downloaded the product.
     * @param product
     * @param user
     */
    private fun sendProductDownloadedNotification(product: Product, user: User) {
        val productAuthor = userService.findById(product.createdBy)
        if (productAuthor != null) {
            if (productAuthor.email != null && !productAuthor.email.isEmpty()) {
                val subject =
                    normalizationHelper.removeDiacriticalMarks("User ${user.firstName} ${user.lastName} downloaded ${product.title}")
                val body =
                    normalizationHelper.removeDiacriticalMarks("User ${user.firstName} ${user.lastName}/${user.email} has downloaded product ${product.title}.")
                mailer.sendMail(subject, body, productAuthor.email)
            }
        }
    }
}
