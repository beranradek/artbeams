package org.xbery.artbeams.articles.repository

import org.xbery.artbeams.articles.domain.ArticleCategory
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

import java.util

/**
  * Maps Article-Category binding to set of attributes and vice versa.
  * @author Radek Beran
  */
class ArticleCategoryMapper() extends DynamicEntityMapper[ArticleCategory, ArticleCategoryFilter] {

  private val cls = classOf[ArticleCategory]

  override val getTableName: String = "article_category"

  val articleIdAttr = add(Attr.ofString(cls, "article_id").get(e => e.articleId).primary())
  val categoryIdAttr = add(Attr.ofString(cls, "category_id").get(e => e.categoryId).primary())

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[ArticleCategory, _]], aliasPrefix: String): ArticleCategory = {
    ArticleCategory(
      articleIdAttr.getValueFromSource(attributeSource, aliasPrefix),
      categoryIdAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
  }

  override def composeFilterConditions(filter: ArticleCategoryFilter): util.List[Condition] = {
    val conditions = new util.ArrayList[Condition]
    filter.articleId.map(articleId => conditions.add(Conditions.eq(this.articleIdAttr, articleId)))
    filter.categoryId.map(categoryId => conditions.add(Conditions.eq(this.categoryIdAttr, categoryId)))
    conditions
  }
}

object ArticleCategoryMapper {
  lazy val Instance = new ArticleCategoryMapper()
}
