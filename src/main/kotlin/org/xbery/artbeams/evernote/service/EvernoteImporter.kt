package org.xbery.artbeams.evernote.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.common.markdown.MarkdownConverter
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.evernote.domain.Note

/**
 * Process import of notes from Evernote.
 *
 * @author Radek Beran
 */
@Service
open class EvernoteImporter(
    private val evernoteApi: EvernoteApi,
    private val articleRepository: ArticleRepository,
    private val markdownConverter: MarkdownConverter
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val normalizationHelper: NormalizationHelper = NormalizationHelper()

    fun importArticles(): List<Article> {
        val operationMsg = "Import of Evernote notes"
        logger.info("$operationMsg: started")
        return try {
            // Force Evernote sync with network storage
            // log.info("Start Evernote sync");
            // List<String> outputLines = processExecutor.execute(evernoteScriptPath, "syncDatabase", "/p", evernoteUserPassword);
            // log.info("Evernote sync output:\n" + Vector.of(outputLines).mkString("\n"));
            // EvernoteSource evernote = new EvernoteSource(evernoteDbFilename, evernoteNotebookName);
            // Seq<Note> notes = evernote.loadNotes();

            // Newer sync logic with pairing articles using their externalIds - ids of Evernote notes
            // Articles that have externalId set will be updated from the notes.
            // This allows to use notes from various notebooks.
            val articles: List<Article> = articleRepository.findArticlesWithExternalIds()
            val noteStoreClient = evernoteApi.getEvernoteStoreClient()
            val updatedArticles = mutableListOf<Article>()
            for (article in articles) {
                if (article.externalId != null) {
                    val noteOpt =
                        evernoteApi.findNoteWithCleanedContentByGuid(
                            article.externalId,
                            noteStoreClient
                        )
                    if (noteOpt != null) {
                        val updatedArticle = updateArticleWithNoteData(article, noteOpt)
                        if (updatedArticle != null) {
                            updatedArticles.add(updatedArticle)
                        }
                    }
                }
            }

            // Older sync logic with pairing article's slug with note's normalized title
            // val updatedOptArticles: Seq[Option[Article]] = for {
            //   note <- this.evernoteApi.loadNotes(this.evernoteConfig.notebookName)
            //   updatedArticleOpt = this.updateArticleWithNoteBySlug(note)
            // } yield updatedArticleOpt

            logger.info("${operationMsg}: finished successfully")
            updatedArticles
        } catch (ex: Exception) {
            logger.error("$operationMsg: finished with error $ex", ex)
            throw ex
        }
    }

    private fun updateArticleWithNoteBySlug(note: Note): Article? {
        val slug = this.normalizationHelper.toSlug(note.title)
        var article = this.articleRepository.findBySlug(slug)
        if (article != null) {
            article = updateArticleWithNoteData(article, note)
        }
        if (article == null) {
            logger.info("Not Found: Article with slug $slug")
        }
        return article
    }

    private fun updateArticleWithNoteData(article: Article, note: Note): Article? {
        val htmlBody: String = if (note.body != null && !note.body.isEmpty()) {
            markdownConverter.markdownToHtml(note.body)
        } else {
            ""
        }
        val updatedArticleOpt =
            articleRepository.updateEntity(article.copy(
                externalId = note.guid,
                title = note.title,
                bodyMarkdown = note.body,
                body = htmlBody
            ))
        updatedArticleOpt?.let { updatedArticle -> logger.info("Updated: Article with slug ${updatedArticle.slug}, externalId ${updatedArticle.externalId ?: ""}") }
        return updatedArticleOpt
    }
}
