package org.xbery.artbeams.categories.domain

import java.util.Date

import net.formio.binding.ArgumentName
import org.xbery.artbeams.common.assets.domain.EditedTimeValidity

/**
  * @author Radek Beran
  */
case class EditedCategory(
  @ArgumentName("id")
  id: String,
  @ArgumentName("slug")
  slug: String,
  @ArgumentName("title")
  title: String,
  @ArgumentName("description")
  description: String,
  @ArgumentName("validFrom")
  override val validFrom: Date,
  @ArgumentName("validTo")
  override val validTo: Option[Date]
) extends EditedTimeValidity
