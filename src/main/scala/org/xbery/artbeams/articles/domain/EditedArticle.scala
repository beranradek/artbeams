package org.xbery.artbeams.articles.domain

import java.util.Date

import net.formio.binding.ArgumentName
import org.xbery.artbeams.common.assets.domain.EditedTimeValidity

/**
  * @author Radek Beran
  */
case class EditedArticle(
  @ArgumentName("id")
  id: String,
  @ArgumentName("externalId")
  externalId: Option[String],
  @ArgumentName("slug")
  slug: String,
  @ArgumentName("title")
  title: String,
  @ArgumentName("image")
  image: Option[String],
  @ArgumentName("perex")
  perex: String,
  @ArgumentName("bodyMarkdown")
  bodyMarkdown: String,
  @ArgumentName("validFrom")
  override val validFrom: Date,
  @ArgumentName("validTo")
  override val validTo: Option[Date],
  @ArgumentName("keywords")
  keywords: String,
  @ArgumentName("showOnBlog")
  showOnBlog: Boolean = false,
  @ArgumentName("showOnBlog")
  categories: java.util.List[String]
) extends EditedTimeValidity
