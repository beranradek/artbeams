package org.xbery.artbeams.web

import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.service.CategoryService
import org.xbery.artbeams.articles.repository.ArticleCategoryRepository
import org.xbery.artbeams.products.service.ProductService
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.courses.service.CourseService
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import java.time.Instant

class WebControllerArticleAliasTest : StringSpec({

    "alias path uses findBySlug and checks course access" {
        val articleService = mockk<ArticleService>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val categoryService = mockk<CategoryService>(relaxed = true)
        val articleCategoryRepository = mockk<ArticleCategoryRepository>(relaxed = true)
        val productService = mockk<ProductService>(relaxed = true)
        val commentService = mockk<CommentService>(relaxed = true)
        val components = mockk<ControllerComponents>(relaxed = true)
        val request = mockk<HttpServletRequest>(relaxed = true)

        // prepare article with courseId
        val article = Article.Empty.copy(courseId = "c-1", slug = "a-slug", draft = false)
        every { request.requestURI } returns "/a/a-slug"
        every { articleService.findBySlug("a-slug") } returns article

        // prepare logged user
        val now = Instant.now()
        val userAttrs = AssetAttributes("u-1", now, "u-1", now, "u-1")
        val user = org.xbery.artbeams.users.domain.User(userAttrs, "u1", "pwd", "First", "Last", "a@b", emptyList())
        every { components.getLoggedUser(request) } returns user

        // courseService returns no courses (user has no access)
        every { courseService.findCoursesForUser(user.common.id) } returns emptyList()

        val controller = WebController(articleService, categoryService, articleCategoryRepository, productService, commentService, courseService, components, mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))

        val result = controller.article(request, "a-slug")
        // should return 404 view
        val mv = result as ModelAndView
        mv.viewName shouldBe "error404"

        verify { articleService.findBySlug("a-slug") }
        verify { courseService.findCoursesForUser(user.common.id) }
    }

    "public path uses findBySlugPublic and does not check courses" {
        val articleService = mockk<ArticleService>(relaxed = true)
        val courseService = mockk<CourseService>(relaxed = true)
        val categoryService = mockk<CategoryService>(relaxed = true)
        val articleCategoryRepository = mockk<ArticleCategoryRepository>(relaxed = true)
        val productService = mockk<ProductService>(relaxed = true)
        val commentService = mockk<CommentService>(relaxed = true)
        val components = mockk<ControllerComponents>(relaxed = true)
        val request = mockk<HttpServletRequest>(relaxed = true)

        val article = Article.Empty.copy(courseId = null, slug = "p-slug", draft = false)
        every { request.requestURI } returns "/p-slug"
        every { articleService.findBySlugPublic("p-slug") } returns article

        val controller = WebController(articleService, categoryService, articleCategoryRepository, productService, commentService, courseService, components, mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))
        val result = controller.article(request, "p-slug")

        // should have invoked public lookup
        verify { articleService.findBySlugPublic("p-slug") }
        // course service must not be invoked for public path
        verify(exactly = 0) { courseService.findCoursesForUser(any()) }
    }

})
