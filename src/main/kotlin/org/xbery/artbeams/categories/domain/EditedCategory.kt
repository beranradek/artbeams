package org.xbery.artbeams.categories.domain

import org.xbery.artbeams.common.assets.domain.EditedTimeValidity
import java.util.*

/**
 * @author Radek Beran
 */
data class EditedCategory(
    val id: String,
    val slug: String,
    val title: String,
    val description: String,
    override val validFrom: Date,
    override val validTo: Date?
) : EditedTimeValidity
