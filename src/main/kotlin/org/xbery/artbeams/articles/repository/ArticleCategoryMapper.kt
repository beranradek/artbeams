package org.xbery.artbeams.articles.repository

import org.xbery.artbeams.articles.domain.ArticleCategory
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.mapper.DynamicEntityMapper
import org.xbery.overview.repo.Conditions
import java.util.*

/**
 * Maps Article-Category binding to set of attributes and vice versa.
 * @author Radek Beran
 */
open class ArticleCategoryMapper : DynamicEntityMapper<ArticleCategory, ArticleCategoryFilter>() {
    private val cls: Class<ArticleCategory> = ArticleCategory::class.java
    override fun getTableName(): String = "article_category"
    val articleIdAttr: Attribute<ArticleCategory, String> = add(Attr.ofString(cls, "article_id").get { e -> e.articleId }.primary())
    val categoryIdAttr: Attribute<ArticleCategory, String> =
        add(Attr.ofString(cls, "category_id").get { e -> e.categoryId }.primary() )

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<ArticleCategory, *>>,
        aliasPrefix: String?
    ): ArticleCategory {
        return ArticleCategory(articleIdAttr.getValueFromSource(
          attributeSource,
          aliasPrefix
        ), categoryIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""))
    }

    override fun composeFilterConditions(filter: ArticleCategoryFilter): MutableList<Condition> {
        val conditions = mutableListOf<Condition>()
        filter.articleId?.let { articleId -> conditions.add(Conditions.eq(this.articleIdAttr, articleId)) }
        filter.categoryId?.let { categoryId -> conditions.add(Conditions.eq(this.categoryIdAttr, categoryId)) }
        return conditions
    }

    companion object {
        val Instance: ArticleCategoryMapper = ArticleCategoryMapper()
    }
}
