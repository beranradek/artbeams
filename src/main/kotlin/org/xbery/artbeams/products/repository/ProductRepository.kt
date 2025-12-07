package org.xbery.artbeams.products.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.jooq.schema.tables.records.ProductsRecord
import org.xbery.artbeams.jooq.schema.tables.references.PRODUCTS
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.repository.mapper.ProductMapper
import org.xbery.artbeams.products.repository.mapper.ProductUnmapper

/**
 * Product repository.
 * @author Radek Beran
 */
@Repository
class ProductRepository(
    override val dsl: DSLContext,
    override val mapper: ProductMapper,
    override val unmapper: ProductUnmapper
) : AssetRepository<Product, ProductsRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<ProductsRecord> = PRODUCTS
    override val idField: Field<String?> = PRODUCTS.ID

    fun findProducts(): List<Product> =
        dsl.selectFrom(table).orderBy(PRODUCTS.TITLE).fetch(mapper)

    fun findProducts(pagination: Pagination): ResultPage<Product> {
        // Get total count
        val totalCount = dsl.selectCount()
            .from(table)
            .fetchOne(0, Long::class.java) ?: 0L

        // Get paginated products
        val products = dsl.selectFrom(table)
            .orderBy(PRODUCTS.TITLE)
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        return ResultPage(products, pagination.withTotalCount(totalCount))
    }

    fun searchProducts(searchTerm: String?, pagination: Pagination): ResultPage<Product> {
        var condition: org.jooq.Condition = org.jooq.impl.DSL.trueCondition()

        // Add search term filter (title, slug, subtitle)
        if (!searchTerm.isNullOrBlank()) {
            val searchLower = searchTerm.lowercase()
            condition = condition.and(
                org.jooq.impl.DSL.lower(PRODUCTS.TITLE).contains(searchLower)
                    .or(org.jooq.impl.DSL.lower(PRODUCTS.SLUG).contains(searchLower))
                    .or(org.jooq.impl.DSL.lower(PRODUCTS.SUBTITLE).contains(searchLower))
            )
        }

        // Get total count with filters
        val totalCount = dsl.selectCount()
            .from(table)
            .where(condition)
            .fetchOne(0, Long::class.java) ?: 0L

        // Get paginated products with filters
        val products = dsl.selectFrom(table)
            .where(condition)
            .orderBy(PRODUCTS.TITLE)
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        return ResultPage(products, pagination.withTotalCount(totalCount))
    }

    fun findBySlug(slug: String): Product? =
        dsl.selectFrom(table)
            .where(PRODUCTS.SLUG.eq(slug))
            .fetchOne(mapper)

    fun requireBySlug(slug: String): Product =
        requireFound(findBySlug(slug)) {
            "Product with slug $slug was not found"
        }
}
