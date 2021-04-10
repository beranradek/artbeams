package org.xbery.artbeams.products.domain

import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes}

/**
  * Product entity.
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class Product(
  override val common: AssetAttributes,
  slug: String,
  title: String,
  /** File name within {@link FileData} in media module. Filled if this is electronic product that can be download. */
  fileName: Option[String]
  // TODO: Add price (decimal number)
) extends Asset {

  def updatedWith(edited: EditedProduct, userId: String): Product = {
    this.copy(
      common = this.common.updatedWith(userId),
      slug = edited.slug,
      title = edited.title,
      fileName = edited.fileName
    )
  }

  def toEdited(): EditedProduct = {
    EditedProduct(
      this.id,
      this.slug,
      this.title,
      this.fileName
    )
  }
}

object Product {
  lazy val Empty = Product(
    AssetAttributes.Empty,
    "",
    "New product",
    None
  )
}
