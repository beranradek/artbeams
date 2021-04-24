package org.xbery.artbeams.articles.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.articles.domain.ArticleCategory
import org.xbery.artbeams.common.repository.ScalaSqlRepository

import javax.inject.Inject
import javax.sql.DataSource

/**
  * Article-Category binding repository.
  * @author Radek Beran
  */
@Repository
class ArticleCategoryRepository @Inject()(dataSource: DataSource) extends ScalaSqlRepository[ArticleCategory, Any, ArticleCategoryFilter](dataSource, ArticleCategoryMapper.Instance) {
  def updateArticleCategories(articleId: String, categoryIds: Seq[String]): Unit = {
    this.deleteByFilter(ArticleCategoryFilter.Empty.copy(articleId = Some(articleId)))
    for {
      categoryId <- categoryIds
    } {
      this.create(ArticleCategory(articleId, categoryId), false)
    }
  }
}
