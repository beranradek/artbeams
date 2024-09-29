package org.xbery.artbeams.userproducts.domain

/**
 * Brief info about product within a "user library" (that the user has access to).
 *
 * @author Radek Beran
 */
data class UserProductInfo(
    val id: String,
    val title: String,
    val slug: String,
    val subtitle: String?
)
