package org.xbery.artbeams.evernote.service

import com.evernote.edam.error.EDAMNotFoundException
import com.evernote.edam.notestore.NoteFilter
import com.evernote.edam.notestore.NoteStore.Client
import com.evernote.edam.type.Note
import com.evernote.edam.type.NoteSortOrder
import com.evernote.edam.type.Notebook
import com.evernote.edam.userstore.Constants
import com.evernote.edam.userstore.UserStore
import com.evernote.thrift.protocol.TBinaryProtocol
import com.evernote.thrift.transport.THttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.html.HtmlUtils
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.evernote.config.EvernoteConfig
import java.time.Instant
import java.util.regex.Pattern

/**
 * @author Radek Beran
 */
@Component
open class EvernoteApi(private val evernoteConfig: EvernoteConfig) {
    private val Logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val normalizationHelper: NormalizationHelper = NormalizationHelper()
    // TODO RBe: Move constants to configuration
    private val NOTES_LIMIT: Int = 50
    private
    val USER_STORE_URL: String = "https://www.evernote.com/edam/user"
    private
    val USER_AGENT: String = "Evernote/CMS (Java) " + Constants.EDAM_VERSION_MAJOR + "." + Constants.EDAM_VERSION_MINOR
    private val CLIENT_NAME: String = "CMS (Java)"
    private
    val BR_INSIDE_DIVS: Pattern = Pattern.compile("<div[^>]*><br[^>]*></div>")
    private val DIV_END_START: Pattern = Pattern.compile("</div><div[^>]*>")

    // See https://dev.evernote.com/doc/reference/NoteStore.html#Fn_NoteStore_updateNote
    fun updateNote(noteGuid: String, content: String) {
        val opName = "Updating note $noteGuid in Evernote"
        Logger.info(opName)
        val noteStoreClient: Client = getEvernoteStoreClient()
        val note = findNoteByGuid(noteGuid, noteStoreClient)
        // \R  matches any line separator
        val evernoteContent: String =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\"><en-note>" +
                    (if (content.isEmpty()) "" else content.replace(ReturnRegex, "<br />")) + "</en-note>"
        note.content = evernoteContent
        note.updated = Instant.now().toEpochMilli()
        noteStoreClient.updateNote(evernoteConfig.developerToken, note)
        Logger.info("$opName - finished")
    }

    private fun findNoteByGuid(noteGuid: String, noteStoreClient: Client): Note {
        return noteStoreClient.getNote(
            evernoteConfig.developerToken,
            noteGuid, true, true, false, false
        )
    }

    private fun findNoteOptByGuid(noteGuid: String, noteStoreClient: Client): Note? {
        return try {
            noteStoreClient.getNote(
                evernoteConfig.developerToken,
                noteGuid, true, true, false, false
            )
        } catch (_: EDAMNotFoundException) {
            Logger.warn("Note not found in Evernote by guid $noteGuid")
            null
        }
    }

    /**
     * Loads notes from Evernote notebook with given name. Possible HTML tags are stripped from the content.
     * @param notebookName
     * @return
     */
    fun loadNotes(notebookName: String): List<org.xbery.artbeams.evernote.domain.Note> {
        Logger.info("Loading notes from Evernote notebook $notebookName")
        val noteStoreClient: Client = getEvernoteStoreClient()
        val notebook = findNotebook(noteStoreClient, notebookName)
        return if (notebook != null) {
            val notes = listNotes(noteStoreClient, notebook.guid)
            Logger.info("${notes.size} notes found")
            // Get the Note with its content and resources data
            notes.mapNotNull { note -> findNoteWithCleanedContentByGuid(note.guid, noteStoreClient) }
        } else {
            Logger.info("Notebook not found")
            listOf()
        }
    }

