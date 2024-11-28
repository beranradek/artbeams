package org.xbery.artbeams.web

import jakarta.servlet.http.HttpServletRequest
import net.formio.FormData
import net.formio.FormMapping
import net.formio.servlet.ServletRequestParams
import net.formio.validation.ValidationResult
import org.apache.pdfbox.Loader
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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.antispam.recaptcha.service.RecaptchaService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.error.UnauthorizedException
import org.xbery.artbeams.common.error.requireAuthorized
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.common.form.FormErrors
import org.xbery.artbeams.common.mailer.service.MailgunMailSender
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.mailing.api.MailingApi
import org.xbery.artbeams.mailing.controller.SubscriptionForm
import org.xbery.artbeams.mailing.controller.SubscriptionFormData
import org.xbery.artbeams.media.domain.FileData
import org.xbery.artbeams.media.repository.MediaRepository
import org.xbery.artbeams.orders.domain.OrderItem
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.userproducts.service.UserProductService
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.service.UserService
import org.xbery.artbeams.users.service.UserSubscriptionService
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.Instant

/**
 * Product routes.
 * @author Radek Beran
 */
@Controller
class ProductController(
    private val controllerComponents: ControllerComponents,
    private val articleService: ArticleService,
    private val productService: ProductService,
    private val userSubscriptionService: UserSubscriptionService,
    private val userService: UserService,
    private val orderService: OrderService,
    private val userProductService: UserProductService,
    private val mediaRepository: MediaRepository,
    private val mailSender: MailgunMailSender,
    private val mailingApi: MailingApi,
    private val recaptchaService: RecaptchaService
) : BaseController(controllerComponents) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val normalizationHelper: NormalizationHelper = NormalizationHelper()

    /**
     * Processes leading form with subscription/ordering of given product - sends subscription
     * confirmation email to the user.
     */
    @PostMapping("/produkt/{slug}/subscribe")
    fun subscribe(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.findBySlug(slug)
        return if (product != null) {
            val params = ServletRequestParams(request)
            val formData = subscriptionFormDef.bind(params)
            return if (!formData.isValid) {
                val validationResult = formData.validationResult
                logger.warn("Form with validation errors: $validationResult")

                // Render AJAX response with HTML from subscriptionFormContent template
                subscriptionFormResponse(formData, request)
            } else {
                val data = formData.data
                val recaptchaResult = recaptchaService.verifyRecaptcha(request)
                if (!recaptchaResult.success) {
                    logger.warn("Captcha token was incorrect, score=${recaptchaResult.score}, for subscription for email=${data.email}, name=${data.name}")
                    subscriptionFormResponse(FormErrors.formDataWithCaptchaInvalidError(formData), request)
                } else {
                    logger.info("Captcha token was correct, score=${recaptchaResult.score}, for subscription for email=${data.email}, name=${data.name}")
                    try {
                        // We should not register the user until he confirms the subscription from email,
                        // so he does not occupy registration to real owner of the email address.
                        // This triggers sending of confirmation email:
                        mailingApi.subscribeToGroup(
                            data.email,
                            data.name,
                            requireNotNull(product.confirmationMailingGroupId),
                            request.remoteAddr
                        )
                        ajaxRedirect("/produkt/$slug/potvrzeni")
                    } catch (ex: Exception) {
                        logger.error("Error while subscribing user ${data.email}/${data.name} to product ${slug}: ${ex.message}", ex)
                        subscriptionFormResponse(FormErrors.formDataWithInternalError(formData), request)
                    }
                }
            }
        } else {
            notFound(request)
        }
    }

    /**
     * Shows confirmation page after subscription request to given product.
     * Just read operation without side effect.
     */
    @GetMapping("/produkt/{slug}/potvrzeni")
    fun showSubscriptionInfo(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.requireBySlug(slug)
        return renderProductArticle(request, product, product.slug + "-potvrzeni", false)
    }

    /**
     * Confirms user's consent with subscription and subscribes user to mailing group related to given product.
     * This subscription sends an email with the digital product itself.
     * Finally, an HTML page about sending of the product is rendered.
     */
    @GetMapping("/produkt/{slug}/odeslano") // TBD: Rename to /confirm action, also in emails
    fun confirmSubscriptionAndSendProduct(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.requireBySlug(slug)
        val fullNameOpt = findNameInRequest(request)
        val email = findEmailInRequest(request)
        return if (email != null) {
            // Creates or updates user (possible new registration can be created). Adds consent to user.
            val user = userSubscriptionService.createOrUpdateUserWithOrderAndConsent(fullNameOpt, email, product)

            // TBD: Update order state to PROCESSING

            // TODO: Check product is already paid if this is paid product
            // TODO: make user an member directly after subscription confirmation (?)
            userProductService.addProductToUserLibrary(user.id, product.id)

            // This triggers sending of the product to the user:
            // TBD: Send an alternative password setup email to member section for paid product
            // TBD: Send instructions to login for user that already set his member section password (store this info in user entity)
            // TBD: Also subscribe user to final mailing group for paid product but without triggering email with product download
            mailingApi.subscribeToGroup(user.email, fullNameOpt ?: "", requireNotNull(product.mailingGroupId), request.remoteAddr)

            // TBD: Update order state to SHIPPED

            // Redirect to this page without email and name parameters shown in URL
            // TBD: Create separate /odeslano route for rendering -odeslano product article
            redirect("/produkt/$slug/odeslano")
        } else {
            // No email in URL - this is second request after the subscription was already confirmed.
            // Showing page informing product was sent.
            renderProductArticle(request, product, product.slug + "-odeslano", false)
        }
    }

    /**
     * Shows order page for given product (with possible details of order/integration of invoicing system).
     */
    @GetMapping("/produkt/{slug}$ORDER_SUB_PATH")
    fun showProductOrder(request: HttpServletRequest, @PathVariable slug: String): Any {
        val product = productService.findBySlug(slug)
        return if (product != null) {
            renderProductArticle(request, product, product.slug + "-objednavka", false)
        } else {
            notFound(request)
        }
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

    /**
     * Serves binary data of product to user identified by email in the request.
     */
    @GetMapping("/produkt/{slug}/download")
    fun serveProductData(request: HttpServletRequest, @PathVariable slug: String): Any {
        return tryOrErrorResponse(request) {
            val product = productService.requireBySlug(slug)
            // Allow this not fully secured download only for free products (with zero regular prices)
            // For paid products, secure download is available only from member section.
            if (!product.priceRegular.isZero()) {
                throw IllegalStateException("Product $slug is not free and cannot be downloaded via this route.")
            }

            val productFileName = requireFound(product.fileName) { "Product $slug has no file name" }
            val email = requireAuthorized(findEmailInRequest(request)) { "Email is missing" }

            // User must exist and must confirm the consent before he/she can download the product
            val user = userService.requireByLogin(email)
            if (user.consent == null) throw UnauthorizedException("User with email $email has not confirmed the consent")

            // Update user with full name from request if it is present (and not set in user entity yet)
            updateUserWithFullName(request, user)

            // Check an order of the product for given user exists
            val orderItem = requireLastOrderItemWithProduct(user, product, email, slug)

            // TODO: In case of paid product, check the order was already paid

            val fileData = requireFound(mediaRepository.findFile(productFileName, null)) {
                "File $productFileName was not found"
            }
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
        }
    }

    private fun requireLastOrderItemWithProduct(
        user: User,
        product: Product,
        email: String,
        slug: String
    ): OrderItem {
        val orderItem = orderService.requireLastOrderItemOfUser(user.id, product.id)
        if (orderItem.quantity <= 0) throw UnauthorizedException(
            "User with email $email has not ordered product $slug"
        )
        return orderItem
    }

    /**
     * GET product detail HTML page.
     */
    @GetMapping("/produkt/{slug}")
    fun productDetail(request: HttpServletRequest, @PathVariable slug: String): Any {
        return tryOrErrorResponse(request) {
            val product = requireFound(productService.findBySlug(slug)) { "Product $slug was not found" }
            renderProductArticle(request, product, product.slug, true)
        }
    }

    private fun writeMetadataToPdf(
        request: HttpServletRequest,
        product: Product,
        customer: User,
        fileData: FileData
    ): ByteArrayOutputStream {
        val pdfDocument = Loader.loadPDF(fileData.data)
        val pdfOutputStream = ByteArrayOutputStream()
        pdfDocument.use { pdfDoc ->
            val nowCalendar: java.util.Calendar = java.util.Calendar.getInstance()
            val pdfMetadata: PDDocumentInformation = pdfDoc.documentInformation
            userService.findById(product.common.createdBy) // check product author
            pdfMetadata.author = ("")
            pdfMetadata.creationDate = nowCalendar
            pdfMetadata.modificationDate = nowCalendar
            pdfMetadata.title = product.title
            val urlBase: String = getUrlBase(request)
            val customerInfo =
                customer.email + (if (customer.fullName.isEmpty()) "" else " (" + customer.fullName + ")")
            pdfMetadata.creator = "$urlBase for $customerInfo"
            pdfDoc.documentInformation = pdfMetadata
            pdfDoc.save(pdfOutputStream)
        }
        return pdfOutputStream
    }

    private fun updateUserWithFullName(request: HttpServletRequest, user: User): User {
        val fullName = findNameInRequest(request) ?: ""
        return if (user.firstName == "" && user.lastName == "" && fullName.isNotEmpty()) {
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

            val subscriptionForm = fillSubscriptionForm()

            val model = createModel(
                request,
                "product" to product,
                "article" to article,
                TPL_PARAM_SUBSCRIPTION_FORM_MAPPING to subscriptionForm,
                "userAccessReport" to userAccessReport,
                "errorMessage" to errorMessage
            )
            ModelAndView("productArticle", model)
        } else {
            logger.error("Article $articleSlug not found")
            notFound(request)
        }
    }

    private fun fillSubscriptionForm(): FormMapping<SubscriptionFormData> {
        val subscriptionData = SubscriptionFormData.Empty
        return WebController.subscriptionFormDef.fill(FormData(subscriptionData, ValidationResult.empty))
    }

    private fun findEmailInRequest(request: HttpServletRequest): String? {
        val param = findParamInRequest(request, "email")
        return param?.replace(' ', '+') ?: param // support of emails containing '+'
    }

    private fun findNameInRequest(request: HttpServletRequest): String? = findParamInRequest(request, "name")

    private fun findParamInRequest(request: HttpServletRequest, paramName: String): String? {
        val value = request.getParameter(paramName)
        return if (value != null && value.isNotEmpty()) {
            value
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
        try {
            val productAuthor = userService.findById(product.createdBy)
            if (productAuthor != null) {
                if (productAuthor.email.isNotEmpty()) {
                    val subject =
                        normalizationHelper.removeDiacriticalMarks("User ${user.firstName} ${user.lastName} downloaded ${product.title}")
                    val body =
                        normalizationHelper.removeDiacriticalMarks(
                            "User ${user.firstName} ${user.lastName}/${user.email} " +
                                    "has downloaded product ${product.title}."
                        )
                    mailSender.sendMailWithText(productAuthor.email, subject, body)
                }
            }
        } catch (ex: Exception) {
            logger.error("Error while sending product downloaded notification: ${ex.message}", ex)
        }
    }

    private fun subscriptionFormResponse(
        formData: FormData<SubscriptionFormData>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val filledFormData = WebController.subscriptionFormDef.fill(formData)
        val model = createModel(request, TPL_PARAM_SUBSCRIPTION_FORM_MAPPING to filledFormData)
        return ajaxResponse(ModelAndView("mailing/subscriptionFormContent", model))
    }

    companion object {
        val subscriptionFormDef: FormMapping<SubscriptionFormData> = SubscriptionForm.definition
        private const val TPL_PARAM_SUBSCRIPTION_FORM_MAPPING = "subscriptionFormMapping"
        const val ORDER_SUB_PATH = "/objednavka"
    }
}
