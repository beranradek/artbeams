package org.xbery.artbeams.common.queue

import org.xbery.artbeams.common.queue.model.AbstractQueueEntry
import org.xbery.artbeams.common.queue.persistence.QueueEntryRepository
import java.time.Clock
import java.time.Instant
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Base implementation for adding entries to the persistent queue.
 *
 * @param <E> type of the queue entry
 *
 * @author Radek Beran
 */
abstract class AbstractQueueAppender<E : AbstractQueueEntry>
/**
 * Create a new queue appender.
 * @param serviceId unique identifier of the service (ID of feature, used for monitoring)
 * @param repository repository for the queue entries
 * @param clock clock to be used for retrieving the current time
 */
protected constructor(
    protected val serviceId: String,
    protected val repository: QueueEntryRepository<E>,
    protected val clock: Clock
) : QueueEntryId<E> {
    protected val logger: Logger = LoggerFactory.getLogger("$serviceId.queue")

    /**
     * This may be used by subclasses to submit (schedule) a new entity to the queue.
     */
    open fun appendNewEntry(entry: E): E {
        if (entry.nextAction == null) entry.nextAction = Instant.now(clock)
        logEntryNew(entry)
        return repository.addNewEntry(entry)
        // TBD RBe: Awake the task for immediate processing? But the task can also reside in a different JVM...
    }

    protected open fun logEntryNew(entry: E) {
        logger.info(
            "scheduling new " + getEntryName(entry) + " at time " + entry.nextAction + ", id: " + getEntryId(entry)
        )
    }
}
