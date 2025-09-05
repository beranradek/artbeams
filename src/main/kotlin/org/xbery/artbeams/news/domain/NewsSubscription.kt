package org.xbery.artbeams.news.domain

import java.time.Instant

/**
 * News subscription entity.
 * @author Radek Beran
 */
data class NewsSubscription(
    val id: String,
    val email: String,
    val created: Instant
) {
    companion object {
        val EMPTY = NewsSubscription("", "", Instant.now())
    }
}
