package org.xbery.artbeams.articles.repository

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.articles.domain.ArticleCategory
import org.xbery.overview.sql.repo.ScalaSqlRepository

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
