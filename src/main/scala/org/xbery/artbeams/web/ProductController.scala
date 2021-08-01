package org.xbery.artbeams.web

import java.io.ByteArrayOutputStream
import java.time.Instant
import java.util.Calendar

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import org.apache.pdfbox.pdmodel.PDDocument
import org.slf4j.LoggerFactory
import org.springframework.http.{CacheControl, HttpHeaders, MediaType, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable}
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.common.mailer.service.Mailer
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.media.domain.FileData
import org.xbery.artbeams.media.repository.MediaRepository
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.service.{UserService, UserSubscriptionService}

/**
  * Product routes.
  * @author Radek Beran
  */
@Controller
class ProductController @Inject()(
  controllerComponents: ControllerComponents,
  articleService: ArticleService,
  productService: ProductService,
  userSubscriptionService: UserSubscriptionService,
  userService: UserService,
  orderService: OrderService,
  mediaRepository: MediaRepository,
  mailer: Mailer
) extends BaseController(controllerComponents) {

  private lazy val logger = LoggerFactory.getLogger(this.getClass)
  private lazy val normalizationHelper = new NormalizationHelper()

  @GetMapping(Array("/produkt/{slug}/potvrzeni"))
  def productConfirmInfo(request: HttpServletRequest, @PathVariable("slug") slug: String): Any = {
    val productOpt = productService.findBySlug(slug)
    productOpt match {
      case Some(product) =>
        val fullNameOpt = findNameInRequest(request)
        val emailOpt = findEmailInRequest(request)
        emailOpt.map { email =>
          userSubscriptionService.subscribe(fullNameOpt, email, product.id)
        }

        renderProductArticle(request, product, product.slug + "-potvrzeni", false)
      case _ =>
        notFound()
    }
  }

  @GetMapping(Array("/produkt/{slug}/odeslano"))
  def productSent(request: HttpServletRequest, @PathVariable("slug") slug: String): Any = {
    val productOpt = productService.findBySlug(slug)
    productOpt match {
      case Some(product) =>
        val fullNameOpt = findNameInRequest(request)
        val emailOpt = findEmailInRequest(request)
        emailOpt.map { email =>
          userSubscriptionService.confirmConsent(fullNameOpt, email, product.id)
        }

        renderProductArticle(request, product, product.slug + "-odeslano", false)
      case _ =>
        notFound()
    }
  }

  // Allow all characters at the end of path (regex addon for filename variable):
  @GetMapping(Array("/produkt/{slug}/download"))
  def findFile(request: HttpServletRequest, @PathVariable("slug") slug: String): ResponseEntity[_] = {
    val productOpt = productService.findBySlug(slug)
    productOpt match {
      case Some(product) if (product.fileName.isDefined) =>
        val emailOpt = findEmailInRequest(request)
        emailOpt match {
          case Some(email)  =>
            // User must exist and confirm the consent before he/she can download the product
            userService.findByEmail(email) match {
              case Some(user) =>
                // Update user with full name from request if it is present (and not set in user entity yet)
                updateUserWithFullName(request, user)
                if (user.consent.isDefined) {
                  orderService.findOrderItemOfUser(user.id, product.id) match {
                    case Some(orderItem) if (orderItem.quantity > 0) =>
                      // An order of the product for given user exists
                      // TODO: In case of paid product, check the order was already paid
                      val productFileName = product.fileName.get
                      mediaRepository.findFile(productFileName, None) match {
                        case Some(fileData) =>
                          val mediaType = fileData.getMediaType()
                          val documentOutputStream = if (MediaType.APPLICATION_PDF.equals(mediaType)) {
                            writeMetadataToPdf(request, product, user, fileData)
                          } else {
                            val os = new ByteArrayOutputStream()
                            os.write(fileData.data)
                            os
                          }

                          orderService.updateOrderItemDownloaded(orderItem.id, Some(Instant.now()))
                          sendProductDownloadedNotification(product, user)

                          ResponseEntity.ok()
                            .contentType(mediaType)
                            .contentLength(documentOutputStream.size())
                            .cacheControl(CacheControl.noStore()) // prevent browsers and proxies to cache the request
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileData.filename)
                            .body(documentOutputStream.toByteArray())
                        case _ =>
                          notFound()
                      }
                    case _ =>
                      unauthorized()
                  }
                } else {
                  unauthorized()
                }
              case _ =>
                unauthorized()
            }
          case _ =>
            unauthorized()
        }
      case _ =>
        notFound()
    }
  }

  /**
    * GET product detail.
    */
  @GetMapping(Array("/produkt/{slug}"))
  def productDetail(request: HttpServletRequest, @PathVariable("slug") slug: String): Any = {
    val productOpt = productService.findBySlug(slug)
    productOpt match {
      case Some(product) =>
        renderProductArticle(request, product, product.slug, true)
      case _ =>
        notFound()
    }
  }

  private def writeMetadataToPdf(request: HttpServletRequest, product: Product, customer: User, fileData: FileData): ByteArrayOutputStream = {
    val pdfDocument = PDDocument.load(fileData.data)
    val pdfOutputStream = new ByteArrayOutputStream()
    try {
      val nowCalendar = Calendar.getInstance()
      val pdfMetadata = pdfDocument.getDocumentInformation()
      val productAuthorOpt = userService.findById(product.common.createdBy)
      pdfMetadata.setAuthor(productAuthorOpt.map(_.fullName).getOrElse(""))
      pdfMetadata.setCreationDate(nowCalendar)
      pdfMetadata.setModificationDate(nowCalendar)
      pdfMetadata.setTitle(product.title)
      val urlBase = getUrlBase(request)
      val customerInfo = customer.email + (if (customer.fullName.isEmpty) "" else " (" + customer.fullName + ")")
      pdfMetadata.setCreator(urlBase + " for " + customerInfo)
      pdfDocument.setDocumentInformation(pdfMetadata)
      pdfDocument.save(pdfOutputStream)
    } finally {
      pdfDocument.close()
    }
    pdfOutputStream
  }

  private def updateUserWithFullName(request: HttpServletRequest, user: User): User = {
    val fullName = findNameInRequest(request)
    if (user.firstName == "" && user.lastName == "" && !fullName.getOrElse("").isEmpty) {
      val (firstName, lastName) = User.namesFromFullName(fullName.getOrElse(""))
      userService.updateUser(user.copy(firstName = firstName, lastName = lastName))
    } else {
      user
    }
  }

  private def renderProductArticle(request: HttpServletRequest, product: Product, articleSlug: String, saveUserAccess: Boolean) = {
    val articleOpt = articleService.findBySlug(articleSlug)
    articleOpt match {
      case Some(article) =>
        // Checks user device capabilities.
        val userAccessReport = if (saveUserAccess) {
          // Logs user access
          val entityKey = EntityKey.fromClassAndId(classOf[Article], article.id)
          controllerComponents.userAccessService.saveUserAccess(entityKey, request)
        } else {
          controllerComponents.userAccessService.getUserAccessReport(request)
        }
        val model = createModel(request,
          "product" -> product,
          "article" -> article,
          "userAccessReport" -> userAccessReport
        )
        new ModelAndView("productArticle", model)
      case None =>
        logger.error(s"Article ${articleSlug} not found")
        notFound()
    }
  }

  private def findEmailInRequest(request: HttpServletRequest): Option[String] = {
    Option(request.getParameter("email")).filter(!_.isEmpty).map(_.replace(' ', '+'))
  }

  private def findNameInRequest(request: HttpServletRequest): Option[String] = {
    Option(request.getParameter("name")).filter(!_.isEmpty)
  }

  /**
    * Sends email notification to product's author that an user has downloaded the product.
    * @param product
    * @param user
    */
  private def sendProductDownloadedNotification(product: Product, user: User): Unit = {
    val productAuthorOpt = userService.findById(product.createdBy)
    if (productAuthorOpt.isDefined) {
      val productAuthor = productAuthorOpt.get
      if (productAuthor.email != null && !productAuthor.email.isEmpty()) {
        val subject = normalizationHelper.removeDiacriticalMarks(s"User ${user.firstName} ${user.lastName} downloaded ${product.title}")
        val body = normalizationHelper.removeDiacriticalMarks(s"User ${user.firstName} ${user.lastName}/${user.email} has downloaded product ${product.title}.")
        mailer.sendMail(subject, body, productAuthor.email)
      }
    }
  }
}
