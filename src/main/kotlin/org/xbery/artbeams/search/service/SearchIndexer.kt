package org.xbery.artbeams.search.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.text.NormalizedStringUtils
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.search.domain.EntityType
import org.xbery.artbeams.search.domain.SearchIndexEntry
import org.xbery.artbeams.search.repository.SearchIndexRepository
import java.time.Instant
import java.util.UUID

/**
 * Service for indexing entities into the search index.
 * Handles creating, updating, and deleting search index entries.
 * @author Radek Beran
 */
@Service
class SearchIndexer(
    private val searchIndexRepository: SearchIndexRepository,
    private val articleRepository: ArticleRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Index an article in the search index.
     */
    fun indexArticle(article: Article) {
        try {
            val existing = searchIndexRepository.findByEntity(EntityType.ARTICLE, article.id)
            val entry = createArticleIndexEntry(article, existing?.id)

            if (existing != null) {
                searchIndexRepository.update(entry)
                logger.debug("Updated search index for article: ${article.id}")
            } else {
                searchIndexRepository.create(entry)
                logger.debug("Created search index for article: ${article.id}")
            }
        } catch (e: Exception) {
            logger.error("Failed to index article ${article.id}: ${e.message}", e)
        }
    }

    /**
     * Index a category in the search index.
     */
    fun indexCategory(category: Category) {
        try {
            val existing = searchIndexRepository.findByEntity(EntityType.CATEGORY, category.id)
            val entry = createCategoryIndexEntry(category, existing?.id)

            if (existing != null) {
                searchIndexRepository.update(entry)
                logger.debug("Updated search index for category: ${category.id}")
            } else {
                searchIndexRepository.create(entry)
                logger.debug("Created search index for category: ${category.id}")
            }
        } catch (e: Exception) {
            logger.error("Failed to index category ${category.id}: ${e.message}", e)
        }
    }

    /**
     * Index a product in the search index.
     */
    fun indexProduct(product: Product) {
        try {
            val existing = searchIndexRepository.findByEntity(EntityType.PRODUCT, product.id)
            val entry = createProductIndexEntry(product, existing?.id)

            if (existing != null) {
                searchIndexRepository.update(entry)
                logger.debug("Updated search index for product: ${product.id}")
            } else {
                searchIndexRepository.create(entry)
                logger.debug("Created search index for product: ${product.id}")
            }
        } catch (e: Exception) {
            logger.error("Failed to index product ${product.id}: ${e.message}", e)
        }
    }

    /**
     * Delete search index entry for an entity.
     */
    fun deleteIndex(entityType: EntityType, entityId: String) {
        try {
            searchIndexRepository.deleteByEntity(entityType, entityId)
            logger.debug("Deleted search index for $entityType: $entityId")
        } catch (e: Exception) {
            logger.error("Failed to delete search index for $entityType $entityId: ${e.message}", e)
        }
    }

    /**
     * Reindex all entities.
     * This should be run nightly or on-demand to ensure search index is up-to-date.
     */
    fun reindexAll() {
        logger.info("Starting full reindex of search data")
        try {
            val startTime = System.currentTimeMillis()

            // Delete all existing entries
            searchIndexRepository.deleteAll()
            logger.info("Cleared existing search index")

            // Reindex all articles
            val articles = articleRepository.findLatest(10000) // Get all articles
            articles.forEach { article ->
                try {
                    val entry = createArticleIndexEntry(article, null)
                    searchIndexRepository.create(entry)
                } catch (e: Exception) {
                    logger.error("Failed to reindex article ${article.id}: ${e.message}")
                }
            }
            logger.info("Reindexed ${articles.size} articles")

            // Reindex all categories
            val categories = categoryRepository.findCategories()
            categories.forEach { category ->
                try {
                    val entry = createCategoryIndexEntry(category, null)
                    searchIndexRepository.create(entry)
                } catch (e: Exception) {
                    logger.error("Failed to reindex category ${category.id}: ${e.message}")
                }
            }
            logger.info("Reindexed ${categories.size} categories")

            // Reindex all products
            val products = productRepository.findProducts()
            products.forEach { product ->
                try {
                    val entry = createProductIndexEntry(product, null)
                    searchIndexRepository.create(entry)
                } catch (e: Exception) {
                    logger.error("Failed to reindex product ${product.id}: ${e.message}")
                }
            }
            logger.info("Reindexed ${products.size} products")

            val duration = System.currentTimeMillis() - startTime
            logger.info("Full reindex completed in ${duration}ms. Total entries: ${articles.size + categories.size + products.size}")
        } catch (e: Exception) {
            logger.error("Full reindex failed: ${e.message}", e)
            throw e
        }
    }

    private fun createArticleIndexEntry(article: Article, existingId: String?): SearchIndexEntry {
        val searchText = buildString {
            append(article.title)
            append(" ")
            append(article.perex)
            append(" ")
            append(article.keywords)
        }

        return SearchIndexEntry(
            id = existingId ?: UUID.randomUUID().toString(),
            entityType = EntityType.ARTICLE,
            entityId = article.id,
            title = article.title,
            description = article.perex,
            keywords = article.keywords,
            slug = article.slug,
            searchVector = null, // Will be generated by database
            metadata = mapOf(
                "image" to article.image,
                "showOnBlog" to article.showOnBlog
            ),
            validFrom = article.validFrom,
            validTo = article.validTo,
            created = article.created,
            modified = article.modified
        )
    }

    private fun createCategoryIndexEntry(category: Category, existingId: String?): SearchIndexEntry {
        return SearchIndexEntry(
            id = existingId ?: UUID.randomUUID().toString(),
            entityType = EntityType.CATEGORY,
            entityId = category.id,
            title = category.title,
            description = category.description,
            keywords = "",
            slug = category.slug,
            searchVector = null, // Will be generated by database
            metadata = emptyMap(),
            validFrom = category.validFrom,
            validTo = category.validTo,
            created = category.created,
            modified = category.modified
        )
    }

    private fun createProductIndexEntry(product: Product, existingId: String?): SearchIndexEntry {
        val description = product.subtitle ?: ""

        return SearchIndexEntry(
            id = existingId ?: UUID.randomUUID().toString(),
            entityType = EntityType.PRODUCT,
            entityId = product.id,
            title = product.title,
            description = description,
            keywords = "",
            slug = product.slug,
            searchVector = null, // Will be generated by database
            metadata = mapOf(
                "image" to product.image,
                "listingImage" to product.listingImage,
                "priceRegular" to product.priceRegular.price.toString(),
                "priceDiscounted" to product.priceDiscounted?.price?.toString()
            ),
            validFrom = null, // Products don't have validity period
            validTo = null,
            created = product.created,
            modified = product.modified
        )
    }
}
