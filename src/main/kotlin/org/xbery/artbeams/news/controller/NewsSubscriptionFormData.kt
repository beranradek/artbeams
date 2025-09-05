package org.xbery.artbeams.news.controller

/**
 * News subscription form data.
 * @author Radek Beran
 */
data class NewsSubscriptionFormData(
    val email: String?
) {
    companion object {
        val Empty = NewsSubscriptionFormData("")
    }
}
