package org.xbery.artbeams.comments.domain

/**
 * @author Radek Beran
 */
data class EditedComment(
    val id: String,
    val entityId: String,
    val comment: String,
    val userName: String,
    val email: String
)
