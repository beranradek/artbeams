package org.xbery.artbeams.news.repository

import org.jooq.DSLContext
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.news.domain.NewsSubscription
import org.xbery.artbeams.news.repository.mapper.NewsSubscriptionMapper
import org.xbery.artbeams.news.repository.mapper.NewsSubscriptionUnmapper
import org.xbery.artbeams.common.repository.AbstractRecordStorage
import org.xbery.artbeams.jooq.schema.tables.records.NewsSubscriptionRecord
import org.xbery.artbeams.jooq.schema.tables.references.NEWS_SUBSCRIPTION
import java.time.Instant

/**
 * Repository for news subscription records.
 * @author Radek Beran
 */
@Repository
class NewsSubscriptionRepository(
    override val dsl: DSLContext,
    val mapper: NewsSubscriptionMapper,
    val unmapper: NewsSubscriptionUnmapper
) : AbstractRecordStorage<NewsSubscription, NewsSubscriptionRecord> {
    override val table: Table<NewsSubscriptionRecord> = NEWS_SUBSCRIPTION

    fun findByEmail(email: String): List<NewsSubscription> {
        return dsl.selectFrom(table)
            .where(NEWS_SUBSCRIPTION.EMAIL.eq(email))
            .fetch(mapper)
    }

    fun create(entity: NewsSubscription): NewsSubscription {
        createWithoutReturn(entity, unmapper)
        return entity
    }

    fun confirm(id: String): Int {
        return dsl.update(table)
            .set(NEWS_SUBSCRIPTION.CONFIRMED, Instant.now())
            .where(NEWS_SUBSCRIPTION.ID.eq(id))
            .execute()
    }
}
