package org.xbery.artbeams.products.domain

import net.formio.binding.ArgumentName

/**
  * @author Radek Beran
  */
case class EditedProduct(
  @ArgumentName("id")
  id: String,
  @ArgumentName("slug")
  slug: String,
  @ArgumentName("title")
  title: String,
  @ArgumentName("fileName")
  fileName: Option[String]
)
