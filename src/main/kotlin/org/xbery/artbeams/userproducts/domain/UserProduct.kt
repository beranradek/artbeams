package org.xbery.artbeams.userproducts.domain

/**
 * Product within a "user library" (that the user has access to).
 *
 * @author Radek Beran
 */
data class UserProduct(
    val id: String,
    val productName: String
)
