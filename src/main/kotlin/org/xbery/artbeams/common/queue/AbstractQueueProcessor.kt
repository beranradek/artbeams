package org.xbery.artbeams.common.queue

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.IntervalTask
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.queue.config.RetryRulesConfig
import org.xbery.artbeams.common.queue.model.AbstractQueueEntry
import org.xbery.artbeams.common.queue.persistence.QueueEntryRepository
import java.util.*
import kotlin.concurrent.Volatile
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * Base implementation for queued processing of some entries.
 * The entries are stored in a persistent queue and may be processed
 * both immediately or after some delay, with flexible retry configuration.
 *
 * The main responsibility of this class is "dispatching" the queue
 * (hence the name), which means dequeuing entries and handling the tasks
 * associated with them, providing automatic retries.
 *
 * This is typically a component ("service") responsible for the
 * dequeing end of the queue.
 *
 * Entering ("enqueuing") entries into the queue can be done using [AbstractQueueAppender].
 * In many cases, the enqueuing is required
 * to happen in a different application/JVM than the processing (which is one
 * of the common reasons why a queue is needed). In such a case,
 * it is recommended to only have this service running on the processing
 * side.
 *
 * The component is ready to be annotated with a `@ConfigurationProperties`
 * (using a suitable unique prefix for concrete subclass).
 *
 * @param <E> type of the queue entry
 *
 * @author Radek Beran
 */
@Suppress("TooManyFunctions")
abstract class AbstractQueueProcessor<E : AbstractQueueEntry>
/**
 * Create a new queue processor.
 * @param serviceId unique identifier of the service (ID of feature, used for monitoring)
 * @param repository repository for the queue entries
 * @param clock clock to be used for retrieving the current time
 */
