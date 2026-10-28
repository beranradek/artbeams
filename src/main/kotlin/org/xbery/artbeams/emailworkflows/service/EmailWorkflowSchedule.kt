package org.xbery.artbeams.emailworkflows.service

import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowStep
import java.time.Instant
import java.time.temporal.ChronoUnit

object EmailWorkflowSchedule {
    /** Steps use offsets from the acquisition time, never from a previous send. */
    fun dueAt(acquiredAt: Instant, step: EmailWorkflowStep): Instant =
        acquiredAt.plus(step.delayMinutes.toLong(), ChronoUnit.MINUTES)
}
