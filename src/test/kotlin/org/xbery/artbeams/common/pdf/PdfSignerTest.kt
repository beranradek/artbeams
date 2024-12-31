package org.xbery.artbeams.common.pdf

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.apache.pdfbox.Loader
import org.xbery.artbeams.config.repository.TestAppConfig
import java.io.File

class PdfSignerTest : StringSpec({

    "should add user metadata and secure the PDF" {
        // Arrange
        val pdfSigner = PdfSigner(
            TestAppConfig(
                mapOf(
                    "web.baseUrl" to "https://myserver.com"
                )
            )
        )
        val inputFile = File("src/test/resources/test_ebook.pdf")
        val fileData = inputFile.readBytes()
        val productTitle = "Test Product"
        val author = "Test Author"
        val customerEmail = "customer@example.com"
        val customerFullName = "Customer Name"
        val orderNumber = "123456"

        // Act
        val outputStream =
            pdfSigner.addUserMetadataToPdf(fileData, productTitle, author, customerEmail, customerFullName, orderNumber)
        val outputFile = File("src/test/resources/test_ebook_signed.pdf")
        outputFile.writeBytes(outputStream.toByteArray())

        try {
            // Assert
            val pdfDocument = Loader.loadPDF(outputFile)
            pdfDocument.use { document ->
                val metadata = document.documentInformation
                metadata.title shouldBe productTitle
                metadata.author shouldBe author
                metadata.creator shouldBe "$customerEmail $customerFullName $orderNumber"

                // Check if the document is protected against copying
                val permissions = document.currentAccessPermission
                permissions.canExtractContent() shouldBe false
            }
        } finally {
            // Clean up
            outputFile.delete()
        }
    }
})