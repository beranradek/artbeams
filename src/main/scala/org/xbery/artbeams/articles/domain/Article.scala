package org.xbery.artbeams.articles.domain

import java.util
import java.util.Date

import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes, Validity, ValidityAsset}

/**
  * Article entity
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class Article(
  override val common: AssetAttributes,
  override val validity: Validity,
  externalId: Option[String],
  slug: String,
  title: String,
  image: Option[String],
  perex: String,
  bodyMarkdown: String,
  body: String,
  keywords: String,
  showOnBlog: Boolean,
) extends Asset with ValidityAsset {

  def updatedWith(edited: EditedArticle, htmlBody: String, userId: String): Article = {
    this.copy(
      common = this.common.updatedWith(userId),
      validity = this.validity.updatedWith(edited),
      externalId = edited.externalId.flatMap(extId => if (extId.trim == "") None else Option(extId)),
      slug = edited.slug,
      title = edited.title,
      image = edited.image.flatMap(img => if (img.trim == "") None else Option(img)),
      perex = edited.perex,
      bodyMarkdown = edited.bodyMarkdown,
      body = htmlBody,
      keywords = edited.keywords,
      showOnBlog = edited.showOnBlog
    )
  }

  def toEdited(categories: util.List[String]): EditedArticle = {
    EditedArticle(
      this.id,
      this.externalId,
      this.slug,
      this.title,
      this.image,
      this.perex,
      this.bodyMarkdown,
      if (this.validFrom == null || this.validFrom == AssetAttributes.EmptyDate) { new Date() } else { new Date(this.validFrom.toEpochMilli()) },
      this.validTo.map(d => new Date(d.toEpochMilli())),
      this.keywords,
      this.showOnBlog,
      categories
    )
  }
}

object Article {
  final val CacheName = "articles"

  lazy val Empty = Article(
    AssetAttributes.Empty,
    Validity.Empty,
    None,
    "",
    "New article",
    None,
    "",
    "",
    "",
    "",
    true
  )

  lazy val EmptyEdited = Empty.toEdited(new util.ArrayList[String]())
}
