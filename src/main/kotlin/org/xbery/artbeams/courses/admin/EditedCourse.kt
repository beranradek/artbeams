package org.xbery.artbeams.courses.admin

/**
 * EditedCourse is a lightweight DTO used by admin forms to edit course metadata.
 */
data class EditedCourse(
    val id: String?,
    val slug: String?,
    val title: String?,
    val subtitle: String?,
    val listingImage: String?,
    val image: String?,
    val perex: String?,
    /**
     * Comma-separated product ids selected in multi-select control.
     * For simplicity forms bind this as a string; conversion is handled in service.
     */
    val productIds: String?
)
