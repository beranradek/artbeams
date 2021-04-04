package org.xbery.artbeams.products.service

import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.products.domain.{EditedProduct, Product}

/**
  * @author Radek Beran
  */
trait ProductService {
  def findProducts(): Seq[Product]

  def saveProduct(edited: EditedProduct)(implicit ctx: OperationCtx): Either[Exception, Option[Product]]

  def findBySlug(slug: String): Option[Product]
}
