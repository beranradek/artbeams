package org.xbery.artbeams.common.pdf

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDDocumentInformation
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.encryption.AccessPermission
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfig
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

/**
 * Signs PDF document by adding customer's metadata, invisible pixels and applying protection policy.
 */
@Component
class PdfSigner(private val appConfig: AppConfig) {

    /**
     * Adds user metadata to the PDF document and includes an invisible pixel and user information.
     */
    fun addUserMetadataToPdf(
        fileData: ByteArray,
        productTitle: String,
        author: String,
        customerEmail: String,
        customerFullName: String,
        orderNumber: String
    ): ByteArrayOutputStream {
        val pdfDocument = Loader.loadPDF(fileData)
        val pdfOutputStream = ByteArrayOutputStream()
        pdfDocument.use { pdfDoc ->
            addMetadata(pdfDoc, author, productTitle, customerEmail, customerFullName, orderNumber)
            replaceSecondBlankPage(pdfDoc, customerEmail, customerFullName, orderNumber)

            // Add user's email and name to each page
//            for (page in pdfDoc.pages) {
//                addInvisiblePixel(pdfDocument, page)
//                addUserInfoToPage(pdfDocument, page, author, customerEmail)
//            }

            addProtectionPolicy(pdfDoc)
            pdfDoc.save(pdfOutputStream)
        }
        return pdfOutputStream
    }

    private fun addMetadata(
        pdfDoc: PDDocument,
        author: String,
        productTitle: String,
        customerEmail: String,
        customerFullName: String,
        orderNumber: String
    ) {
        val nowCalendar = Calendar.getInstance()
        val pdfMetadata: PDDocumentInformation = pdfDoc.documentInformation
        pdfMetadata.author = author
        pdfMetadata.title = productTitle
        pdfMetadata.creationDate = nowCalendar
        pdfMetadata.modificationDate = nowCalendar
        pdfMetadata.title = productTitle
        pdfMetadata.creator = "$customerEmail $customerFullName $orderNumber"
        pdfDoc.documentInformation = pdfMetadata
    }

    private fun replaceSecondBlankPage(
        document: PDDocument,
        customerEmail: String,
        customerFullName: String,
        orderNumber: String
    ) {
        // Check if the document has at least two pages
        if (document.numberOfPages < 2) {
            throw IllegalArgumentException("The PDF must have at least two pages.")
        }

        // Create a new page with the same size as the second page
        val newPage = PDPage(PDRectangle.A4) // Change A4 to the desired size

        // Load the lock image
        val lockImage = PDImageXObject.createFromByteArray(
            document,
            inputStreamToByteArray(getLockImageStream()), SECURED_LOCK_IMAGE_NAME
        )

        // Create a content stream for the new page
        PDPageContentStream(document, newPage).use { contentStream ->
            // Set margins
            val margin = 50f // Set the desired margin
            val imageWidth = lockImage.width.toFloat()
            val imageHeight = lockImage.height.toFloat()

            // Draw the lock image repeatedly within the margins
            var row = 0
            for (j in margin.toInt() until (newPage.mediaBox.height - 2 * margin).toInt() step imageHeight.toInt()) {
                row++
                if (row < 3) continue // Limit the number of rows
                for (i in margin.toInt() until (newPage.mediaBox.width - 2 * margin).toInt() step imageWidth.toInt()) {
                    contentStream.drawImage(lockImage, i.toFloat(), j.toFloat())
                }
            }

            // Add user email and name at the bottom of the page
            val boldFontStream = getResourceAsStream("OpenSans-Bold.ttf")
            val boldFont = PDType0Font.load(document, boldFontStream)

            val fontStream = getResourceAsStream("OpenSans-Regular.ttf")
            val font = PDType0Font.load(document, fontStream)

            // Define the position and maximum width for the text
            val x = 100f // X position
            var y = 180f // Initial Y position
            val maxWidth = newPage.mediaBox.width - 2 * x // Maximum width based on page margins

            val serverText = "E-book byl zakoupen na serveru " + appConfig.findConfig("web.baseUrl")
            addWrappedText(contentStream, serverText, font, 10f, x, y, maxWidth)
            y -= 20f // Move down for the statement paragraph

            val userInfoText = "Kupující: $customerEmail $customerFullName"
            addWrappedText(contentStream, userInfoText, boldFont, 12f, x, y, maxWidth)
            y -= 20f // Move down for the statement paragraph

            val idText = "ID: $orderNumber"
            addWrappedText(contentStream, idText, boldFont, 12f, x, y, maxWidth)

            // Add the statement paragraph below the user info
            y -= 20f // Move down for the statement paragraph
            val statementText =
                "Prohlášení: Tento materiál je informačním produktem. Jakékoliv šíření nebo poskytování " +
                        "třetím osobám bez souhlasu autora je zakázáno a je porušením autorského zákona, které může být stíháno. " +
                        "Děkuji za pochopení a respektování tohoto sdělení."
            addWrappedText(contentStream, statementText, font, 10f, x, y, maxWidth)
        }

        // Insert the new page as the second page
        document.addPage(newPage)
        document.pages.insertBefore(newPage, document.getPage(1)) // Insert before the current second page

        // Remove the original second blank page (which is now at index 2)
        document.removePage(2)
    }

