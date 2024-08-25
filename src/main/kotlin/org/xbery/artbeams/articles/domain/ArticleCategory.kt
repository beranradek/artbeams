package org.xbery.artbeams.articles.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
 * @author Radek Beran
 */
data class ArticleCategory(val articleId: String, val categoryId: String) {
    companion object {
        val Empty: ArticleCategory = ArticleCategory(AssetAttributes.EMPTY_ID, AssetAttributes.EMPTY_ID)
    }
}
