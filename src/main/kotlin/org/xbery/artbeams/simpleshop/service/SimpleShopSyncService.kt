package org.xbery.artbeams.simpleshop.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.prices.domain.Price
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.simpleshop.config.SimpleShopConfig

/**
 * Service for synchronizing products with SimpleShop.cz.
 * @author Radek Beran
 */
@Service
class SimpleShopSyncService(
    private val apiClient: SimpleShopApiClient,
    private val productRepository: ProductRepository,
    private val config: SimpleShopConfig
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Result of product synchronization.
     */
    data class SyncResult(
        val productId: String,
        val success: Boolean,
        val message: String,
        val updatedFields: List<String> = emptyList()
    )

    /**
     * Synchronize a single product from SimpleShop.
     * Updates the product's name (title) and price if they differ from SimpleShop data.
     *
     * @param product Product to synchronize
     * @param ctx Operation context
     * @return Sync result
     */
    fun syncProduct(product: Product, ctx: OperationCtx): SyncResult {
        if (!config.isConfigured()) {
            return SyncResult(
                productId = product.id,
                success = false,
                message = "SimpleShop API is not configured"
            )
        }

        val simpleShopProductId = product.simpleShopProductId
        if (simpleShopProductId.isNullOrBlank()) {
            return SyncResult(
                productId = product.id,
                success = false,
                message = "Product does not have SimpleShop Product ID set"
            )
        }

        logger.info("Syncing product ${product.id} with SimpleShop product $simpleShopProductId")

        val simpleShopProduct = apiClient.getProduct(simpleShopProductId)
        if (simpleShopProduct == null) {
            return SyncResult(
                productId = product.id,
                success = false,
                message = "Failed to fetch product from SimpleShop (ID: $simpleShopProductId)"
            )
        }

        // Determine what needs to be updated
        val updatedFields = mutableListOf<String>()
        var updatedProduct = product

        // Update title if different and non-empty in SimpleShop
        val newTitle = simpleShopProduct.title ?: simpleShopProduct.name
        if (newTitle.isNotBlank() && newTitle != product.title) {
            updatedProduct = updatedProduct.copy(title = newTitle)
            updatedFields.add("title")
            logger.info("Updating title from '${product.title}' to '$newTitle'")
        }

        // Update price if different and non-null in SimpleShop
        simpleShopProduct.price?.let { newPrice ->
            if (newPrice != product.priceRegular.price) {
                updatedProduct = updatedProduct.copy(
                    priceRegular = Price(newPrice, Price.DEFAULT_CURRENCY)
                )
                updatedFields.add("price")
                logger.info("Updating price from '${product.priceRegular.price}' to '$newPrice'")
            }
        }

        // Save if there are changes
        if (updatedFields.isNotEmpty()) {
            // Update modified timestamp
            updatedProduct = updatedProduct.copy(
                common = updatedProduct.common.updatedWith(ctx.loggedUser?.id!!)
            )
            productRepository.update(updatedProduct)
            logger.info("Product ${product.id} synchronized successfully. Updated fields: ${updatedFields.joinToString(", ")}")
            return SyncResult(
                productId = product.id,
                success = true,
                message = "Product synchronized successfully",
                updatedFields = updatedFields
            )
        } else {
            logger.info("Product ${product.id} is already up to date with SimpleShop")
            return SyncResult(
                productId = product.id,
                success = true,
                message = "Product is already up to date",
                updatedFields = emptyList()
            )
        }
    }

    /**
     * Synchronize all products that have SimpleShop Product ID set.
     *
     * @param ctx Operation context
     * @return List of sync results
     */
    fun syncAllProducts(ctx: OperationCtx): List<SyncResult> {
        logger.info("Starting synchronization of all products with SimpleShop")

        val allProducts = productRepository.findProducts()
        val productsToSync = allProducts.filter { !it.simpleShopProductId.isNullOrBlank() }

        logger.info("Found ${productsToSync.size} products to synchronize")

        val results = productsToSync.map { product ->
            try {
                syncProduct(product, ctx)
            } catch (e: Exception) {
                logger.error("Error syncing product ${product.id}: ${e.message}", e)
                SyncResult(
                    productId = product.id,
                    success = false,
                    message = "Error: ${e.message}"
                )
            }
        }

        val successCount = results.count { it.success }
        val updatedCount = results.count { it.updatedFields.isNotEmpty() }
        logger.info("Synchronization completed. Success: $successCount/${results.size}, Updated: $updatedCount")

        return results
    }
}
