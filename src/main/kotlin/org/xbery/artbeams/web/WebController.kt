package org.xbery.artbeams.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.formio.FormData
import net.formio.FormMapping
import net.formio.validation.ValidationResult
import org.apache.commons.io.IOUtils
import org.springframework.core.io.ResourceLoader
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.service.CategoryService
import org.xbery.artbeams.comments.controller.CommentController
import org.xbery.artbeams.comments.controller.CommentForm
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.mailing.controller.SubscriptionForm
import org.xbery.artbeams.mailing.controller.SubscriptionFormData
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.users.password.setup.service.PasswordSetupMailer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Common web routes.
 * @author Radek Beran
 */
@Controller
open class WebController(
    val articleService: ArticleService,
    private val categoryService: CategoryService,
    val productService: ProductService,
    val commentService: CommentService,
    val controllerComponents: ControllerComponents,
    val resourceLoader: ResourceLoader,
    val passwordSetupMailer: PasswordSetupMailer
) : BaseController(controllerComponents), SitemapWriter {

    override fun articleService(): ArticleService = articleService
    override fun categoryService(): CategoryService = categoryService
    override fun productService(): ProductService = productService

    private val ArticlesPerPageLimit: Int = 20
    private val SearchLimit: Int = 20
    private val LatestArticlesSidebarLimit: Int = 5
    private var searchCount: Int = 0

    @GetMapping("/")
    fun homepage(request: HttpServletRequest): Any {
        //passwordSetupMailer.sendPasswordSetupMail("beran.radek@seznam.cz")
        //val fArticles = CompletableFuture.supplyAsync { articleService.findLatest(ArticlesPerPageLimit) }
        //val fUserAccessReport =
        //    CompletableFuture.supplyAsync { controllerComponents.userAccessService.getUserAccessReport(request) }
        //Stream.of(fArticles, fUserAccessReport).map(CompletableFuture::join)
        //CompletableFuture.allOf(fArticles, fUserAccessReport).join()

        val articles = articleService.findLatest(ArticlesPerPageLimit)
        val userAccessReport = controllerComponents.userAccessService.getUserAccessReport(request)
        val model = createBlogModel(
            request,
            FormData(SubscriptionFormData.Empty, ValidationResult.empty),
            "articles" to articles,
            "showHeadline" to true,
            "userAccessReport" to userAccessReport
        )
        return ModelAndView("homepage", model)
    }

    @GetMapping("/kategorie/{slug}")
    fun category(request: HttpServletRequest, @PathVariable slug: String?): Any {
        return if (slug != null) {
            val category = categoryService.findBySlug(slug)
            if (category != null) {
                val articles = articleService.findByCategoryId(category.id, ArticlesPerPageLimit)
                val model = createBlogModel(
                    request,
                    FormData(SubscriptionFormData.Empty, ValidationResult.empty),
                    "category" to category,
                    "articles" to articles
                )
                ModelAndView("category", model)
            } else {
                notFound(request)
            }
        } else {
            notFound(request)
        }
    }

    @GetMapping(value = ["/robots", "/robots.txt", "/robot.txt"])
    fun robots(request: HttpServletRequest, response: HttpServletResponse) {
        resourceLoader.getResource("classpath:robots.txt").inputStream.use { fileStream ->
            val cacheControl: CacheControl =
                CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic()
            response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.headerValue)
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=robots.txt")
            response.contentType = "text/plain"
            IOUtils.copy(fileStream, response.outputStream)
            response.flushBuffer()
        }
    }

    @GetMapping("/sitemap.xml")
    fun sitemap(request: HttpServletRequest, response: HttpServletResponse) {
        val cacheControl: CacheControl = CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic()
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.headerValue)
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=sitemap.xml")
        response.contentType = "application/xml"
        response.writer.use { writer ->
            writeSitemap(this.getUrlBase(request), writer)
        }
    }

    /**
     * GET article detail.
     */
    @GetMapping("/{slug}")
    fun article(request: HttpServletRequest, @PathVariable slug: String?): Any {
        return if (slug != null) {
            val article = articleService.findBySlug(slug)
            if (article != null) {
                val entityKey = EntityKey.fromClassAndId(Article::class.java, article.id)
                val fUserAccessReport = CompletableFuture.supplyAsync {
                    controllerComponents.userAccessService.saveUserAccess(
                        entityKey,
                        request
                    )
                }
                val fCountOfVisits =
                    CompletableFuture.supplyAsync { controllerComponents.userAccessService.findCountOfVisits(entityKey) }
                val fCommentsWithForm = CompletableFuture.supplyAsync {
                    if (article.showOnBlog) {
                        val newComment = Comment.Empty.toEdited().copy(entityId = article.id)
                        val comments = commentService.findByEntityId(article.id)
                        val commentForm = commentFormDef.fill(FormData(newComment, ValidationResult.empty))
                        Pair<List<Comment>, FormMapping<EditedComment>?>(comments, commentForm)
                    } else {
                        Pair<List<Comment>, FormMapping<EditedComment>?>(listOf(), null)
                    }
                }
                CompletableFuture.allOf(fUserAccessReport, fCountOfVisits, fCommentsWithForm).join()
                val commentsWithForm = fCommentsWithForm.get()
                val model = createBlogModel(
                    request,
                    FormData(SubscriptionFormData.Empty, ValidationResult.empty),
                    "article" to article,
                    "comments" to commentsWithForm.first,
                    CommentController.TPL_PARAM_COMMENT_FORM to commentsWithForm.second,
                    "userAccessReport" to fUserAccessReport.get(),
                    "countOfVisits" to fCountOfVisits.get()
                )
                ModelAndView("article", model)
            } else {
                notFound(request)
            }
        } else {
            notFound(request)
        }
    }

    @GetMapping("/search")
    fun search(request: HttpServletRequest): Any {
        return try {
            searchCount += 1
            if (searchCount > 1) {
                ModelAndView("searchOverloaded", createBlogModel(request, FormData(SubscriptionFormData.Empty, ValidationResult.empty)))
            } else {
                val query: String? = request.getParameter("query")
                val articles = if (query == null || query.trim().length < 2) {
                    listOf()
                } else {
                    articleService.findByQuery(query, SearchLimit)
                }
                val model = createBlogModel(
                    request,
                    FormData(SubscriptionFormData.Empty, ValidationResult.empty),
                    "query" to query,
                    "articles" to articles
                )
                ModelAndView("search", model)
            }
        } finally {
            searchCount -= 1
        }
    }

    private fun createBlogModel(request: HttpServletRequest, subscriptionFormData: FormData<SubscriptionFormData>, vararg args: Pair<String, Any?>): Map<String, Any?> {
        val model = createModel(request, *args)
        return appendSidebarData(subscriptionFormData, model)
    }

    private fun appendSidebarData(subscriptionFormData: FormData<SubscriptionFormData>, model: MutableMap<String, Any?>): MutableMap<String, Any?> {
        val fLatestArticles = CompletableFuture.supplyAsync { articleService.findLatest(LatestArticlesSidebarLimit) }
        val fArticleCategories = CompletableFuture.supplyAsync { categoryService.findCategories() }
        CompletableFuture.allOf(fLatestArticles, fArticleCategories).join()
        model["latestArticles"] = fLatestArticles.get()
        model["articleCategories"] = fArticleCategories.get()

        val subscriptionForm = subscriptionFormDef.fill(subscriptionFormData)
        model["subscriptionFormMapping"] = subscriptionForm
        return model
    }

    companion object {
        val commentFormDef: FormMapping<EditedComment> = CommentForm.definition
        val subscriptionFormDef: FormMapping<SubscriptionFormData> = SubscriptionForm.definition
    }
}
