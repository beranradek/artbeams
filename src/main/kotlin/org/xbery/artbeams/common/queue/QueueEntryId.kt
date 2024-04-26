package org.xbery.artbeams.common.queue

import org.xbery.artbeams.common.queue.model.AbstractQueueEntry

/**
 * Common operations for identification of queue entries.
 *
 * @author Radek Beran
 */
interface QueueEntryId<E : AbstractQueueEntry> {

    /**
     * Retrieve a descriptive ID of an entry.
     */
    fun getEntryId(entry: E): Any {
        return entry.id
    }

    /**
     * Return a name describing entries used.
     */
    fun getEntryName(entry: E): String {
        return "entry"
    }
}
