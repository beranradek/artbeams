package org.xbery.artbeams.courses.admin

/**
 * EditedModule used by admin forms for module editing.
 */
data class EditedModule(
    val id: String,
    val title: String,
    val image: String?,
    val shortDescription: String?,
    val perex: String?
)
