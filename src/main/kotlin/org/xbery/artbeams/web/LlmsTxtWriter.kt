package org.xbery.artbeams.web

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.service.CategoryService
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.service.ProductService
import java.io.PrintWriter

/**
 * llms.txt file generating logic.
 * The llms.txt file is an emerging standard that provides AI agents with a curated,
 * prioritized map of the most important content on a website.
 *
 * @see <a href="https://llmstxt.org/">llms.txt specification</a>
 * @author Radek Beran
 */
interface LlmsTxtWriter {
    fun articleService(): ArticleService

    fun categoryService(): CategoryService

    fun productService(): ProductService

    fun writeLlmsTxt(urlBase: String, writer: PrintWriter) {
        // H1 with site name (required)
        writer.println("# ArtBeams")
        writer.println()

        // Blockquote with brief description (recommended)
        writer.println("> ArtBeams is an open-source content management system (CMS) for blogs with administration interface.")
        writer.println("> Built with Kotlin and Spring Boot, it features article management, category-based organization,")
        writer.println("> user authentication, e-commerce capabilities for digital products (ebooks), and integrations")
        writer.println("> with Google Docs, Evernote, and email services.")
        writer.println()

        // Core pages section
        writer.println("## Core Pages")
        writer.println("- [Homepage]($urlBase): Main blog homepage with latest articles")
        writer.println("- [Search]($urlBase/search): Search articles by keywords")
        writer.println()

        // Categories section
        val categories: List<Category> = categoryService().findCategories()
        if (categories.isNotEmpty()) {
            writer.println("## Article Categories")
            for (category in categories) {
                val description = if (!category.description.isNullOrBlank()) {
                    ": ${category.description}"
                } else {
                    ""
                }
                writer.println("- [${category.title}]($urlBase/kategorie/${category.slug})$description")
            }
            writer.println()
        }

        // Products section (ebooks)
        val products: List<Product> = productService().findProducts()
        if (products.isNotEmpty()) {
            writer.println("## Products (Ebooks)")
            for (product in products) {
                val subtitle = if (!product.subtitle.isNullOrBlank()) {
                    " - ${product.subtitle}"
                } else {
                    ""
                }
                writer.println("- [${product.title}]($urlBase/produkt/${product.slug})$subtitle")
            }
            writer.println()
        }

        // Blog articles section - limit to most recent articles
        val articles: List<Article> = articleService().findLatest(20)
        if (articles.isNotEmpty()) {
            writer.println("## Latest Blog Articles")
            for (article in articles) {
                val perex = if (!article.perex.isNullOrBlank()) {
                    // Truncate perex to first sentence or 150 chars
                    val truncated = article.perex.take(150)
                    val endIndex = truncated.indexOfAny(listOf(". ", ".\n", ".\""))
                    if (endIndex > 0) {
                        ": ${truncated.substring(0, endIndex + 1)}"
                    } else {
                        ": $truncated..."
                    }
                } else {
                    ""
                }
                writer.println("- [${article.title}]($urlBase/${article.slug})$perex")
            }
            writer.println()
        }

        // Optional section - additional resources
        writer.println("## Optional")
        writer.println("- [Sitemap]($urlBase/sitemap.xml): Complete XML sitemap of all pages")
        writer.println("- [Robots.txt]($urlBase/robots.txt): Robots exclusion standard file")
    }
}
