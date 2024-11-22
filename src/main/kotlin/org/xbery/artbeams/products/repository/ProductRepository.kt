package org.xbery.artbeams.products.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
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

    fun findBySlug(slug: String): Product? =
        dsl.selectFrom(table)
            .where(PRODUCTS.SLUG.eq(slug))
            .fetchOne(mapper)
}
