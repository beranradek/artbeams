package org.xbery.artbeams.products.domain

/**
 * @author Radek Beran
 */
data class EditedProduct(
    val id: String,
    val slug: String,
    val title: String,
    val subtitle: String?,
    val fileName: String?,
    val listingImage: String?,
    val image: String?,
    val confirmationMailingGroupId: String?,
    val mailingGroupId: String?
)
