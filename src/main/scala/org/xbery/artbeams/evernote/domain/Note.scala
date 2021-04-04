package org.xbery.artbeams.evernote.domain

import java.time.Instant

/**
  * Evernote note.
 *
  * @author Radek Beran
  */
case class Note(
  guid: String,
  title: String,
  body: String,
  created: Instant,
  updated: Instant
)
