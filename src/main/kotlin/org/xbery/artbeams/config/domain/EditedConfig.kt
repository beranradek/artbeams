package org.xbery.artbeams.config.domain

/**
 * Edited config entry
 * @author Radek Beran
 */
data class EditedConfig(
    val originalKey: String, // Used to identify the record when updating (key might change)
    val entryKey: String,
    val entryValue: String
)
