package org.xbery.artbeams.categories.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.overview.Order
import org.xbery.overview.Overview
import java.time.Instant
import javax.sql.DataSource

/**
 * Category repository.
 * @author Radek Beran
 */
@Repository
open class CategoryRepository(dataSource: DataSource) :
    AssetRepository<Category, CategoryFilter>(dataSource, CategoryMapper.Instance) {
    private val DefaultOrdering: List<Order> = listOf(Order((entityMapper as CategoryMapper).titleAttr, false))

    open fun findCategories(): List<Category> {
        val overview: Overview<CategoryFilter> = Overview<CategoryFilter>(CategoryFilter.Empty, DefaultOrdering)
        return findByOverview(overview)
    }

    open fun findBySlug(slug: String): Category? {
        val filter: CategoryFilter =
            CategoryFilter.Empty.copy(slug = slug, validityDate = Instant.now())
        return this.findOneByFilter(filter)
    }
}
