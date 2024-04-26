package org.xbery.artbeams.common.queue.model

import org.jooq.RecordMapper
import org.xbery.artbeams.common.context.OriginStamp
import org.xbery.artbeams.common.repository.fromDbDateTime
import org.xbery.artbeams.jooq.schema.tables.references.QUEUE

/**
 * Abstract JOOQ [RecordMapper] for [AbstractQueueEntry] entity.
 *
 * @author Radek Beran
 */
abstract class AbstractQueueEntryMapper<R : org.jooq.Record, E : AbstractQueueEntry> : RecordMapper<R, E> {
    override fun map(record: R): E {
        return mapRecordToEntity(record, createQueueEntry())
    }

    abstract fun createQueueEntry(): E

    protected open fun mapRecordToEntity(record: R, entity: E): E {
        entity.id = requireNotNull(record.get(QUEUE.ID))
        entity.entered = OriginStamp(
            requireNotNull(record.get(QUEUE.ENTERED_TIME)?.fromDbDateTime()),
            requireNotNull(record.get(QUEUE.ENTERED_ORIGIN)),
            null
        )
        entity.attempts = requireNotNull(record.get(QUEUE.ATTEMPTS))
        entity.nextAction = record.get(QUEUE.NEXT_ACTION_TIME)?.fromDbDateTime()

        val processedTime = record.get(QUEUE.PROCESSED_TIME)?.fromDbDateTime()
        val processedOrigin = record.get(QUEUE.PROCESSED_ORIGIN)
        entity.processed = if (processedTime != null && processedOrigin != null) OriginStamp(
            processedTime,
            processedOrigin,
            null
        ) else null

        val lastAttemptTime = record.get(QUEUE.LAST_ATTEMPT_TIME)?.fromDbDateTime()
        val lastAttemptOrigin = record.get(QUEUE.LAST_ATTEMPT_ORIGIN)
        entity.lastAttempt = if (lastAttemptTime != null && lastAttemptOrigin != null) OriginStamp(
            lastAttemptTime,
            lastAttemptOrigin,
            null
        ) else null

        entity.lastResult = record.get(QUEUE.LAST_RESULT)
        entity.expiration = record.get(QUEUE.EXPIRATION_TIME)?.fromDbDateTime()
        return entity
    }
}
