package org.xbery.artbeams.evernote.domain

import java.time.Instant

/**
 * Evernote note.
 *
 * @author Radek Beran
 */
data class Note(val guid: String, val title: String, val body: String, val created: Instant, val updated: Instant)
