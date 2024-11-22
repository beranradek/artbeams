package org.xbery.artbeams.articles.repository

import org.jooq.*
import org.springframework.stereotype.Repository
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.mapper.ArticleMapper
import org.xbery.artbeams.articles.repository.mapper.ArticleUnmapper
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.jooq.schema.tables.records.ArticlesRecord
import org.xbery.artbeams.jooq.schema.tables.references.ARTICLES
import org.xbery.artbeams.jooq.schema.tables.references.ARTICLE_CATEGORY
import java.time.Instant

/**
 * Article repository.
 * @author Radek Beran
 */
@Repository
class ArticleRepository(
    override val dsl: DSLContext,
    override val mapper: ArticleMapper,
    override val unmapper: ArticleUnmapper
) : AssetRepository<Article, ArticlesRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<ArticlesRecord> = ARTICLES
    override val idField: Field<String?> = ARTICLES.ID

    fun findArticles(): List<Article> =
        dsl.select(INFO_ATTRIBUTES)
            .from(table)
            .orderBy(ARTICLES.MODIFIED.desc())
            .fetch(articleInfoMapper())

    fun findLatest(limit: Int): List<Article> {
        val validityDate = Instant.now()
        val whereCondition = validityCondition(validityDate).and(ARTICLES.SHOW_ON_BLOG.isTrue)
        return findByCriteriaWithLimit(
            INFO_ATTRIBUTES,
            whereCondition,
            defaultOrdering(),
            limit,
            articleInfoMapper()
        )
    }

    fun findByCategoryId(categoryId: String, limit: Int): List<Article> {
        val validityDate = Instant.now()
        val whereCondition =
            ARTICLES.ID.`in`(
                dsl.select(ARTICLE_CATEGORY.ARTICLE_ID)
                    .from(ARTICLE_CATEGORY)
                    .where(ARTICLE_CATEGORY.CATEGORY_ID.eq(categoryId))
            )
            .and(validityCondition(validityDate)).and(ARTICLES.SHOW_ON_BLOG.isTrue)
        return findByCriteriaWithLimit(
            INFO_ATTRIBUTES,
            whereCondition,
            defaultOrdering(),
            limit,
            articleInfoMapper()
        )
    }

    fun findBySlug(slug: String): Article? =
        dsl.selectFrom(table)
            .where(ARTICLES.SLUG.eq(slug).and(validityCondition(Instant.now())))
            .fetchOne(mapper)

    fun findByQuery(query: String, limit: Int): List<Article> {
        if (query.trim().isEmpty()) {
            return emptyList()
        }
        val validityDate = Instant.now()
        val whereCondition: Condition =
            validityCondition(validityDate)
                .and(
                    ARTICLES.TITLE.containsIgnoreCase(query)
                        .or(ARTICLES.PEREX.containsIgnoreCase(query))
                        .or(ARTICLES.BODY.containsIgnoreCase(query))
                )
        return findByCriteriaWithLimit(
            INFO_ATTRIBUTES,
            whereCondition,
            defaultOrdering(),
            limit,
            articleInfoMapper()
        )
    }


    fun findArticlesWithExternalIds(): List<Article> =
        dsl.selectFrom(table)
            .where(ARTICLES.EXTERNAL_ID.isNotNull)
            .fetch(mapper)

    private fun articleInfoMapper() = { record: Record ->
        Article(
            common = AssetAttributes(
                id = requireNotNull(record[ARTICLES.ID]),
                created = requireNotNull(record[ARTICLES.CREATED]),
                createdBy = requireNotNull(record[ARTICLES.CREATED_BY]),
                modified = requireNotNull(record[ARTICLES.MODIFIED]),
                modifiedBy = requireNotNull(record[ARTICLES.MODIFIED_BY])
            ),
            validity = Validity(
                validFrom = requireNotNull(record[ARTICLES.VALID_FROM]),
                validTo = record[ARTICLES.VALID_TO]
            ),
            slug = requireNotNull(record[ARTICLES.SLUG]),
            title = requireNotNull(record[ARTICLES.TITLE]),
            image = record[ARTICLES.IMAGE],
            perex = requireNotNull(record[ARTICLES.PEREX]),
            externalId = null,
            bodyMarkdown = "",
            body = "",
            keywords = "",
            showOnBlog = false
        )
    }

    protected fun validityCondition(validityDate: Instant) =
        ARTICLES.VALID_FROM.lessOrEqual(validityDate)
            .and(
                ARTICLES.VALID_TO.isNull
                    .or(ARTICLES.VALID_TO.greaterOrEqual(validityDate))
            )

    protected fun defaultOrdering() = ARTICLES.VALID_FROM.desc()

    companion object {
        val INFO_ATTRIBUTES = listOf(
            ARTICLES.ID,
            ARTICLES.VALID_FROM,
            ARTICLES.VALID_TO,
            ARTICLES.CREATED,
            ARTICLES.CREATED_BY,
            ARTICLES.MODIFIED,
            ARTICLES.MODIFIED_BY,
            ARTICLES.SLUG,
            ARTICLES.TITLE,
            ARTICLES.IMAGE,
            ARTICLES.PEREX
        )
    }
}
