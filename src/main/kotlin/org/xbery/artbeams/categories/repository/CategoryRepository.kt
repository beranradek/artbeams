package org.xbery.artbeams.categories.repository

import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.repository.mapper.CategoryMapper
import org.xbery.artbeams.categories.repository.mapper.CategoryUnmapper
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.jooq.schema.tables.records.CategoriesRecord
import org.xbery.artbeams.jooq.schema.tables.references.CATEGORIES
import java.time.Instant

/**
 * Category repository.
 * @author Radek Beran
 */
@Repository
class CategoryRepository(
    override val dsl: DSLContext,
    override val mapper: CategoryMapper,
    override val unmapper: CategoryUnmapper
) : AssetRepository<Category, CategoriesRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<CategoriesRecord> = CATEGORIES
    override val idField: Field<String?> = CATEGORIES.ID

    fun findCategories(): List<Category> =
        dsl.selectFrom(table)
            .orderBy(CATEGORIES.TITLE)
            .fetch(mapper)

    fun findCategories(pagination: Pagination): ResultPage<Category> {
        // Get total count
        val totalCount = dsl.selectCount()
            .from(table)
            .fetchOne(0, Long::class.java) ?: 0L

        // Get paginated categories
        val categories = dsl.selectFrom(table)
            .orderBy(CATEGORIES.TITLE)
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        return ResultPage(categories, pagination.withTotalCount(totalCount))
    }

    fun findBySlug(slug: String): Category? =
        dsl.selectFrom(table)
            .where(CATEGORIES.SLUG.eq(slug).and(validityCondition(Instant.now())))
            .fetchOne(mapper)

    protected fun validityCondition(validityDate: Instant): Condition =
        CATEGORIES.VALID_FROM.lessOrEqual(validityDate)
            .and(
                CATEGORIES.VALID_TO.isNull
                .or(CATEGORIES.VALID_TO.greaterOrEqual(validityDate))
            )
}
