package org.xbery.artbeams.web

import net.formio.FormData
import net.formio.validation.ValidationResult
import org.apache.commons.io.IOUtils
import org.springframework.core.io.ResourceLoader
import org.springframework.http.{CacheControl, HttpHeaders}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable}
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.service.CategoryService
import org.xbery.artbeams.comments.controller.CommentController.commentFormDef
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.async.Awaits
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.products.service.ProductService

import java.util
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import scala.concurrent.{ExecutionContext, Future}

/**
  * Common web routes.
  * @author Radek Beran
  */
@Controller
class WebController @Inject()(
  val articleService: ArticleService,
  val categoryService: CategoryService,
  val productService: ProductService,
  val commentService: CommentService,
  controllerComponents: ControllerComponents,
  resourceLoader: ResourceLoader,
  implicit val ec: ExecutionContext)
  extends BaseController(controllerComponents) with SitemapWriter {

  // TODO RBe: Constants from config
  private lazy val ArticlesPerPageLimit = 10
  private lazy val SearchLimit = 20
  private lazy val LatestArticlesSidebarLimit = 5
  private var searchCount: Int = 0

  @GetMapping(Array("/"))
  def homepage(request: HttpServletRequest): Any = {
    // Articles in main blog area
    val fArticles = Future(articleService.findLatest(ArticlesPerPageLimit))
    val fUserAccessReport = Future(controllerComponents.userAccessService.getUserAccessReport(request))
    val fModel = for {
      articles <- fArticles
      userAccessReport <- fUserAccessReport
    } yield {
      createBlogModel(request,
        "articles" -> articles,
        "showHeadline" -> true,
        "userAccessReport" -> userAccessReport)
    }
    val model = Awaits.result(fModel)
    new ModelAndView("homepage", model)
  }

  @GetMapping(Array("/kategorie/{slug}"))
  def category(request: HttpServletRequest, @PathVariable("slug") slug: String): Any = {
    val categoryOpt = categoryService.findBySlug(slug)
    categoryOpt match {
      case Some(category) =>
        // Articles in category
        val articles = articleService.findByCategoryId(category.id, ArticlesPerPageLimit)
        val model = createBlogModel(request,
          "category" -> category,
          "articles" -> articles)
        new ModelAndView("category", model)
      case _ =>
        notFound()
    }
  }

  @GetMapping(Array("/robots", "/robots.txt", "/robot.txt"))
  def robots(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val resource = resourceLoader.getResource("classpath:robots.txt")
    val fileStream = resource.getInputStream()
    try {
      val cacheControl = CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic()
      response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.getHeaderValue())
      response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=robots.txt")
      response.setContentType("text/plain")
      IOUtils.copy(fileStream, response.getOutputStream())
      response.flushBuffer()
    } finally {
      if (fileStream != null) {
        fileStream.close()
      }
    }
  }

  @GetMapping(Array("/sitemap.xml"))
  def sitemap(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val cacheControl = CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic()
    response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.getHeaderValue())
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=sitemap.xml")
    response.setContentType("application/xml")
    val writer = response.getWriter()
    try {
      writeSitemap(this.getUrlBase(request), writer)
    } finally {
      if (writer != null) {
        writer.close()
      }
    }
  }

  /**
    * GET article detail.
    */
  @GetMapping(Array("/{slug}"))
  def article(request: HttpServletRequest, @PathVariable("slug") slug: String): Any = {
    val articleOpt = articleService.findBySlug(slug)
    articleOpt match {
      case Some(article) =>
        // Logs user access. Checks user device capabilities.
        val entityKey = EntityKey.fromClassAndId(classOf[Article], article.id)
        val fUserAccessReport = Future(controllerComponents.userAccessService.saveUserAccess(entityKey, request))

        // Find count of accesses for the article
        val fCountOfVisits = Future(controllerComponents.userAccessService.findCountOfVisits(entityKey))

        val fCommentsWithForm = Future(if (article.showOnBlog) {
          val newComment = Comment.Empty.toEdited().copy(entityId = article.id)
          val comments = commentService.findByEntityId(article.id)
          val commentForm = commentFormDef.fill(new FormData(newComment, ValidationResult.empty))
          (comments, Some(commentForm))
        } else {
          (Seq.empty[Comment], None)
        })

        val fModel = for {
          userAccessReport <- fUserAccessReport
          countOfVisits <- fCountOfVisits
          commentsWithForm <- fCommentsWithForm
        } yield {
          val (comments, commentFormOpt) = commentsWithForm
          createBlogModel(
            request,
            "article" -> article,
            "comments" -> comments,
            "commentForm" -> commentFormOpt.orNull,
            "userAccessReport" -> userAccessReport,
            "countOfVisits" -> countOfVisits,
          )
        }
        val model = Awaits.result(fModel)
        new ModelAndView("article", model)
      case _ =>
        notFound()
    }
  }

  @GetMapping(Array("/search"))
  def search(request: HttpServletRequest): Any = {
    try {
      searchCount = searchCount + 1
      if (searchCount > 1) {
        new ModelAndView("searchOverloaded", createBlogModel(request))
      } else {
        val query = request.getParameter("query")
        val articles = if (query == null || query.trim().length() < 2) {
          Seq.empty[Article]
        } else {
          articleService.findByQuery(query, SearchLimit)
        }
        val model = createBlogModel(request,
          "query" -> query,
          "articles" -> articles)
        new ModelAndView("search", model)
      }
    } finally {
      searchCount = searchCount - 1
    }
  }

  private def createBlogModel(request: HttpServletRequest, args: (String, Any)*): util.Map[String, Any] = {
    val model = createModel(request, args: _*)
    appendSidebarData(model)
  }

  private def appendSidebarData(model: util.Map[String, Any]): util.Map[String, Any] = {
    val fLatestArticles = Future(articleService.findLatest(LatestArticlesSidebarLimit))
    val fArticleCategories = Future(categoryService.findCategories())
    val fModel = for {
      latestArticles <- fLatestArticles
      articleCategories <- fArticleCategories
    } yield {
      model.put("latestArticles", latestArticles)
      model.put("articleCategories", articleCategories)
      model
    }
    Awaits.result(fModel)
  }
}
