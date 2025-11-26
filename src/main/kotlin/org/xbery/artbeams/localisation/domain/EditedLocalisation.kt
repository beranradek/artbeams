package org.xbery.artbeams.localisation.domain

/**
 * Edited localisation entry
 * @author Radek Beran
 */
data class EditedLocalisation(
    val originalKey: String, // Used to identify the record when updating (key might change)
    val entryKey: String,
    val entryValue: String
)
