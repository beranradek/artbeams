package org.xbery.artbeams.articles.repository

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.ValidityAssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions
import org.xbery.overview.sql.filter.SqlCondition
import scala.jdk.CollectionConverters._

import java.util

/**
  * Maps {@link Article} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class ArticleMapper() extends ValidityAssetMapper[Article, ArticleFilter] {

  override protected def cls = classOf[Article]

  override val getTableName: String = "articles"

  val externalIdAttr = add(Attr.ofString(cls, "external_id").get(e => e.externalId.orNull))
  val slugAttr = add(Attr.ofString(cls, "slug").get(e => e.slug))
  val titleAttr = add(Attr.ofString(cls, "title").get(e => e.title))
  val imageAttr = add(Attr.ofString(cls, "image").get(e => e.image.orNull))
  val imageDetailAttr = add(Attr.ofString(cls, "image_detail").get(e => e.imageDetail.orNull))
  val perexAttr = add(Attr.ofString(cls, "perex").get(e => e.perex))
  val bodyMarkdownAttr = add(Attr.ofString(cls, "body_markdown").get(e => e.bodyMarkdown))
  val bodyAttr = add(Attr.ofString(cls, "body").get(e => e.body))
  val keywordsAttr = add(Attr.ofString(cls, "keywords").get(e => e.keywords))
  val showOnBlogAttr = add(Attr.ofBoolean(cls, "show_on_blog").get(e => e.showOnBlog))

  /** Attributes for loading short info about articles (for article lists) */
  lazy val infoAttributes = Seq(idAttr, validFromAttr, createdAttr, createdByAttr, modifiedAttr, modifiedByAttr, slugAttr, titleAttr, imageAttr, perexAttr)

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[Article, _]], aliasPrefix: String): Article = {
    val projectedAttributeNames = attributes.asScala.map(_.getName).toSet
    val assetAttributes = createAssetAttributes(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    val validity = createValidity(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    Article(
      assetAttributes,
      validity,
      // TODO RBe: Implement getValueFromSourceOptElse
      if (projectedAttributeNames.contains(externalIdAttr.getName)) Option(externalIdAttr.getValueFromSource(attributeSource, aliasPrefix)) else None,
      if (projectedAttributeNames.contains(slugAttr.getName)) slugAttr.getValueFromSource(attributeSource, aliasPrefix) else "",
      if (projectedAttributeNames.contains(titleAttr.getName)) titleAttr.getValueFromSource(attributeSource, aliasPrefix) else "",
      if (projectedAttributeNames.contains(imageAttr.getName)) Option(imageAttr.getValueFromSource(attributeSource, aliasPrefix)) else None,
      if (projectedAttributeNames.contains(imageDetailAttr.getName)) Option(imageDetailAttr.getValueFromSource(attributeSource, aliasPrefix)) else None,
      if (projectedAttributeNames.contains(perexAttr.getName)) perexAttr.getValueFromSource(attributeSource, aliasPrefix) else "",
      if (projectedAttributeNames.contains(bodyMarkdownAttr.getName)) bodyMarkdownAttr.getValueFromSource(attributeSource, aliasPrefix) else "",
      if (projectedAttributeNames.contains(bodyAttr.getName)) bodyAttr.getValueFromSource(attributeSource, aliasPrefix) else "",
      if (projectedAttributeNames.contains(keywordsAttr.getName)) keywordsAttr.getValueFromSource(attributeSource, aliasPrefix) else "",
      if (projectedAttributeNames.contains(showOnBlogAttr.getName)) showOnBlogAttr.getValueFromSource(attributeSource, aliasPrefix) else false
    )
  }

  override def composeFilterConditions(filter: ArticleFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.slug.map(slug => conditions.add(Conditions.eq(this.slugAttr, slug)))
    filter.showOnBlog.map(showOnBlog => conditions.add(Conditions.eq[Article, java.lang.Boolean](this.showOnBlogAttr, showOnBlog)))
    filter.withExternalId.map(withExternalId => conditions.add(new SqlCondition(externalIdAttr.getName() + " IS" + (if (withExternalId) " NOT" else "") + " NULL")))
    filter.categoryId.map(categoryId => {
      val params = new util.ArrayList[Object]()
      params.add(categoryId)
      conditions.add(new SqlCondition("id IN (SELECT article_id FROM article_category WHERE category_id = ?)", params))
    })
    filter.query.map(query => {
      // Note: ILIKE is Postgre SQL extension - case insensitive LIKE
      val params = new util.ArrayList[Object]()
      params.add("%" + query + "%") // for title
      params.add("%" + query + "%") // for perex
      params.add("%" + query + "%") // for body
      conditions.add(new SqlCondition(s"(${titleAttr.getName()} ILIKE ? OR ${perexAttr.getName()} ILIKE ? OR ${bodyAttr.getName()} ILIKE ?)", params))
    })
    conditions
  }

  override def entityWithCommonAttributes(entity: Article, common: AssetAttributes): Article = entity.copy(common = common)
}

object ArticleMapper {
  lazy val Instance = new ArticleMapper()
}
