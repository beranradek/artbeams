package org.xbery.artbeams.evernote.service

import com.evernote.edam.notestore.NoteStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
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

    @CacheEvict(value = [ Article.CacheName ], allEntries = true)
    open fun importArticles(): List<Article> {
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
                val updatedArticle = updateArticleWithNote(article, noteStoreClient)
                updatedArticle?.let { updatedArticles.add(it) }
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

    /**
     * Updates article with the content of corresponding Evernote note. Returns updated article,
     * or null if it was not updated (note not found, or article has not an externalId - id of note).
     */
    @CacheEvict(value = [ Article.CacheName ], allEntries = true)
    open fun updateArticleWithNote(
        article: Article,
        noteStoreClient: NoteStore.Client = evernoteApi.getEvernoteStoreClient()
    ): Article? {
        if (article.externalId == null || !isEvernoteIdentifier(article.externalId)) {
            // Article without pairing to note id, or with different identifier than for Evernote
            return null
        }
        val noteOpt =
            evernoteApi.findNoteWithCleanedContentByGuid(
                article.externalId,
                noteStoreClient
            )
        if (noteOpt != null) {
            return updateArticleWithNoteData(article, noteOpt) ?: null
        }
        return null
    }

    fun isEvernoteIdentifier(noteId: String): Boolean = noteId.contains('-')

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
        val htmlBody: String = if (note.body != null && note.body.isNotEmpty()) {
            markdownConverter.markdownToHtml(note.body)
        } else {
            ""
        }
        if (article.bodyMarkdown == note.body && article.body == htmlBody && article.title == note.title) {
            logger.info("Nothing to update from Evernote (already up to date): Article with slug ${article.slug}, externalId ${article.externalId ?: ""}")
            return article
        }
        val updatedArticleOpt =
            articleRepository.updateEntity(article.copy(
                title = note.title,
                bodyMarkdown = note.body,
                body = htmlBody
            ))
        updatedArticleOpt?.let { updatedArticle -> logger.info("Updated from Evernote: Article with slug ${updatedArticle.slug}, externalId ${updatedArticle.externalId ?: ""}") }
        return updatedArticleOpt
    }
}
