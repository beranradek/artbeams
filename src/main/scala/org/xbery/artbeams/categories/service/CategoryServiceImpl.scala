package org.xbery.artbeams.categories.service

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.{CacheEvict, Cacheable}
import org.springframework.stereotype.Service
import org.xbery.artbeams.categories.domain.{Category, EditedCategory}
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx

import javax.inject.Inject

/**
  * @author Radek Beran
  */
@Service
class CategoryServiceImpl @Inject()(categoryRepository: CategoryRepository) extends CategoryService {
  protected lazy val logger = LoggerFactory.getLogger(this.getClass)

  @Cacheable(Array(Category.CacheName))
  override def findCategories(): Seq[Category] = {
    logger.info("Finding categories")
    categoryRepository.findCategories()
  }

  @CacheEvict(value = Array(Category.CacheName), allEntries = true)
  override def saveCategory(edited: EditedCategory)(implicit ctx: OperationCtx): Either[Exception, Option[Category]] = {
    try {
      val userId = ctx.loggedUser.map(_.id).getOrElse(AssetAttributes.EmptyId)
      val updatedCategoryOpt = if (edited.id == AssetAttributes.EmptyId) {
        Option(categoryRepository.create(Category.Empty.updatedWith(edited, userId)))
      } else {
        categoryRepository.findByIdAsOpt(edited.id) flatMap { category =>
          categoryRepository.updateEntity(category.updatedWith(edited, userId))
        }
      }
      Right(updatedCategoryOpt)
    } catch {
      case ex: Exception =>
        logger.error(s"Update of category ${edited.id} finished with error ${ex.getMessage()}", ex)
        Left(ex)
    }
  }

  @Cacheable(Array(Category.CacheName))
  override def findBySlug(slug: String): Option[Category] = categoryRepository.findBySlug(slug)
}
