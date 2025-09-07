package org.xbery.artbeams.news.domain

import java.time.Instant

/**
 * News subscription entity.
 * @author Radek Beran
 */
data class NewsSubscription(
    val id: String,
    val email: String,
    val created: Instant,
    val confirmed: Instant? = null,
) {
    companion object {
        val EMPTY = NewsSubscription("", "", Instant.now())
    }
}
