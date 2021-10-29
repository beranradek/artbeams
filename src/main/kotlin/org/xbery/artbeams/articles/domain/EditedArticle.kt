package org.xbery.artbeams.articles.domain

import org.xbery.artbeams.common.assets.domain.EditedTimeValidity
import java.util.*

/**
 * Editable article attributes.
 * @author Radek Beran
 */
data class EditedArticle(
    val id: String,
    val externalId: String?,
    val slug: String,
    val title: String,
    val image: String?,
    val perex: String,
    val bodyMarkdown: String,
    override val validFrom: Date,
    override val validTo: Date?,
    val keywords: String,
    val showOnBlog: Boolean,
    val categories: List<String>
) : EditedTimeValidity

