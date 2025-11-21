package org.xbery.artbeams.common.seo

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.users.domain.User
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Generator for JSON-LD structured data for SEO/GEO optimization.
 * Supports Schema.org vocabularies for articles, authors, breadcrumbs, and website metadata.
 *
 * @author Radek Beran
 */
object StructuredDataGenerator {

    /**
     * Generates JSON-LD structured data for a blog article.
     * Implements Schema.org BlogPosting type for optimal GEO (Generative Engine Optimization).
     *
     * @param article The article to generate structured data for
     * @param author The article author (optional)
     * @param categories List of categories the article belongs to
     * @param siteUrl Base URL of the website (e.g., "https://artbeams.com")
     * @param siteName Name of the website
     * @param logoUrl Full URL to the website logo
     * @return JSON-LD string ready to be embedded in a script tag
     */
    fun generateArticleJsonLd(
        article: Article,
        author: User?,
        categories: List<Category>,
        siteUrl: String,
        siteName: String,
        logoUrl: String
    ): String {
        val isoFormatter = DateTimeFormatter.ISO_INSTANT
        val wordCount = countWords(article.body)

        return """
{
  "@context": "https://schema.org",
  "@type": "BlogPosting",
  "headline": "${escapeJson(article.title)}",
  "description": "${escapeJson(article.perex)}",
  ${if (article.image != null) "\"image\": \"$siteUrl/image/${article.image}?w=1200&h=630\"," else ""}
  "datePublished": "${article.validity.validFrom.format(isoFormatter)}",
  "dateModified": "${article.common.modified.format(isoFormatter)}",
  "author": {
    "@type": "Person",
    "name": "${escapeJson(author?.fullName ?: author?.login ?: "Anonymous")}",
    "url": "$siteUrl/autor/${author?.id ?: ""}"
  },
  "publisher": {
    "@type": "Organization",
    "name": "${escapeJson(siteName)}",
    "logo": {
      "@type": "ImageObject",
      "url": "$logoUrl"
    }
  },
  "mainEntityOfPage": {
    "@type": "WebPage",
    "@id": "$siteUrl/${article.slug}"
  },
  ${if (categories.isNotEmpty()) "\"articleSection\": \"${escapeJson(categories.first().title)}\"," else ""}
  ${if (article.keywords.isNotBlank()) "\"keywords\": \"${escapeJson(article.keywords)}\"," else ""}
  "wordCount": $wordCount,
  "inLanguage": "cs-CZ"
}
        """.trimIndent()
    }

    /**
     * Generates JSON-LD breadcrumb structured data.
     * Helps search engines and LLMs understand site hierarchy.
     *
     * @param items List of breadcrumb items as (name, url) pairs
     * @return JSON-LD string for breadcrumb navigation
     */
    fun generateBreadcrumbJsonLd(items: List<Pair<String, String>>): String {
        val itemListElements = items.mapIndexed { index, (name, url) ->
            """
  {
    "@type": "ListItem",
    "position": ${index + 1},
    "name": "${escapeJson(name)}",
    "item": "$url"
  }""".trimIndent()
        }.joinToString(",\n")

        return """
{
  "@context": "https://schema.org",
  "@type": "BreadcrumbList",
  "itemListElement": [
$itemListElements
  ]
}
        """.trimIndent()
    }

    /**
     * Generates JSON-LD for the website entity.
     * Should be included on the homepage and optionally on other pages.
     *
     * @param siteName Name of the website
     * @param siteUrl Base URL of the website
     * @param description Website description
     * @param logoUrl Full URL to the website logo
     * @param searchUrl Optional search URL template (e.g., "/hledani?q={search_term_string}")
     * @return JSON-LD string for website metadata
     */
    fun generateWebsiteJsonLd(
        siteName: String,
        siteUrl: String,
        description: String,
        logoUrl: String,
        searchUrl: String? = null
    ): String {
        val searchAction = if (searchUrl != null) {
            """,
  "potentialAction": {
    "@type": "SearchAction",
    "target": {
      "@type": "EntryPoint",
      "urlTemplate": "$siteUrl$searchUrl"
    },
    "query-input": "required name=search_term_string"
  }"""
        } else {
            ""
        }

        return """
{
  "@context": "https://schema.org",
  "@type": "WebSite",
  "name": "${escapeJson(siteName)}",
  "url": "$siteUrl",
  "description": "${escapeJson(description)}"$searchAction
}
        """.trimIndent()
    }

