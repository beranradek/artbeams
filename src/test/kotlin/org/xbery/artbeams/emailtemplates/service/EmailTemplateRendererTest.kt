package org.xbery.artbeams.emailtemplates.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow

class EmailTemplateRendererTest : StringSpec({
    "escapes values inserted into HTML" {
        EmailTemplateRenderer.renderHtml("<p>{{name}}</p>", mapOf("name" to "<Radek & Amy>")) shouldBe "<p>&lt;Radek &amp; Amy&gt;</p>"
    }
    "keeps subject values as plain text" {
        EmailTemplateRenderer.renderSubject("Hello {{name}}", mapOf("name" to "Radek")) shouldBe "Hello Radek"
    }
    "rejects an undefined placeholder" {
        shouldThrow<IllegalArgumentException> { EmailTemplateRenderer.renderHtml("{{missing}}", emptyMap()) }
    }
})