    private fun addWrappedText(
        contentStream: PDPageContentStream,
        text: String,
        font: PDFont,
        fontSize: Float,
        x: Float,
        y: Float,
        maxWidth: Float
    ) {
        val words = text.split(" ")
        val currentLine = StringBuilder()
        var currentY = y

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testLineWidth = font.getStringWidth(testLine) / 1000 * fontSize

            if (testLineWidth > maxWidth) {
                // Write the current line
                contentStream.beginText()
                contentStream.setFont(font, fontSize)
                contentStream.newLineAtOffset(x, currentY)
                contentStream.showText(currentLine.toString())
                contentStream.endText()

                // Move to the next line
                currentLine.clear().append(word)
                currentY -= fontSize * 1.2f // Adjust line height as needed
            } else {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            }
        }

        // Write any remaining text in the current line
        if (currentLine.isNotEmpty()) {
            contentStream.beginText()
            contentStream.setFont(font, fontSize)
            contentStream.newLineAtOffset(x, currentY)
            contentStream.showText(currentLine.toString())
            contentStream.endText()
        }
    }

    private fun addInvisiblePixel(document: PDDocument, page: PDPage) {
        val contentStream = PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)
        contentStream.setNonStrokingColor(1f, 1f, 1f) // White color for invisible pixel
        contentStream.addRect(0f, 0f, 1f, 1f) // Add a 1x1 pixel rectangle
        contentStream.fill()
        contentStream.close()
    }

    private fun addProtectionPolicy(document: PDDocument) {
        val accessPermission = AccessPermission()
        accessPermission.setCanExtractContent(false)
        accessPermission.setCanModify(false)
        accessPermission.setCanPrint(true)

        // Protection setup (empty user password, but with restrictions applied)
        val spp = StandardProtectionPolicy("owner-password", "", accessPermission)
        spp.encryptionKeyLength = 128 // cypher setup
        spp.permissions = accessPermission

        // Apply protection policy to the document
        document.protect(spp)
    }

//    private fun addUserInfoToPage(pdfDocument: PDDocument, page: PDPage, userFullName: String, userEmail: String) {
//        val contentStream = PDPageContentStream(pdfDocument, page, PDPageContentStream.AppendMode.APPEND, true, true)
//        contentStream.beginText()
//        contentStream.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
//        contentStream.newLineAtOffset(10f, 10f) // Positioning at the bottom
//        contentStream.showText("Purchased by: $userEmail $userFullName")
//        contentStream.endText()
//        contentStream.close()
//    }

    private fun getLockImageStream(): InputStream =
        requireNotNull(javaClass.getResourceAsStream(SECURED_LOCK_IMAGE_NAME)) { // placed in /src/main/resources
            "Lock image was not found"
        }

    private fun getResourceAsStream(fileName: String): InputStream =
        requireNotNull(javaClass.getResourceAsStream(fileName)) {
            "Resource was not found"
        }

    private fun inputStreamToByteArray(inputStream: InputStream): ByteArray {
        val buffer = ByteArray(1024)
        val output = ByteArrayOutputStream()
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        return output.toByteArray()
    }

    companion object {
        const val SECURED_LOCK_IMAGE_NAME = "secured_lock.png"
    }
}