    /**
     * Generates JSON-LD for an organization.
     * Useful for about pages and footer information.
     *
     * @param name Organization name
     * @param url Organization website URL
     * @param logoUrl Full URL to the organization logo
     * @param description Organization description
     * @param foundingDate Optional founding date (ISO format)
     * @return JSON-LD string for organization metadata
     */
    fun generateOrganizationJsonLd(
        name: String,
        url: String,
        logoUrl: String,
        description: String,
        foundingDate: String? = null
    ): String {
        val foundingDateField = if (foundingDate != null) {
            """,
  "foundingDate": "$foundingDate""""
        } else {
            ""
        }

        return """
{
  "@context": "https://schema.org",
  "@type": "Organization",
  "name": "${escapeJson(name)}",
  "url": "$url",
  "logo": "$logoUrl",
  "description": "${escapeJson(description)}"$foundingDateField
}
        """.trimIndent()
    }

    /**
     * Generates JSON-LD for a person (author profile).
     * Enhances author credibility for GEO.
     *
     * @param user The user/author
     * @param authorUrl Full URL to the author's profile page
     * @param bio Optional author biography
     * @param organizationName Optional organization the author works for
     * @param organizationUrl Optional organization URL
     * @param socialLinks Optional map of social media platform to URL (e.g., "Twitter" to "https://twitter.com/...")
     * @return JSON-LD string for person/author metadata
     */
    fun generatePersonJsonLd(
        user: User,
        authorUrl: String,
        bio: String? = null,
        organizationName: String? = null,
        organizationUrl: String? = null,
        socialLinks: List<String>? = null
    ): String {
        val bioField = if (bio != null) {
            """,
  "description": "${escapeJson(bio)}""""
        } else {
            ""
        }

        val worksForField = if (organizationName != null) {
            """,
  "worksFor": {
    "@type": "Organization",
    "name": "${escapeJson(organizationName)}"${if (organizationUrl != null) ",\n    \"url\": \"$organizationUrl\"" else ""}
  }"""
        } else {
            ""
        }

        val sameAsField = if (socialLinks != null && socialLinks.isNotEmpty()) {
            val links = socialLinks.joinToString(",\n    ") { "\"$it\"" }
            """,
  "sameAs": [
    $links
  ]"""
        } else {
            ""
        }

        return """
{
  "@context": "https://schema.org",
  "@type": "Person",
  "name": "${escapeJson(user.fullName)}",
  "url": "$authorUrl"$bioField$worksForField$sameAsField
}
        """.trimIndent()
    }

    /**
     * Generates JSON-LD for FAQ structured data.
     * Useful for articles that contain question-answer pairs.
     *
     * @param faqs List of (question, answer) pairs
     * @return JSON-LD string for FAQ page
     */
    fun generateFaqJsonLd(faqs: List<Pair<String, String>>): String {
        val faqItems = faqs.joinToString(",\n") { (question, answer) ->
            """
  {
    "@type": "Question",
    "name": "${escapeJson(question)}",
    "acceptedAnswer": {
      "@type": "Answer",
      "text": "${escapeJson(answer)}"
    }
  }""".trimIndent()
        }

        return """
{
  "@context": "https://schema.org",
  "@type": "FAQPage",
  "mainEntity": [
$faqItems
  ]
}
        """.trimIndent()
    }

    /**
     * Escapes special characters in JSON strings.
     */
    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\u000C", "\\f")
    }

    /**
     * Counts words in HTML content by stripping tags first.
     */
    private fun countWords(html: String): Int {
        return html
            .replace(Regex("<[^>]*>"), " ") // Remove HTML tags
            .replace(Regex("&[^;]+;"), " ") // Remove HTML entities
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .size
    }
}
