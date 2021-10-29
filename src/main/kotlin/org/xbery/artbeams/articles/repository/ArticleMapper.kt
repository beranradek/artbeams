package org.xbery.artbeams.articles.repository

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.common.assets.repository.ValidityAssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.repo.Conditions
import org.xbery.overview.sql.filter.SqlCondition

/**
 * @author Radek Beran
 */
open class ArticleMapper() : ValidityAssetMapper<Article, ArticleFilter>() {
    override fun cls(): Class<Article> = Article::class.java
    override fun getTableName(): String = "articles"
    val externalIdAttr: Attribute<Article, String> = add(Attr.ofString(cls(), "external_id").get { e -> e.externalId})
    val slugAttr: Attribute<Article, String> = add(Attr.ofString(cls(), "slug").get { e -> e.slug})
    val titleAttr: Attribute<Article, String> = add(Attr.ofString(cls(), "title").get { e -> e.title})
    val imageAttr: Attribute<Article, String> = add(Attr.ofString(cls(), "image").get { e -> e.image})
    val perexAttr: Attribute<Article, String> = add(Attr.ofString(cls(), "perex").get { e -> e.perex})
    val bodyMarkdownAttr: Attribute<Article, String> = add(Attr.ofString(cls(), "body_markdown").get { e -> e.bodyMarkdown})
    val bodyAttr: Attribute<Article, String> = add(Attr.ofString(cls(), "body").get { e -> e.body})
    val keywordsAttr: Attribute<Article, String> = add(Attr.ofString(cls(), "keywords").get { e -> e.keywords})
    val showOnBlogAttr: Attribute<Article, Boolean> = add(Attr.ofBoolean(cls(), "show_on_blog").get { e -> e.showOnBlog})
    val infoAttributes: List<Attribute<Article, *>> = listOf(
        idAttr,
        validFromAttr,
        createdAttr,
        createdByAttr,
        modifiedAttr,
        modifiedByAttr,
        slugAttr,
        titleAttr,
        imageAttr,
        perexAttr
    )

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<Article, *>>,
        aliasPrefix: String?
    ): Article {
        val projectedAttributeNames: Set<String> =
            attributes.map { it.getName() }.toSet()
        val assetAttributes: AssetAttributes =
            createAssetAttributes(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        val validity: Validity = createValidity(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        // TODO RBe: Implement getValueFromSourceOrElse
        return Article(assetAttributes, validity, if (projectedAttributeNames.contains(externalIdAttr.getName())) externalIdAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else null, if (projectedAttributeNames.contains(slugAttr.getName())) slugAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else "", if (projectedAttributeNames.contains(titleAttr.getName())) titleAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else "", if (projectedAttributeNames.contains(imageAttr.getName())) imageAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else null, if (projectedAttributeNames.contains(perexAttr.getName())) perexAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else "", if (projectedAttributeNames.contains(bodyMarkdownAttr.getName())) bodyMarkdownAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else "", if (projectedAttributeNames.contains(bodyAttr.getName())) bodyAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else "", if (projectedAttributeNames.contains(keywordsAttr.getName())) keywordsAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else "", if (projectedAttributeNames.contains(showOnBlogAttr.getName())) showOnBlogAttr.getValueFromSource(attributeSource, aliasPrefix ?: "") else false)
    }

    override fun composeFilterConditions(filter: ArticleFilter): MutableList<Condition> {
        val conditions = super.composeFilterConditions(filter)
        filter.slug?.let { slug -> conditions.add(Conditions.eq(this.slugAttr, slug)) }
        filter.showOnBlog?.let { showOnBlog ->
            conditions.add(
                Conditions.eq<Article, Boolean>(
                    this.showOnBlogAttr,
                    showOnBlog
                )
            )
        }
        filter.withExternalId?.let { withExternalId -> conditions.add(SqlCondition(externalIdAttr.name + " IS" +(if (withExternalId) " NOT" else "") + " NULL")) }
        filter.categoryId?.let { categoryId ->
            val params = mutableListOf<Any>()
            params.add(categoryId)
            conditions.add(
                SqlCondition("id IN (SELECT article_id FROM article_category WHERE category_id = ?)",
                    params
                )
            )
        }
        filter.query?.let { query ->
            val params = mutableListOf<Any>()
            params.add("%" + query + "%")
            params.add("%" + query + "%")
            params.add("%" + query + "%")
            conditions.add(
                SqlCondition(
                    "(${titleAttr.name} ILIKE ? OR ${perexAttr.name} ILIKE ? OR ${bodyAttr.name} ILIKE ?)",
                    params
                )
            )
        }
        return conditions
    }

    override fun entityWithCommonAttributes(entity: Article, common: AssetAttributes): Article =
        entity.copy(common = common)

    companion object {
        val Instance: ArticleMapper = ArticleMapper()
    }
}
