package org.xbery.artbeams.evernote.service

import javax.inject.Inject
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
class EvernoteImporter @Inject()(evernoteApi: EvernoteApi, articleRepository: ArticleRepository, markdownConverter: MarkdownConverter) {

  private lazy val logger = LoggerFactory.getLogger(this.getClass)
  private lazy val normalizationHelper = new NormalizationHelper()

  def importArticles(): Either[Exception, Seq[Article]] = {
    val operationMsg = "Import of Evernote notes"
    logger.info(s"${operationMsg}: started")
    try {
      // Force Evernote sync with network storage
      // log.info("Start Evernote sync");
      // List<String> outputLines = processExecutor.execute(evernoteScriptPath, "syncDatabase", "/p", evernoteUserPassword);
      // log.info("Evernote sync output:\n" + Vector.of(outputLines).mkString("\n"));
      // EvernoteSource evernote = new EvernoteSource(evernoteDbFilename, evernoteNotebookName);
      // Seq<Note> notes = evernote.loadNotes();

      // Newer sync logic with pairing articles using their externalIds - ids of Evernote notes
      // Articles that have externalId set will be updated from the notes.
      // This allows to use notes from various notebooks.
      val articles = articleRepository.findArticlesWithExternalIds()
      val noteStoreClient = evernoteApi.getEvernoteStoreClient()
      val updatedOptArticles = for (article <- articles) yield {
        val noteOpt = evernoteApi.findNoteWithCleanedContentByGuid(article.externalId.get, noteStoreClient)
        noteOpt.flatMap(note => updateArticleWithNoteData(article, note))
      }

      // Older sync logic with pairing article's slug with note's normalized title
      // val updatedOptArticles: Seq[Option[Article]] = for {
      //   note <- this.evernoteApi.loadNotes(this.evernoteConfig.notebookName)
      //   updatedArticleOpt = this.updateArticleWithNoteBySlug(note)
      // } yield updatedArticleOpt

      logger.info(s"${operationMsg}: finished successfully")
      Right(updatedOptArticles.flatten)
    } catch {
      case ex: Exception =>
        logger.error(s"${operationMsg}: finished with error ${ex}", ex)
        Left(ex)
    }
  }

  private def updateArticleWithNoteBySlug(note: Note): Option[Article] = {
    val slug = this.normalizationHelper.toSlug(note.title)
    val updatedArticleOpt = this.articleRepository.findBySlug(slug) flatMap { article =>
      updateArticleWithNoteData(article, note)
    }
    if (!updatedArticleOpt.isDefined) {
      logger.info(s"Not Found: Article with slug ${slug}");
    }
    updatedArticleOpt
  }

  private def updateArticleWithNoteData(article: Article, note: Note): Option[Article] = {
    val htmlBody = if (note.body != null && !note.body.isEmpty()) {
      markdownConverter.markdownToHtml(note.body)
    } else {
      ""
    }
    val updatedArticleOpt = articleRepository.updateEntity(article.copy(
      externalId = Option(note.guid),
      title = note.title,
      bodyMarkdown = note.body,
      body = htmlBody
    ))
    updatedArticleOpt.map { updatedArticle =>
      logger.info(s"Updated: Article with slug ${updatedArticle.slug}, externalId ${updatedArticle.externalId.getOrElse("")}");
    }
    updatedArticleOpt
  }
}
