package org.xbery.artbeams.categories.service

import org.xbery.artbeams.categories.domain.{Category, EditedCategory}
import org.xbery.artbeams.common.context.OperationCtx

/**
  * @author Radek Beran
  */
trait CategoryService {
  def findCategories(): Seq[Category]

  def saveCategory(edited: EditedCategory)(implicit ctx: OperationCtx): Either[Exception, Option[Category]]

  def findBySlug(slug: String): Option[Category]
}
