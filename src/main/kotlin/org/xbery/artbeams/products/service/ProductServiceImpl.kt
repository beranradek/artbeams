package org.xbery.artbeams.products.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.products.domain.EditedProduct
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.search.service.SearchIndexer

/**
 * @author Radek Beran
 */
@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val searchIndexer: SearchIndexer
) : ProductService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun findProducts(): List<Product> {
        return productRepository.findProducts()
    }

    override fun saveProduct(edited: EditedProduct, ctx: OperationCtx): Product? {
        return try {
            val userId = ctx.loggedUser?.id ?: AssetAttributes.EMPTY_ID
            val updatedProduct = if (edited.id == AssetAttributes.EMPTY_ID) {
                productRepository.create(Product.Empty.updatedWith(edited, userId))
            } else {
                val product = productRepository.requireById(edited.id)
                productRepository.update(product.updatedWith(edited, userId))
            }

            // Update search index
            updatedProduct?.let { searchIndexer.indexProduct(it) }

            updatedProduct
        } catch (ex: Exception) {
            logger.error("Update of Product ${edited.id} finished with error ${ex.message}", ex)
            throw ex
        }
    }

    override fun findBySlug(slug: String): Product? = productRepository.findBySlug(slug)

    override fun requireBySlug(slug: String): Product = productRepository.requireBySlug(slug)
}
