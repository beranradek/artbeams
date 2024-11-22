package org.xbery.artbeams.common.queue.persistence

import org.xbery.artbeams.common.queue.model.AbstractQueueEntry
import java.time.Duration
import java.time.Instant

/**
 * Storage of a general-purpose persistent queue.
 *
 * @param <E> type of the queue entry
 *
 * @author Radek Beran
 */
interface QueueEntryRepository<E : AbstractQueueEntry> {
    /**
     * Queue a new entry into the underlying queue.
     * The entry should already have its [AbstractQueueEntry.nextAction] set accordingly.
     */
    fun addNewEntry(entry: E): E

    /**
     * Remove expired (already finished) entries from the queue.
     * @param now current time
     * @return number of removed entries
     */
    fun deleteExpiredEntries(now: Instant): Int


    /**
     * Dequeue the next entry scheduled for an action and shift its time to the future.
     *
     * @param now time to search for in "next action". Must not be null.
     * @param shift how much time to shift the entry to the future
     * @return next event to be processed. Null if there is none.
     */
    fun findNextEntry(now: Instant, shift: Duration?): E?

    /**
     * Change the scheduling of some entry, or cancel the scheduling completely (mextAction = null, non-null expiration).
     *
     * @param entry an existing object, previously returned
     * from [findNextEntry]. All fields should be changed
     * as required for the new state. Most importantly,
     * the [AbstractQueueEntry.nextAction] should be set
     * to either null or to some future time.
     */
    fun rescheduleEntry(entry: E)

    /**
     * Retrieve an entry by its ID.
     */
    fun findEntryById(id: String): E?

    /**
     * Deletes an entry by its ID. Returns true if the entry was found and deleted.
     */
    fun deleteEntryById(id: String): Boolean
}
