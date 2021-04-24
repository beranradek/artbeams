package org.xbery.artbeams.evernote.service

import java.time.Instant
import java.util.regex.Pattern

import javax.inject.Inject
import com.evernote.edam.`type`.{NoteSortOrder, Notebook}
import com.evernote.edam.error.EDAMNotFoundException
import com.evernote.edam.notestore.{NoteFilter, NoteStore}
import com.evernote.edam.userstore.{Constants, UserStore}
import com.evernote.thrift.protocol.TBinaryProtocol
import com.evernote.thrift.transport.THttpClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.html.HtmlUtils
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.evernote.config.EvernoteConfig
import org.xbery.artbeams.evernote.domain.Note

import scala.jdk.CollectionConverters._

/**
  * @author Radek Beran
  */
@Component
class EvernoteApi @Inject() (evernoteConfig: EvernoteConfig) {

  private val Logger = LoggerFactory.getLogger(this.getClass)
  private val normalizationHelper = new NormalizationHelper()
  // TODO RBe: Move constants to configuration
  private val NOTES_LIMIT = 50
  private val USER_STORE_URL = "https://www.evernote.com/edam/user"
  private val USER_AGENT = "Evernote/CMS (Java) " + Constants.EDAM_VERSION_MAJOR + "." + Constants.EDAM_VERSION_MINOR
  private val CLIENT_NAME = "CMS (Java)"
  private val BR_INSIDE_DIVS = Pattern.compile("<div[^>]*><br[^>]*></div>")
  private val DIV_END_START = Pattern.compile("</div><div[^>]*>")

  // See https://dev.evernote.com/doc/reference/NoteStore.html#Fn_NoteStore_updateNote
  def updateNote(noteGuid: String, content: String): Unit = {
    Logger.info(s"Updating note ${noteGuid} in Evernote")
    val noteStoreClient = getEvernoteStoreClient()
    val note = findNoteByGuid(noteGuid, noteStoreClient)
    // \R  matches any line separator
    val evernoteContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\"><en-note>" +
      (if (content.isEmpty()) "" else content.replaceAll("\\R", "<br />")) +
      "</en-note>"
    note.setContent(evernoteContent)
    note.setUpdated(Instant.now().toEpochMilli())
    noteStoreClient.updateNote(evernoteConfig.developerToken, note)
    Logger.info(s"Updating note ${noteGuid} in Evernote - finished")
  }

  private def findNoteByGuid(noteGuid: String, noteStoreClient: NoteStore.Client): com.evernote.edam.`type`.Note = {
    noteStoreClient.getNote(evernoteConfig.developerToken, noteGuid, true, true, false, false)
  }

  private def findNoteOptByGuid(noteGuid: String, noteStoreClient: NoteStore.Client): Option[com.evernote.edam.`type`.Note] = {
    try {
      val note = noteStoreClient.getNote(evernoteConfig.developerToken, noteGuid, true, true, false, false)
      Option(note)
    } catch {
      case _: EDAMNotFoundException =>
        None
    }
  }

  /**
    * Loads notes from Evernote notebook with given name. Possible HTML tags are stripped from the content.
    * @param notebookName
    * @return
    */
  def loadNotes(notebookName: String): Seq[Note] = {
    Logger.info(s"Loading notes from Evernote notebook ${notebookName}")
    val noteStoreClient = getEvernoteStoreClient()
    val notebookOpt = findNotebook(noteStoreClient, notebookName)
    notebookOpt match {
      case Some(notebook) =>
        val notes = listNotes(noteStoreClient, notebook.getGuid)
        Logger.info(s"${notes.size} notes found")
        (for {
          note <- notes
          // Get the Note with its content and resources data
          cmsNote = findNoteWithCleanedContentByGuid(note.getGuid(), noteStoreClient)
        } yield cmsNote).flatten
      case None =>
        Logger.info(s"Notebook not found")
        Seq.empty
    }
  }

