package org.xbery.artbeams.google.docs

import com.google.api.services.docs.v1.Docs
import com.google.api.services.docs.v1.DocsScopes
import com.google.api.services.docs.v1.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.common.access.domain.UnauthorizedException
import org.xbery.artbeams.common.markdown.MarkdownConverter
import org.xbery.artbeams.google.auth.GoogleApiAuth

/**
 * Google Document manipulation service.
 *
 * @author Radek Beran
 */
@Service
open class GoogleDocsService(
    private val googleAuth: GoogleApiAuth,
    private val markdownConverter: MarkdownConverter,
    private val articleRepository: ArticleRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Global instance of the scopes required by this application.
     * If modifying these scopes, delete your previously saved tokens/folder.
     */
    // NOTE: Without open keyword, initialization of val after injection to another component does not work
    // withing created Spring proxy
    open val scopes: List<String> = listOf(DocsScopes.DOCUMENTS)

    private var docs: Docs? = null

    /**
     * Replaces all content of given Google Document with given text content.
     */
    open fun writeGoogleDoc(documentId: String, content: String) {
        val opName = "Updating Google Document $documentId"
        if (content.trim().isEmpty()) {
            logger.info("$opName: Document will not be updated with empty content due to potential data loss.")
            return
        }

        logger.info(opName)

        val docs = getDocsService()
        val document = docs.documents().get(documentId).execute()

        val requests = mutableListOf<Request>()

        // Clear existing content
        if (!document.body.content.isNullOrEmpty()) {
            requests.add(
                Request().setDeleteContentRange(
                    DeleteContentRangeRequest().setRange(
                        Range().setStartIndex(1).setEndIndex(document.body.content.last().endIndex - 1)
                    )
                )
            )
        }

        // Insert new text content
        requests.add(Request().setInsertText(InsertTextRequest().setText(content).setLocation(Location().setIndex(1))))

        docs.documents().batchUpdate(documentId, BatchUpdateDocumentRequest().setRequests(requests)).execute()

        logger.info("$opName - finished")
    }

    /**
     * Returns text content from paragraphs of given Google Document.
     */
    open fun readGoogleDoc(documentId: String): String {
        val docs = getDocsService()
        val document = docs.documents().get(documentId).execute()
        return extractTextContent(document)
    }

    /**
     * Updates article with the content of corresponding Google Document. Returns updated article,
     * or null if it was not updated (document not found, or article has not an externalId - id of document).
     *
     * @throws UnauthorizedException if user is not authorized to access Google documents or authorization has expired
     */
    @CacheEvict(value = [ Article.CacheName ], allEntries = true)
    open fun updateArticleWithGoogleDoc(article: Article): Article? {
        if (article.externalId == null) {
            // Article without pairing to Google Doc id
            return null
        }
        val docContent = readGoogleDoc(article.externalId)
        if (docContent == null || docContent.trim().isEmpty()) {
            logger.info("Nothing to update from Google Doc. Doc is empty. Article with slug ${article.slug}, externalId ${article.externalId}")
            return article
        }
        val htmlBody = markdownConverter.markdownToHtml(docContent)
        if (article.bodyMarkdown == docContent && article.body == htmlBody) {
            logger.info("Nothing to update from Google Doc (already up to date): Article with slug ${article.slug}, externalId ${article.externalId}")
            return article
        }
        val updatedArticleOpt = articleRepository.updateEntity(
            article.copy(
                bodyMarkdown = docContent,
                body = htmlBody
            )
        )
        updatedArticleOpt?.let { updatedArticle -> logger.info("Updated from Google Doc: Article with slug ${updatedArticle.slug}, externalId ${updatedArticle.externalId ?: ""}") }
        return updatedArticleOpt
    }

    private fun extractTextContent(document: Document): String {
        val content = StringBuilder()
        document.body.content.forEach { structuralElement ->
            if (structuralElement.paragraph != null) {
                structuralElement.paragraph.elements.forEach { paragraphElement ->
                    content.append(paragraphElement.textRun.content)
                }
            }
        }
        return content.toString()
    }

    /**
     * Returns authorized Google Docs API client service. This Docs service is (due to need of authorization
     * tokens and non-exact documentation) probably not thread-safe.
     * See also Google Docs API: https://developers.google.com/docs/api/how-tos/overview,
     * https://developers.google.com/docs/api/quickstart/java
     *
     * @param returnUrl Return URL if processing of Google doc authorization is needed
     * @throws UnauthorizedException if user is not authorized or authorization has expired
     */
    private fun getDocsService(): Docs {
        if (!googleAuth.isUserAuthorized(scopes)) {
            docs = null
            throw UnauthorizedException("Unauthorized access to Google documents")
        }
        if (docs == null) {
            docs = Docs.Builder(googleAuth.httpTransport, googleAuth.jsonFactory, googleAuth.getCredentials(scopes))
                .setApplicationName(googleAuth.applicationName)
                .build()
        }
        return requireNotNull(docs)
    }
}
