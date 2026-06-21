package org.xbery.artbeams.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.prices.domain.Price
import org.xbery.artbeams.products.domain.Product
import java.time.Instant
import jakarta.servlet.http.HttpServletRequest

class FreeProductControllerAuthTest :
    StringSpec({

        "renderProductArticle returns notFound when article service returns null" {
            val articleService = mockk<ArticleService>()
            val productService = mockk<org.xbery.artbeams.products.service.ProductService>()
            val components = mockk<ControllerComponents>(relaxed = true)
            val request = mockk<HttpServletRequest>(relaxed = true)

            val product = Product(AssetAttributes.EMPTY, "p1", "title", null, null, null, null, null, null, Price.ZERO, null, null)
            every { productService.findBySlug("p1") } returns product
            every { articleService.findBySlugPublic("p1") } returns null

            val controller = FreeProductController(
                components,
                articleService,
                productService,
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true)
            )

            val res = controller.productDetail(request, "p1")
            // BaseController.notFound returns ModelAndView with viewName "error404"
            (res as org.springframework.web.servlet.ModelAndView).viewName shouldBe "error404"
            verify { articleService.findBySlugPublic("p1") }
        }

        "renderProductArticle returns ModelAndView when article exists" {
            val articleService = mockk<ArticleService>()
            val productService = mockk<org.xbery.artbeams.products.service.ProductService>()
            val components = mockk<ControllerComponents>(relaxed = true)
            val request = mockk<HttpServletRequest>(relaxed = true)

            val now = Instant.now()
            val product = Product(AssetAttributes.EMPTY, "p1", "title", null, null, null, null, null, null, Price.ZERO, null, null)
            every { productService.findBySlug("p1") } returns product

            val article =
                Article(
                    AssetAttributes("a", now, "u", now, "u"),
                    Validity.Empty,
                    null,
                    "p1",
                    "t",
                    null,
                    "p",
                    "",
                    "",
                    "",
                    true,
                    false,
                    "md",
                    null,
                    null
                )
            every { articleService.findBySlugPublic("p1") } returns article

            val controller = FreeProductController(
                components,
                articleService,
                productService,
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true)
            )

            val res = controller.productDetail(request, "p1")
            (res as org.springframework.web.servlet.ModelAndView).viewName shouldBe "productArticle"
            verify { articleService.findBySlugPublic("p1") }
        }

    })
