package org.xbery.artbeams.common.queue.persistence

import org.jooq.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import org.xbery.artbeams.common.queue.model.AbstractQueueEntry
import org.xbery.artbeams.common.queue.model.AbstractQueueEntryMapper
import org.xbery.artbeams.common.queue.model.AbstractQueueEntryUnmapper
import org.xbery.artbeams.common.repository.AbstractRecordFetcher
import org.xbery.artbeams.common.repository.AbstractRecordStorage
import java.time.Duration
import java.time.Instant

/**
 * Implementation of [QueueEntryRepository] using SQL database.
 *
 * @author Radek Beran
 */
abstract class AbstractSqlQueueEntryRepository<R: UpdatableRecord<R>, E : AbstractQueueEntry>(
    override val table: Table<R>,
    protected val queueEntryMapper: AbstractQueueEntryMapper<R, E>,
    protected val queueEntryUnmapper: AbstractQueueEntryUnmapper<E, R>,
    override val dsl: DSLContext
) : QueueEntryRepository<E>, AbstractRecordFetcher<R>, AbstractRecordStorage<E, R> {

    protected val logger: Logger = LoggerFactory.getLogger(table.name)

    protected abstract val idField: Field<String?>
    protected abstract val nextActionTimeField: Field<Instant?>
    protected abstract val attemptsField: Field<Int?>
    protected abstract val expirationTimeField: Field<Instant?>

    @Transactional
    override fun findNextEntry(now: Instant, shift: Duration?): E? {
        val next = if (shift == null) null else now.plus(shift)
        check(next == null || next > now) { "time shift must be positive" }

        val entryId = dsl.selectFrom(table)
            .where(findNextEntryCondition(now))
            .orderBy(nextActionTimeField)
            .fetchOne(idField)

        return entryId?.let {
            val updatedCount = dsl.update(table)
                .set(nextActionTimeField, next)
                .set(attemptsField, attemptsField + 1)
                .where(idField.eq(it))
                .execute()
            check(updatedCount == 1) { "Entry not updated with next action time and attempts: $entryId" }

            findEntryById(it)
        }
    }

    protected open fun findNextEntryCondition(now: Instant) = nextActionTimeField.lessOrEqual(now)

    override fun findEntryById(id: String): E? = findOneBy(idField, id, queueEntryMapper)

    override fun deleteEntryById(id: String): Boolean = deleteBy(idField, id) > 0

    override fun rescheduleEntry(entry: E) {
        updateBy(entry, idField, entry.id, queueEntryUnmapper)
    }

    override fun addNewEntry(entry: E): E {
        createWithoutReturn(entry, queueEntryUnmapper)
        val insertedEntry = findEntryById(entry.id)
        return requireNotNull(insertedEntry) { "Entry not found: ${entry.id}" }
    }

    override fun deleteExpiredEntries(now: Instant): Int {
        return dsl.deleteFrom(table)
            .where(expirationTimeField.lessOrEqual(now))
            .execute()
    }
}
