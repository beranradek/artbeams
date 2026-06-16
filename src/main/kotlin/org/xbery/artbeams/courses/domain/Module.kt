package org.xbery.artbeams.courses.domain

/**
 * Module within a course.
 */
data class Module(
    val id: String,
    val title: String,
    /** Image file name/id for module */
    val image: String?,
    /** Short description */
    val shortDescription: String?,
    /** Perex / introduction text for module */
    val perex: String?
)
