package org.xbery.artbeams.news.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.news.domain.NewsSubscription
import org.xbery.artbeams.jooq.schema.tables.records.NewsSubscriptionRecord
import org.xbery.artbeams.jooq.schema.tables.references.NEWS_SUBSCRIPTION

/**
 * @author Radek Beran
 */
@Component
class NewsSubscriptionUnmapper : RecordUnmapper<NewsSubscription, NewsSubscriptionRecord> {

    override fun unmap(newsSubscription: NewsSubscription): NewsSubscriptionRecord {
        val record = NEWS_SUBSCRIPTION.newRecord()
        record.id = newsSubscription.id
        record.email = newsSubscription.email
        record.created = newsSubscription.created
        record.confirmed = newsSubscription.confirmed
        return record
    }
}
