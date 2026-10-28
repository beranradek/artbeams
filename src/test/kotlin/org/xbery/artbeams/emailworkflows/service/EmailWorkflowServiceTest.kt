package org.xbery.artbeams.emailworkflows.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.emailtemplates.domain.EmailTemplate
import org.xbery.artbeams.emailtemplates.repository.EmailTemplateRepository
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRepository
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.service.ProductService

class EmailWorkflowServiceTest : StringSpec({
    "rejects a system template in a product workflow" {
        val workflows = mockk<EmailWorkflowRepository>(relaxed = true)
        val templates = mockk<EmailTemplateRepository>()
        val products = mockk<ProductService>()
        every { products.findProducts() } returns listOf(mockk<Product> { every { id } returns "product-1" })
        every { templates.findById("system-template") } returns EmailTemplate(AssetAttributes.EMPTY, "receipt", "Receipt", "<p>ok</p>", true)

        shouldThrow<IllegalArgumentException> {
            EmailWorkflowService(workflows, templates, products).create("product-1", "Follow-up", true, listOf("system-template"), listOf(0), "admin")
        }

        verify(exactly = 0) { workflows.save(any()) }
    }

    "rejects a delay longer than one year" {
        val service = EmailWorkflowService(mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true))

        shouldThrow<IllegalArgumentException> {
            service.create("product-1", "Follow-up", true, listOf("template-1"), listOf(525_601), "admin")
        }
    }
})
