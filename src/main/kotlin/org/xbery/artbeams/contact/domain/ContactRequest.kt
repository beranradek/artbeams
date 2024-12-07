package org.xbery.artbeams.contact.domain

/**
 * @author Radek Beran
 */
data class ContactRequest(
    val name: String?,
    val email: String,
    val phone: String?,
    val message: String
) {
    companion object {
        val EMPTY = ContactRequest(null, "", null, "")
    }
}
