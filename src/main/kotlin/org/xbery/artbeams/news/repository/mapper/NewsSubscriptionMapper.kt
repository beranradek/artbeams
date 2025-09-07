package org.xbery.artbeams.news.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.news.domain.NewsSubscription
import org.xbery.artbeams.jooq.schema.tables.records.NewsSubscriptionRecord

/**
 * @author Radek Beran
 */
@Component
class NewsSubscriptionMapper : RecordMapper<NewsSubscriptionRecord, NewsSubscription> {

    override fun map(record: NewsSubscriptionRecord): NewsSubscription {
        return NewsSubscription(
            id = requireNotNull(record.id),
            email = requireNotNull(record.email),
            created = requireNotNull(record.created),
            confirmed = record.confirmed
        )
    }
}
