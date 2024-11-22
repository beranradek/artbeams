package org.xbery.artbeams.common.queue.model

import org.xbery.artbeams.common.context.OriginStamp
import java.time.Instant

/**
 * Queue entry model.
 *
 * @author Radek Beran
 */
abstract class AbstractQueueEntry {
    abstract var id: String
    abstract var entered: OriginStamp
    var attempts: Int = 0

    /**
     * Next scheduled action on this entry.
     * This is the crucial field that decides when the entry will be returned back.
     */
    var nextAction: Instant? = null

    var processed: OriginStamp? = null
    var lastAttempt: OriginStamp? = null
    var lastResult: String? = null

    // TBD RBe: Implement removal of entries by expiration time
    /**
     * Scheduled expiration of this entry.
     * If non-null, it stores the time when the entry will be automatically
     * removed from the queue. This is ensured by a Mongo "expiration index".
     * <p>
     * This should never be earlier than {@link #nextAction}.
     * In fact, it is recommended that exactly one of these two fields
     * is null at all times.
     */
    var expiration: Instant? = null

    override fun toString(): String {
        return "${this::class::simpleName}(id='$id')"
    }
}
