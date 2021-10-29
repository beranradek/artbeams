package org.xbery.artbeams.articles.repository

/**
 * @author Radek Beran
 */
data class ArticleCategoryFilter(val articleId: String?, val categoryId: String?) {
    companion object {
        val Empty: ArticleCategoryFilter = ArticleCategoryFilter(null, null)
    }
}
