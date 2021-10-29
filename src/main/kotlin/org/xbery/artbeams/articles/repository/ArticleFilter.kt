package org.xbery.artbeams.articles.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter
import org.xbery.artbeams.common.assets.repository.ValidityAssetFilter
import java.time.Instant

/**
 * @author Radek Beran
 */
data class ArticleFilter(
    override val id: String?,
    override val ids: List<String>?,
    override val createdBy: String?,
    override val validityDate: Instant?,
    val slug: String?,
    val showOnBlog: Boolean?,
    val withExternalId: Boolean?,
    val categoryId: String?,
    val query: String?
) : AssetFilter, ValidityAssetFilter {
    companion object {
        val Empty: ArticleFilter = ArticleFilter(null, null, null, null, null, null, null, null, null)
        fun validOnBlog(): ArticleFilter =
            Empty.copy(validityDate = Instant.now(), showOnBlog = true)

        fun validOnBlogWithCategory(categoryId: String): ArticleFilter = validOnBlog().copy(categoryId = categoryId)
        fun validByQuery(query: String): ArticleFilter = Empty.copy(validityDate = Instant.now(), query = query)
    }
}