  /**
    * Loads note with given GUID from Evernote. Possible HTML tags are stripped from the content.
    * @param noteGuid
    * @param noteStoreClient
    * @return
    */
  def findNoteWithCleanedContentByGuid(noteGuid: String, noteStoreClient: NoteStore.Client): Option[Note] = {
    val noteDetailOpt = findNoteOptByGuid(noteGuid, noteStoreClient)
    noteDetailOpt.map { noteDetail =>
      val body = if (noteDetail.getContent() != null) {
        // log.info("Content of " + noteDetail.getTitle() + ": " + noteDetail.getContent());
        val htmlWithoutBrInsideDivs = BR_INSIDE_DIVS.matcher(noteDetail.getContent()).replaceAll("\r\n\r\n")
        val htmlWithoutDivEndStart = DIV_END_START.matcher(htmlWithoutBrInsideDivs).replaceAll("\r\n")
        val contentWithoutHtml = HtmlUtils.stripHtmlTags(htmlWithoutDivEndStart)
        // log.info("Content of " + noteDetail.getTitle() + " without HTML: " + contentWithoutHtml);
        contentWithoutHtml
      } else {
        ""
      }
      Note(
        noteDetail.getGuid(),
        noteDetail.getTitle(),
        body,
        Option(noteDetail.getCreated()).map(Instant.ofEpochMilli).getOrElse(Instant.now()),
        Option(noteDetail.getUpdated()).map(Instant.ofEpochMilli).getOrElse(Instant.now())
      )
    }
  }

  private def findNotebook(noteStoreClient: NoteStore.Client, notebookName: String): Option[Notebook] = {
    val notebookNameNormalized = normalizationHelper.normalize(notebookName)
    val notebooks = Option(noteStoreClient.listNotebooks(evernoteConfig.developerToken)).map(_.asScala.toSeq).getOrElse(Seq.empty[Notebook])
    Logger.info(s"${notebooks.size} notebooks found")
    notebooks.find(nb => {
      val nbNameNorm = normalizationHelper.normalize(nb.getName)
      nbNameNorm != null && nbNameNorm == notebookNameNormalized
    })
  }

  /**
    * List notes from given notebook. See https://github.com/evernote/evernote-sdk-java/blob/master/sample/client/EDAMDemo.java
    */
  private def listNotes(evernoteStoreClient: NoteStore.Client, notebookGuid: String): Seq[com.evernote.edam.`type`.Note] = {
    Logger.info(s"Listing notes")
    // Search for the notes in notebook, ordered by creation date
    val filter = new NoteFilter
    filter.setNotebookGuid(notebookGuid)
    filter.setOrder(NoteSortOrder.CREATED.getValue)
    filter.setAscending(true)
    var notes: Seq[com.evernote.edam.`type`.Note] = Seq.empty
    var offset = 0
    var totalNotes = 0
    do {
      val noteList = evernoteStoreClient.findNotes(evernoteConfig.developerToken, filter, offset, NOTES_LIMIT)
      totalNotes = noteList.getTotalNotes()
      offset = offset + NOTES_LIMIT
      notes = notes ++ Option(noteList.getNotes).map(_.asScala.toSeq).getOrElse(Seq.empty)
    } while (offset < totalNotes)
    notes
  }

  /**
    * Initialize the noteStrore. See
    * https://github.com/evernote/evernote-sdk-java/blob/master/sample/client/EDAMDemo.java
    */
  def getEvernoteStoreClient(): NoteStore.Client = {
    // Set up the UserStore client and check that we can speak to the server
    val userStoreTrans = new THttpClient(USER_STORE_URL)
    userStoreTrans.setCustomHeader("User-Agent", USER_AGENT)
    val userStoreProtocol = new TBinaryProtocol(userStoreTrans)
    val userStore = new UserStore.Client(userStoreProtocol, userStoreProtocol)
    val versionOk = userStore.checkVersion(CLIENT_NAME, Constants.EDAM_VERSION_MAJOR, Constants.EDAM_VERSION_MINOR)
    if (!versionOk) throw new IllegalStateException(s"Incompatible Evernote client protocol version. ${Constants.EDAM_VERSION_MAJOR}.${Constants.EDAM_VERSION_MINOR} expected.")
    // Get the URL used to interact with the contents of the user's account
    // When your application authenticates using OAuth, the NoteStore URL
    // will be returned along with the auth token in the final OAuth request.
    // In that case, you don't need to make this call.
    val developerToken = evernoteConfig.developerToken
    if (developerToken == null || developerToken.trim().isEmpty()) {
      throw new IllegalStateException(s"Invalid Evernote developer token: ${developerToken}")
    }
    val notestoreUrl = userStore.getNoteStoreUrl(developerToken)
    // Set up the NoteStore client
    val noteStoreTrans = new THttpClient(notestoreUrl)
    noteStoreTrans.setCustomHeader("User-Agent", USER_AGENT)
    val noteStoreProtocol = new TBinaryProtocol(noteStoreTrans)
    new NoteStore.Client(noteStoreProtocol, noteStoreProtocol)
  }
}
