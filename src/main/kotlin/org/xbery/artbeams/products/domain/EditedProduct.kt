package org.xbery.artbeams.products.domain

/**
 * @author Radek Beran
 */
data class EditedProduct(
    val id: String,
    val slug: String,
    val title: String,
    val fileName: String?,
    val confirmationMailingGroupId: String?,
    val mailingGroupId: String?
)
