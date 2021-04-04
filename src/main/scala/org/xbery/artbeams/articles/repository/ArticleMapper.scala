package org.xbery.artbeams.articles.repository

import java.util

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.assets.domain.{AssetAttributes, Validity}
import org.xbery.artbeams.common.assets.repository.ValidityAssetMapper
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions
import org.xbery.overview.sql.filter.SqlCondition

/**
  * Maps {@link Article} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class ArticleMapper() extends ValidityAssetMapper[Article, ArticleFilter] {

  override protected def cls = classOf[Article]

  override val getTableName: String = "articles"

  override def createEntity(): Article = Article.Empty

  val externalIdAttr = add(Attr.ofString(cls, "external_id").get(e => e.externalId.orNull).updatedEntity((e, a) => e.copy(externalId = Option(a))))
  val slugAttr = add(Attr.ofString(cls, "slug").get(e => e.slug).updatedEntity((e, a) => e.copy(slug = a)))
  val titleAttr = add(Attr.ofString(cls, "title").get(e => e.title).updatedEntity((e, a) => e.copy(title = a)))
  val imageAttr = add(Attr.ofString(cls, "image").get(e => e.image.orNull).updatedEntity((e, a) => e.copy(image = Option(a))))
  val imageDetailAttr = add(Attr.ofString(cls, "image_detail").get(e => e.imageDetail.orNull).updatedEntity((e, a) => e.copy(imageDetail = Option(a))))
  val perexAttr = add(Attr.ofString(cls, "perex").get(e => e.perex).updatedEntity((e, a) => e.copy(perex = a)))
  val bodyMarkdownAttr = add(Attr.ofString(cls, "body_markdown").get(e => e.bodyMarkdown).updatedEntity((e, a) => e.copy(bodyMarkdown = a)))
  val bodyAttr = add(Attr.ofString(cls, "body").get(e => e.body).updatedEntity((e, a) => e.copy(body = a)))
  val keywordsAttr = add(Attr.ofString(cls, "keywords").get(e => e.keywords).updatedEntity((e, a) => e.copy(keywords = a)))
  val showOnBlogAttr = add(Attr.ofBoolean(cls, "show_on_blog").get(e => e.showOnBlog).updatedEntity((e, a) => e.copy(showOnBlog = a)))

  /** Attributes for loading short info about articles (for article lists) */
  lazy val infoAttributes = Seq(idAttr, validFromAttr, createdAttr, createdByAttr, modifiedAttr, modifiedByAttr, slugAttr, titleAttr, imageAttr, perexAttr)

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

  override def entityWithValidity(entity: Article, validity: Validity): Article = entity.copy(validity = validity)

}

object ArticleMapper {
  lazy val Instance = new ArticleMapper()
}
