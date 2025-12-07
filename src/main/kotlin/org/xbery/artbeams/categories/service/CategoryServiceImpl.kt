package org.xbery.artbeams.categories.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.repository.ArticleCategoryRepository
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.domain.EditedCategory
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx

/**
 * @author Radek Beran
 */
@Service
open class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val articleCategoryRepository: ArticleCategoryRepository
) : CategoryService {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val ARTICLE_CATEGORIES_CACHE_NAME = "articleCategories"
    }

    @Cacheable(Category.CacheName)
    override fun findCategories(): List<Category> {
        logger.info("Finding categories")
        return categoryRepository.findCategories()
    }

    @Cacheable(ARTICLE_CATEGORIES_CACHE_NAME, key = "#articleId")
    override fun findCategoriesByArticleId(articleId: String): List<Category> {
        logger.info("Finding categories for article $articleId")
        val categoryIds = articleCategoryRepository.findArticleCategoryIdsByArticleId(articleId)
        // Use cached findCategories() result and filter in memory
        // This is efficient because findCategories() result is cached
        val allCategories = findCategories()
        return allCategories.filter { categoryIds.contains(it.id) }
    }

    @CacheEvict(value = [Category.CacheName, ARTICLE_CATEGORIES_CACHE_NAME], allEntries = true)
    override fun saveCategory(edited: EditedCategory, ctx: OperationCtx): Category? {
        return try {
            val userId = ctx.loggedUser?.id ?: AssetAttributes.EMPTY_ID
            val updatedCategoryOpt: Category? = if (edited.id == AssetAttributes.EMPTY_ID) {
                categoryRepository.create(Category.Empty.updatedWith(edited, userId))
            } else {
                val category = categoryRepository.requireById(edited.id)
                categoryRepository.update(category.updatedWith(edited, userId))
            }
            updatedCategoryOpt
        } catch (ex: Exception) {
            logger.error("Update of category ${edited.id} finished with error ${ex.message}", ex)
            throw ex
        }
    }

    @Cacheable(Category.CacheName)
    override fun findBySlug(slug: String): Category? = categoryRepository.findBySlug(slug)
}
