package org.xbery.artbeams.common.queue.model

import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.jooq.Table
import org.xbery.artbeams.jooq.schema.tables.references.QUEUE

/**
 * Abstract JOOQ [RecordMapper] for [AbstractQueueEntry] entity.
 *
 * @author Radek Beran
 */
abstract class AbstractQueueEntryUnmapper<E : AbstractQueueEntry, R : org.jooq.Record>(
    protected val table: Table<R>
) : RecordUnmapper<E, R> {

    override fun unmap(entry: E): R {
        val record = table.newRecord()
        return mapEntityToRecord(entry, record)
    }

    protected open fun mapEntityToRecord(entity: E, record: R): R {
        record.set(QUEUE.ID, entity.id)
        record.set(QUEUE.ENTERED_TIME, entity.entered.time)
        record.set(QUEUE.ENTERED_ORIGIN, entity.entered.origin)
        record.set(QUEUE.ATTEMPTS, entity.attempts)
        record.set(QUEUE.NEXT_ACTION_TIME, entity.nextAction)
        record.set(QUEUE.PROCESSED_TIME, entity.processed?.time)
        record.set(QUEUE.PROCESSED_ORIGIN, entity.processed?.origin)
        record.set(QUEUE.LAST_ATTEMPT_TIME, entity.lastAttempt?.time)
        record.set(QUEUE.LAST_ATTEMPT_ORIGIN, entity.lastAttempt?.origin)
        record.set(QUEUE.LAST_RESULT, entity.lastResult)
        record.set(QUEUE.EXPIRATION_TIME, entity.expiration)
        return record
    }
}
