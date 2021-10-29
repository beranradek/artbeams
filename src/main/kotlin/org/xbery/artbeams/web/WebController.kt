package org.xbery.artbeams.web

import net.formio.FormData
import net.formio.FormMapping
import net.formio.validation.ValidationResult
import org.apache.commons.io.IOUtils
import org.springframework.core.io.Resource
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
import org.xbery.artbeams.comments.admin.CommentForm
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.antispam.domain.AntispamQuiz
import org.xbery.artbeams.common.antispam.repository.AntispamQuizRepository
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.products.service.ProductService
import java.io.InputStream
import java.io.PrintWriter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Common web routes.
 * @author Radek Beran
 */
@Controller
open class WebController(
    val articleService: ArticleService,
    val categoryService: CategoryService,
    val productService: ProductService,
    val commentService: CommentService,
    val antispamQuizRepository: AntispamQuizRepository,
    val controllerComponents: ControllerComponents,
    val resourceLoader: ResourceLoader
) : BaseController(controllerComponents), SitemapWriter {

    override fun articleService(): ArticleService = articleService
    override fun categoryService(): CategoryService = categoryService
    override fun productService(): ProductService = productService

    private val ArticlesPerPageLimit: Int = 10
    private val SearchLimit: Int = 20
    private val LatestArticlesSidebarLimit: Int = 5
    private var searchCount: Int = 0

    @GetMapping("/")
    fun homepage(request: HttpServletRequest): Any {
        //val fArticles = CompletableFuture.supplyAsync { articleService.findLatest(ArticlesPerPageLimit) }
        //val fUserAccessReport =
        //    CompletableFuture.supplyAsync { controllerComponents.userAccessService.getUserAccessReport(request) }
        //Stream.of(fArticles, fUserAccessReport).map(CompletableFuture::join)
        //CompletableFuture.allOf(fArticles, fUserAccessReport).join()

        val articles = articleService.findLatest(ArticlesPerPageLimit)
        val userAccessReport = controllerComponents.userAccessService.getUserAccessReport(request)
        val model = createBlogModel(
            request,
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
                    "category" to category,
                    "articles" to articles
                )
                ModelAndView("category", model)
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    @GetMapping(value = ["/robots", "/robots.txt", "/robot.txt"])
    fun robots(request: HttpServletRequest, response: HttpServletResponse): Unit {
        val resource: Resource = resourceLoader.getResource("classpath:robots.txt")
        val fileStream: InputStream = resource.inputStream
        try {
            val cacheControl: CacheControl =
                CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic()
            response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.headerValue)
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=robots.txt")
            response.setContentType("text/plain")
            IOUtils.copy(fileStream, response.outputStream)
            response.flushBuffer()
        } finally {
            if (fileStream != null) {
                fileStream.close()
            }
        }
    }

    @GetMapping("/sitemap.xml")
    fun sitemap(request: HttpServletRequest, response: HttpServletResponse): Unit {
        val cacheControl: CacheControl = CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic()
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl.headerValue)
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "filename=sitemap.xml")
        response.setContentType("application/xml")
        val writer: PrintWriter = response.writer
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
                        val antispamQuiz: AntispamQuiz = antispamQuizRepository.findRandom()
                        val newComment: EditedComment = Comment.Empty.toEdited(antispamQuiz.question)
                            .copy(entityId = article.id)
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
                    "article" to article,
                    "comments" to commentsWithForm.first,
                    "commentForm" to commentsWithForm.second,
                    "userAccessReport" to fUserAccessReport.get(),
                    "countOfVisits" to fCountOfVisits.get()
                )
                ModelAndView("article", model)
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    @GetMapping("/search")
    fun search(request: HttpServletRequest): Any {
        return try {
            searchCount = searchCount + 1
            if (searchCount > 1) {
                ModelAndView("searchOverloaded", createBlogModel(request))
            } else {
                val query: String = request.getParameter("query")
                val articles = if (query == null || query.trim().length < 2) {
                    listOf()
                } else {
                    articleService.findByQuery(query, SearchLimit)
                }
                val model = createBlogModel(
                    request,
                    "query" to query,
                    "articles" to articles
                )
                ModelAndView("search", model)
            }
        } finally {
            searchCount = searchCount - 1
        }
    }

    private fun createBlogModel(request: HttpServletRequest, vararg args: Pair<String, Any?>): Map<String, Any?> {
        val model = createModel(request, *args)
        return appendSidebarData(model)
    }

    private fun appendSidebarData(model: MutableMap<String, Any?>): MutableMap<String, Any?> {
        val fLatestArticles = CompletableFuture.supplyAsync { articleService.findLatest(LatestArticlesSidebarLimit) }
        val fArticleCategories = CompletableFuture.supplyAsync { categoryService.findCategories() }
        CompletableFuture.allOf(fLatestArticles, fArticleCategories).join()
        model["latestArticles"] = fLatestArticles.get()
        model["articleCategories"] = fArticleCategories.get()
        return model
    }

    companion object {
        val commentFormDef: FormMapping<EditedComment> = CommentForm.definition
    }
}
