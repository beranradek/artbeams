package org.xbery.artbeams.products.service

import javax.inject.Inject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.products.domain.{EditedProduct, Product}
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.users.repository.UserRepository

/**
  * @author Radek Beran
  */
@Service
class ProductServiceImpl @Inject()(productRepository: ProductRepository, userRepository: UserRepository) extends ProductService {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  override def findProducts(): Seq[Product] = {
    productRepository.findProducts()
  }

  override def saveProduct(edited: EditedProduct)(implicit ctx: OperationCtx): Either[Exception, Option[Product]] = {
    try {
      val userId = ctx.loggedUser.map(_.id).getOrElse(AssetAttributes.EmptyId)
      val updatedProductOpt = if (edited.id == AssetAttributes.EmptyId) {
        Option(productRepository.create(Product.Empty.updatedWith(edited, userId)))
      } else {
        productRepository.findByIdAsOpt(edited.id) flatMap { product =>
          productRepository.updateEntity(product.updatedWith(edited, userId))
        }
      }
      Right(updatedProductOpt)
    } catch {
      case ex: Exception =>
        logger.error(s"Update of Product ${edited.id} finished with error ${ex.getMessage()}", ex)
        Left(ex)
    }
  }

  override def findBySlug(slug: String): Option[Product] = productRepository.findBySlug(slug)
}
