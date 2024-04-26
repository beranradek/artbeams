package org.xbery.artbeams.common.queue

/**
 * State of running queue processing task.
 *
 * @author Radek Beran
 */
open class QueueProcessingTaskState {
    /** System "nano-time" when this task run started (the first entry). */
    var startNanoTime: Long = 0

    /** ID of an entry that was frozen (i.e., locked, not processed, and not unlocked),
     * null if the entry was just released.
     */
    var frozenId: Any? = null

    /** Status of the last entry (false means exception or false returned). */
    var isLastSuccess: Boolean = false

    /** Number of entries processed so far by one task run. */
    var processed: Int = 0

    /** Number of successfully processed entries so far by one task run. */
    var successful: Int = 0

    /** Number of errors encountered so far by one task run. */
    var errors: Int = 0
}
