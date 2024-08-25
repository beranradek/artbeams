package org.xbery.artbeams.articles.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.common.assets.domain.ValidityAsset
import java.util.*

/**
 * Article entity
 *
 * @author Radek Beran
 */
data class Article(
    override val common: AssetAttributes,
    override val validity: Validity,
    val externalId: String?,
    val slug: String,
    val title: String,
    val image: String?,
    val perex: String,
    val bodyMarkdown: String,
    val body: String,
    val keywords: String,
    val showOnBlog: Boolean
) : Asset(), ValidityAsset {
    fun updatedWith(edited: EditedArticle, htmlBody: String, userId: String): Article {
        return this.copy(
            common = this.common.updatedWith(userId),
            validity = this.validity.updatedWith(edited),
            externalId = (edited.externalId?.let { extId -> if (extId.trim() == "") null else extId }),
            slug = edited.slug,
            title = edited.title,
            image = (edited.image?.let { img -> if (img.trim() == "") null else img }),
            perex = edited.perex,
            bodyMarkdown = edited.bodyMarkdown,
            body = htmlBody,
            keywords = edited.keywords,
            showOnBlog = edited.showOnBlog
        )
    }

    fun toEdited(categories: List<String>): EditedArticle {
        val validTo = this.validTo
        return EditedArticle(this.id,
            this.externalId,
            this.slug,
            this.title,
            this.image,
            null,
            this.perex,
            this.bodyMarkdown,
            if (this.validFrom == AssetAttributes.EmptyDate) {
                Date()
            } else {
                Date(this.validFrom.toEpochMilli())
            },
            if (validTo != null) {
                Date(validTo.toEpochMilli())
            } else {
                null
            },
            this.keywords,
            this.showOnBlog,
            categories
        )
    }

    companion object {
        const val CacheName: String = "articles"
        val Empty: Article = Article(
            AssetAttributes.Empty,
            Validity.Empty,
            null, "", "New article",
            null, "", "", "", "", true
        )
        val EmptyEdited: EditedArticle = Empty.toEdited(ArrayList<String>())
    }
}
