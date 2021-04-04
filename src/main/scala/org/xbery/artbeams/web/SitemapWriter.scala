package org.xbery.artbeams.web

import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.categories.service.CategoryService
import org.xbery.artbeams.products.service.ProductService

/**
  * Sitemap generating logic.
  * @author Radek Beran
  */
trait SitemapWriter {

  private lazy val SitemapDateFormat = new SimpleDateFormat("YYYY-MM-dd")

  def articleRepository: ArticleRepository
  def categoryService: CategoryService
  def productService: ProductService

  def writeSitemap(urlBase: String, writer: PrintWriter): Unit = {
    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    writer.println("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">")

    // Homepage
    writer.println(buildUrl(urlBase, None, writer))

    // Category pages
    val categories = categoryService.findCategories()
    for (category <- categories) {
      writer.println(buildUrl(urlBase + "/kategorie/" + category.slug, None, writer))
    }

    // Articles
    val articles = articleRepository.findLatest(Int.MaxValue)
    for (article <- articles) {
      writer.println(buildUrl(urlBase + "/" + article.slug, Option(article.modified), writer))
    }

    // Product pages
    val products = productService.findProducts()
    for (product <- products) {
      writer.println(buildUrl(urlBase + "/produkt/" + product.slug, None, writer))
    }

    writer.println("</urlset>")
  }

  private def buildUrl(url: String, modifiedOpt: Option[Instant], writer: PrintWriter): String = {
    val sb = new StringBuilder()
    sb ++= "  <url>"
    sb ++= "<loc>" + url + "</loc>"
    modifiedOpt map { modified =>
      sb ++= "<lastmod>" + SitemapDateFormat.format(new Date(modified.toEpochMilli)) + "</lastmod>"
    }
    sb ++= "<changefreq>daily</changefreq><priority>0.8</priority>"
    sb ++= "</url>"
    sb.toString()
  }
}