protected constructor(
    protected open val serviceId: String,
    protected open val repository: QueueEntryRepository<E>,
    protected open val clock: Clock
) : QueueEntryId<E>, SchedulingConfigurer {
    protected open val serviceName: String = "$serviceId.queue"

    protected val logger: Logger = LoggerFactory.getLogger("$serviceId.queue")

    @Volatile
    private var pauseTm: Instant? = null

    /**
     * Fixed delay between consecutive runs of a dispatch task.
     * Note that if both appending/removal to/from the queue happen in the same JVM,
     * it could be better to awake the task each time a new entry was accepted.
     */
    var taskDelay: Duration = DEFAULT_TASK_DELAY

    /**
     * Initial delay before the first task execution.
     * The default is to select a reasonable value randomly.
     */
    var initialDelay: Duration = (
        MINIMUM_INITIAL_TASK_DELAY.inWholeMilliseconds + randGen.nextInt(
            min(
                MAX_INITIAL_TASK_DELAY_ADDITION.inWholeMilliseconds.toDouble(),
                taskDelay.inWholeMilliseconds.toDouble()
            ).toInt()
        )
        ).milliseconds

    /**
     * Time for which an entry will be reserved for processing by the task.
     * It serves as a first-attempt safety reschedule duration for the case
     * the task crashes unexpectedly or the whole application shuts down
     * while processing the entries.
     *
     * On retrieval, each entry is re-scheduled this amount of time into the future.
     * Under normal circumstances, the time will be re-adjusted very soon to better value,
     * regardless whether the task is successful or not.
     */
    var freezeTime: Duration = DEFAULT_ENTRY_PROCESSING_FREEZE_TIME

    /**
     * Maximum entries to be processed in a single task run.
     * This limit serves to prevent blocking other tasks or consuming many resources.
     * For extensive processors, this can be re-adjusted to higher value.
     */
    var maxPerRun: Int = DEFAULT_MAX_ENTRIES_PER_RUN

    /**
     * Maximum time for one task to run. When reached, task will be allowed to finish its current entry
     * and then it will be interrupted with a warning.
     */
    var maxRunTime: Duration = DEFAULT_MAX_TASK_RUN_TIME

    /**
     * Configuration of the retry delays according to entry ages.
     * @see RetryRulesConfig
     */
    var retry: RetryRulesConfig = RetryRulesConfig()

    /**
     * Expiration period for entries that have been successfully processed.
     * It will be added to the time of the successful attempt.
     * After reaching this additional period, entry will be discarded from database.
     */
    var preserveAfterFinished: Duration = DEFAULT_ENTRY_EXPIRATION_AFTER_FINISHED

    /**
     * Expiration period for entries that have failed permanently.
     * It will be applied after a maximum retry time is reached - added to the time of the last failure.
     * After reaching this additional period, entry will be discarded from database.
     */
    var preserveAfterGivenUp: Duration = DEFAULT_ENTRY_EXPIRATION_AFTER_GIVEN_UP

    /**
     * period for service to pause when exception for which method [isPausingException] return true has been thrown
     */
    var pauseDuration: Duration = DEFAULT_PAUSE_DURATION

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        logTaskConfigure()
        taskRegistrar.addFixedDelayTask(
            IntervalTask(
                { this.runDispatchTask() },
                taskDelay.toJavaDuration(),
                initialDelay.toJavaDuration()
            )
        )
    }

    protected open fun logTaskConfigure() {
        logger.debug(
            serviceName + " task scheduled to run in " +
                (initialDelay.inWholeSeconds) + "s and then with a fixed delay of " +
                (taskDelay.inWholeSeconds) + "s"
        )
    }

    /**
     * Execution of dispatch task invoked by a scheduler.
     * It should not be called directly.
     */
    open fun runDispatchTask() {
        var entry: E?
        var state: QueueProcessingTaskState? = null
        try {
            if (isPaused) {
                logPause()
                return
            }
            logger.debug("invocation of $serviceName task, checking for available entries")

            // this is done without an operation context
            entry = findNextEntry()
            if (entry == null) {
                logger.debug("nothing to be processed by $serviceName")
                return
            }

            // now we know there is a work to do
            logTaskRun()
            state = createTaskState()
            state.startNanoTime = System.nanoTime()
            state.frozenId = getEntryId(entry)
            val opCtx = createTaskOperationCtx(entry)
            while (entry != null) {
                state.frozenId =
                    getEntryId(entry) // set twice redundantly for the first entry, but it is idempotent and we need it for subsequent entries
                logEntryStart(entry)
                processOneOrMoreEntries(state, entry, opCtx)
                if (discontinueTaskNow(state)) {
                    // any monitoring should be done inside the "discontinueTaskNow" method
                    break
                }
                entry = findNextEntry()
            }
            logTaskDone(state)
            deleteExpiredEntries(clock.now())
        } catch (@Suppress("TooGenericExceptionCaught") ex: Throwable) {
            // this is necessary
            // the method is usually run by Spring scheduler, which means the exception would not be visible anywhere
            logger.error("exception while running task", ex)
            // note that this often means some entry remained unprocessed ("frozen")
            if (state?.frozenId != null) {
                logger.error(
                    "NOTICE: some work was not processed correctly and remain frozen for $freezeTime, frozenId: ${state.frozenId}",
                    null
                )
            }
            throw ex
        }
    }

    /**
     * Delete expired entries from the queue.
     */
    protected open fun deleteExpiredEntries(now: Instant) {
        val startNanoTime = System.nanoTime()
        val deletedCount = repository.deleteExpiredEntries(now)
        logDeleteExpiredDone(deletedCount, startNanoTime)
    }

    protected open fun createTaskState(): QueueProcessingTaskState {
        return QueueProcessingTaskState()
    }

    /**
     * Processing one or more entries from the queue. E.g. more entries can
     * be taken as a batch and processed at once.
     *
     * By the time of invocation, the entry is locked in the database
     * and marked as potentially frozen
     * (if this method throws an exception, the entry remains frozen).
     * After completion, entry is unlocked and the "frozen id" indicator in the state object cleared.
     **
     * @param state a state object returned from [createTaskState].
     * @param entry the entry to be processed.
     * @param opCtx operation context created for task processing
     */
    protected open fun processOneOrMoreEntries(state: QueueProcessingTaskState, entry: E, opCtx: OperationCtx) {
        var success = false
        ++state.processed
        try {
            success = processOneEntry(entry, opCtx)
            finishEntry(entry, success, opCtx)
            state.frozenId = null // not frozen anymore after the last line
            logEntryFinish(entry, success)
            if (success) ++state.successful
        } catch (@Suppress("TooGenericExceptionCaught") ex: Exception) {
            ++state.errors
            logEntryError(entry, ex)
            rescheduleEntry(entry, ex, null, opCtx)
            state.frozenId = null // not frozen anymore after the last line
            if (isPausingException(ex)) {
                pause(clock.now() + pauseDuration)
            }
        }
        state.isLastSuccess = success
    }

    protected open fun findNextEntry(): E? {
        // this must use the real time, not the context
        // (not mentioning that no context may exist at this moment)
        return repository.findNextEntry(clock.now(), freezeTime)
    }

    protected open fun finishEntry(entry: E, success: Boolean, opCtx: OperationCtx) {
        entry.nextAction = null
        entry.expiration = getFinishExpiration(entry)
        entry.processed = opCtx.stamp
        entry.lastResult = success.toString()
        entry.lastAttempt = opCtx.stamp
        repository.rescheduleEntry(entry)
    }

    protected open fun getFinishExpiration(entry: E): Instant {
        return clock.now() + preserveAfterFinished
    }

    protected open fun rescheduleEntry(entry: E, error: Throwable?, rescheduleCause: String?, opCtx: OperationCtx) {
        val perm = (error != null && isPermanentError(error))
        val next = if (perm) null else getNextRetry(entry)
        entry.nextAction = next
        val errorMessage = createErrorMessage(error, rescheduleCause)
        entry.lastResult = errorMessage
        entry.lastAttempt = opCtx.stamp
        if (next != null) {
            logEntryReschedule(entry, next, errorMessage)
        } else {
            logEntryGivenUp(entry, if (perm) error else null)
            entry.expiration = getFailureExpiration(entry)
        }
        repository.rescheduleEntry(entry)
    }

    protected open fun createErrorMessage(error: Throwable?, rescheduleCause: String?): String {
        val builder = StringBuilder()
        if (rescheduleCause != null) {
            builder.append(rescheduleCause)
        }
        if (error != null) {
            if (builder.isNotEmpty()) {
                builder.append(" ")
            }
            builder.append(error.toString())
        }
        return builder.toString()
    }

    protected open fun getNextRetry(entry: E): Instant? {
        return retry.calculateNextRetry(
            clock.now(),
            entry.entered.time,
            entry.attempts
        )
    }

    protected open fun getFailureExpiration(entry: E): Instant {
        return clock.now() + preserveAfterGivenUp
    }

    protected open fun isPermanentError(error: Throwable?): Boolean {
        return false
    }

    /**
     * Stops the task after some condition is fulfilled. Subclasses should call super implementation.
     * @return true to break the task and finish immediately
     */
    protected open fun discontinueTaskNow(state: QueueProcessingTaskState): Boolean {
        return if (state.processed >= maxPerRun) {
            logger.warn("maximal number of entries processed, discontinuing this task run")
            true
        } else {
            val duration = System.nanoTime() - state.startNanoTime
            if (duration.nanoseconds >= maxRunTime) {
                logger.warn("maximal allowed processing time exceeded, discontinuing this task run")
                true
            } else {
                if (isPaused) {
                    logPause()
                    true
                } else {
                    false
                }
            }
        }
    }

    protected open fun logPause() {
        logger.info("request rate limit of target service has been reached, discontinuing this task run")
    }

    protected open fun logTaskRun() {
        logger.info("running $serviceName task")
    }

    protected open fun logTaskDone(state: QueueProcessingTaskState) {
        val nanos = System.nanoTime() - state.startNanoTime
        val sb = StringBuilder()
        @Suppress("MagicNumber")
        sb.append(serviceName)
            .append(" task done in ").append((nanos + 500000L) / 1000000L)
            .append("ms, processed ").append(state.processed).append(" entries")
        if (state.successful == state.processed) {
            sb.append(" successfully")
        } else {
            sb.append(", ").append(state.successful).append(" successful")
        }
        if (state.errors > 0) {
            sb.append(", ").append(state.errors).append(" errors")
        }
        logger.info("queue.task.done: $sb")
    }

    protected open fun logDeleteExpiredDone(deletedCount: Int, startTimeNanos: Long) {
        val nanos = System.nanoTime() - startTimeNanos
        logger.info("$serviceName.task.expired.deletion: done in ${(nanos + 500000L) / 1000000L} ms, $deletedCount entries deleted")
    }

    protected open fun logEntryStart(entry: E) {
        logger.info(
            "processing next " + getEntryName(entry) + ", id: " + getEntryId(entry)
        )
    }

    protected open fun logEntryFinish(entry: E, success: Boolean) {
        logger.debug(getEntryName(entry) + "/" + getEntryId(entry) + " processing finished")
    }

    protected open fun logEntryError(entry: E, ex: Throwable?) {
        logger.error("error processing " + getEntryName(entry) + "/" + getEntryId(entry), ex)
    }

    protected open fun logEntryReschedule(entry: E, next: Instant, rescheduleCause: String?) {
        logger.warn(
            "rescheduling " + getEntryName(entry) + "/" + getEntryId(entry) +
                " from " + entry.entered.time +
                " to " + next + (if (rescheduleCause == null) "" else " due to : $rescheduleCause")
        )
    }

    protected open fun logEntryGivenUp(entry: E, permError: Throwable?) {
        logger.error(
            "no more attempts on " + getEntryName(entry) + "/" + getEntryId(entry) + ", giving up" +
                (if (permError == null) "" else " due to a permanent error: $permError")
        )
    }

    /**
     * Create OperationCtx used when processing an entry.
     * @param entry entry for which the context will be then used.
     */
    protected abstract fun createTaskOperationCtx(entry: E): OperationCtx

    /**
     * Process one queue entry.
     * @param entry the queue entry to be processed.
     * @param opCtx operation context created by the processing task
     * @return whether the processing was successful.
     * If the method returns normally without exception,
     * the entry will be considered processed and will not be rescheduled again.
     * @throws Exception an exception will cause the entry to be rescheduled
     * for a later time, according to rules.
     * Method [isPermanentError] can be overridden to classify the exception
     * as permanent - in such a case, no further attempts will be scheduled.
     */
    @Throws(Exception::class)
    protected abstract fun processOneEntry(entry: E, opCtx: OperationCtx): Boolean

    protected open val isPaused: Boolean
        /** true if dispatching is paused  */
        get() = pauseTm != null && requireNotNull(pauseTm) > clock.now()

    /** Pauses queue processing until given time  */
    open fun pause(until: Instant?) {
        this.pauseTm = until
    }

    /** Resumes processing of previously paused queue.  */
    open fun resume() {
        this.pauseTm = null
    }

    /**
     * If true then dispatching will be paused for a period of [pauseDuration].
     */
    protected open fun isPausingException(error: Throwable?): Boolean {
        return false
    }

    companion object {
        private val randGen = Random()

        // Key queue performance parameters:
        private val DEFAULT_TASK_DELAY =
            30.seconds // TBD RBe: longer default delay if we can awake the task on scheduling new entries
        private const val DEFAULT_MAX_ENTRIES_PER_RUN = 100
        private val DEFAULT_MAX_TASK_RUN_TIME = 2.minutes

        /** Next entry processing shift preserving the entry outside of processing in case of task crash. */
        private val DEFAULT_ENTRY_PROCESSING_FREEZE_TIME = 15.minutes

        private val DEFAULT_ENTRY_EXPIRATION_AFTER_FINISHED = 5.days
        private val DEFAULT_ENTRY_EXPIRATION_AFTER_GIVEN_UP = 14.days
        private val DEFAULT_PAUSE_DURATION = 30.seconds
        private val MINIMUM_INITIAL_TASK_DELAY = 20.seconds
        private val MAX_INITIAL_TASK_DELAY_ADDITION = 5000.milliseconds
    }
}
