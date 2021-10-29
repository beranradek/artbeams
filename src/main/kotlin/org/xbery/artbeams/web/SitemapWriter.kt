package org.xbery.artbeams.web

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.service.ArticleService
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.service.CategoryService
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.service.ProductService
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.time.Instant

/**
 * Sitemap generating logic.
 * @author Radek Beran
 */
interface SitemapWriter {
    fun articleService(): ArticleService

    fun categoryService(): CategoryService

    fun productService(): ProductService

    fun writeSitemap(urlBase: String, writer: PrintWriter) {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        writer.println("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">")

        // Homepage URL
        writer.println(buildUrl(urlBase, null))

        // Category URLs
        val categories: List<Category> = categoryService().findCategories()
        for (category in categories) {
            writer.println(
                buildUrl(
                    urlBase + "/kategorie/" + category.slug,
                    null
                )
            )
        }

        // Article URLs
        val articles: List<Article> = articleService().findLatest(Int.MAX_VALUE)
        for (article in articles) {
            writer.println(
                buildUrl(
                    urlBase + "/" + article.slug, article.modified
                )
            )
        }

        // Product URLs
        val products: List<Product> = productService().findProducts()
        for (product in products) {
            writer.println(
                buildUrl(
                    urlBase + "/produkt/" + product.slug,
                    null
                )
            )
        }
        writer.println("</urlset>")
    }

    private fun buildUrl(url: String, modifiedOpt: Instant?): String {
        val sb = StringBuilder()
        sb.append("  <url>")
        sb.append("<loc>" + url + "</loc>")
        modifiedOpt?.let { modified ->
            sb.append("<lastmod>" + SitemapDateFormat.format(java.util.Date(modified.toEpochMilli())) + "</lastmod>")
        }
        sb.append("<changefreq>daily</changefreq><priority>0.8</priority>")
        sb.append("</url>")
        return sb.toString()
    }

    companion object {
        val SitemapDateFormat = SimpleDateFormat("YYYY-MM-dd")
    }
}
