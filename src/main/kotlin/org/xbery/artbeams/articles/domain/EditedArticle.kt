package org.xbery.artbeams.articles.domain

import net.formio.upload.UploadedFile
import org.xbery.artbeams.common.assets.domain.EditedTimeValidity
import java.util.Date

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
    val file: UploadedFile?,
    val perex: String,
    val bodyEdited: String,
    val editor: String,
    override val validFrom: Date,
    override val validTo: Date?,
    val keywords: String,
    val showOnBlog: Boolean,
    val categories: List<String>,
) : EditedTimeValidity
