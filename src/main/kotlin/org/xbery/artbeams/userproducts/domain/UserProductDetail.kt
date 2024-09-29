package org.xbery.artbeams.userproducts.domain

/**
 * Product within a "user library" (that the user has access to).
 *
 * @author Radek Beran
 */
data class UserProductDetail(
    val id: String,
    val title: String,
    val slug: String,
    val subtitle: String?
)
