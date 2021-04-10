package org.xbery.artbeams.categories.domain

import java.util.Date

import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes, Validity, ValidityAsset}

/**
  * Category entity
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class Category(
  override val common: AssetAttributes,
  override val validity: Validity,
  slug: String,
  title: String,
  description: String
) extends Asset with ValidityAsset {

  def updatedWith(edited: EditedCategory, userId: String): Category = {
    this.copy(
      common = this.common.updatedWith(userId),
      validity = this.validity.updatedWith(edited),
      slug = edited.slug,
      title = edited.title,
      description = edited.description
    )
  }

  def toEdited(): EditedCategory = {
    EditedCategory(
      this.id,
      this.slug,
      this.title,
      this.description,
      if (this.validFrom == null || this.validFrom == AssetAttributes.EmptyDate) { new Date() } else { new Date(this.validFrom.toEpochMilli()) },
      this.validTo.map(d => new Date(d.toEpochMilli()))
    )
  }
}

object Category {
  final val CacheName = "categories"

  lazy val Empty = Category(
    AssetAttributes.Empty,
    Validity.Empty,
    "",
    "New category",
    ""
  )
}