    /**
     * Loads note with given GUID from Evernote. Possible HTML tags are stripped from the content.
     * @param noteGuid
     * @param noteStoreClient
     * @return
     */
    fun findNoteWithCleanedContentByGuid(noteGuid: String, noteStoreClient: Client): org.xbery.artbeams.evernote.domain.Note? {
        val noteDetailOpt: Note? = findNoteOptByGuid(noteGuid, noteStoreClient)
        return noteDetailOpt?.let { noteDetail ->
            val body: String = if (noteDetail.content != null) {
                val htmlWithoutBrInsideDivs: String =
                    BR_INSIDE_DIVS.matcher(noteDetail.content).replaceAll("\r\n\r\n")
                val htmlWithoutDivEndStart: String =
                    DIV_END_START.matcher(htmlWithoutBrInsideDivs).replaceAll("\r\n")
                val contentWithoutHtml: String = HtmlUtils.stripHtmlTags(htmlWithoutDivEndStart)
                contentWithoutHtml
            } else {
                ""
            }
            org.xbery.artbeams.evernote.domain.Note(
                noteDetail.guid,
                noteDetail.title,
                body,
                noteDetail.created?.let { Instant.ofEpochMilli(it) } ?: Instant.now(),
                noteDetail.updated?.let { Instant.ofEpochMilli(it) } ?: Instant.now()
            )
        }
    }

    private fun findNotebook(noteStoreClient: Client, notebookName: String): Notebook? {
        val notebookNameNormalized: String = normalizationHelper.normalize(notebookName)
        val notebooks = noteStoreClient.listNotebooks(evernoteConfig.developerToken)
        Logger.info("${notebooks.size} notebooks found")
        return notebooks.find { nb ->
            val nbNameNorm: String = normalizationHelper.normalize(nb.name)
            nbNameNorm != null && nbNameNorm == notebookNameNormalized
        }
    }

    /**
     * List notes from given notebook. See https://github.com/evernote/evernote-sdk-java/blob/master/sample/client/EDAMDemo.java
     */
    private fun listNotes(evernoteStoreClient: Client, notebookGuid: String): List<Note> {
        Logger.info("Listing notes")
        // Search for the notes in notebook, ordered by creation date
        val filter = NoteFilter()
        filter.notebookGuid = notebookGuid
        filter.order = NoteSortOrder.CREATED.value
        filter.isAscending = true
        var notes = mutableListOf<Note>()
        var offset: Int = 0
        var totalNotes: Int = 0
        do {
            val noteList = evernoteStoreClient.findNotes(evernoteConfig.developerToken, filter, offset, NOTES_LIMIT)
            totalNotes = noteList.getTotalNotes()
            offset += NOTES_LIMIT
            notes.addAll(noteList.notes)
        } while (offset < totalNotes)
        return notes
    }

    /**
     * Initialize the noteStrore. See
     * https://github.com/evernote/evernote-sdk-java/blob/master/sample/client/EDAMDemo.java
     */
    fun getEvernoteStoreClient(): Client {
        // Set up the UserStore client and check that we can speak to the server
        val userStoreTrans: THttpClient = THttpClient(USER_STORE_URL)
        userStoreTrans.setCustomHeader("User-Agent", USER_AGENT)
        val userStoreProtocol = TBinaryProtocol(userStoreTrans)
        val userStore = UserStore.Client(userStoreProtocol, userStoreProtocol)
        val versionOk = userStore.checkVersion(CLIENT_NAME, Constants.EDAM_VERSION_MAJOR, Constants.EDAM_VERSION_MINOR)
        if (!versionOk) {
            throw IllegalStateException("Incompatible Evernote client protocol version. ${ Constants.EDAM_VERSION_MAJOR }.${ Constants.EDAM_VERSION_MINOR } expected.")
        }

        // Get the URL used to interact with the contents of the user's account
        // When your application authenticates using OAuth, the NoteStore URL
        // will be returned along with the auth token in the final OAuth request.
        // In that case, you don't need to make this call.
        val developerToken = evernoteConfig.developerToken
            if (developerToken == null || developerToken.trim().isEmpty()) {
                throw  IllegalStateException("Invalid Evernote developer token: $developerToken")
            }
        val noteStoreUrl = userStore.getNoteStoreUrl(developerToken)
        // Set up the NoteStore client
        val noteStoreTrans = THttpClient(noteStoreUrl)
        noteStoreTrans.setCustomHeader("User-Agent", USER_AGENT)
        val noteStoreProtocol = TBinaryProtocol(noteStoreTrans)
        return Client(noteStoreProtocol, noteStoreProtocol)
    }

    companion object {
        val ReturnRegex = "\\R".toRegex()
    }
}
