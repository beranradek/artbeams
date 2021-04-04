package org.xbery.artbeams.articles.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
  * @author Radek Beran
  */
case class ArticleCategory(articleId: String, categoryId: String)

object ArticleCategory {
  lazy val Empty = ArticleCategory(
    AssetAttributes.EmptyId,
    AssetAttributes.EmptyId
  )
}
