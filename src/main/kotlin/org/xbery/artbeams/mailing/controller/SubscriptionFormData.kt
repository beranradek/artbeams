package org.xbery.artbeams.mailing.controller

/**
 * @author Radek Beran
 */
data class SubscriptionFormData(
    val email: String,
    val name: String
) {

    companion object {
        val Empty = SubscriptionFormData("", "")
    }
}
