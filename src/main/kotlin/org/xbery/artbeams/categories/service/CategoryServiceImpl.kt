package org.xbery.artbeams.categories.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.domain.EditedCategory
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx

/**
 * @author Radek Beran
 */
@Service
open class CategoryServiceImpl(private val categoryRepository: CategoryRepository) : CategoryService {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Cacheable(Category.CacheName)
    override fun findCategories(): List<Category> {
        logger.info("Finding categories")
        return categoryRepository.findCategories()
    }

    @CacheEvict(value = [Category.CacheName], allEntries = true)
    override fun saveCategory(edited: EditedCategory, ctx: OperationCtx): Category? {
        return try {
            val userId = ctx.loggedUser?.id ?: AssetAttributes.EmptyId
            val updatedCategoryOpt: Category? = if (edited.id == AssetAttributes.EmptyId) {
                categoryRepository.create(Category.Empty.updatedWith(edited, userId))
            } else {
                val category = categoryRepository.findByIdAsOpt(edited.id)
                if (category != null) {
                    categoryRepository.updateEntity(category.updatedWith(edited,
                        userId
                    ))
                } else {
                    null
                }
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
