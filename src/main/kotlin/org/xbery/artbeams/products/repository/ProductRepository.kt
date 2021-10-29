package org.xbery.artbeams.products.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.products.domain.Product
import org.xbery.overview.Order
import org.xbery.overview.Overview
import javax.sql.DataSource

/**
 * Product repository.
 * @author Radek Beran
 */
@Repository
open class ProductRepository(dataSource: DataSource) :
    AssetRepository<Product, ProductFilter>(dataSource, ProductMapper.Instance) {
    protected val DefaultOrdering: List<Order> = listOf(Order((entityMapper as ProductMapper).titleAttr, false))

    open fun findProducts(): List<Product> {
        val overview = Overview(ProductFilter.Empty, DefaultOrdering)
        return findByOverview(overview)
    }

    open fun findBySlug(slug: String): Product? {
        val filter: ProductFilter = ProductFilter.Empty.copy(slug = slug)
        return this.findOneByFilter(filter)
    }
}
