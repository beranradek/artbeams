package org.xbery.artbeams.comments.domain

/**
 * @author Radek Beran
 */
enum class CommentState {
    /** Waiting for approval. */
    WAITING_FOR_APPROVAL,
    APPROVED,
    REJECTED
}
