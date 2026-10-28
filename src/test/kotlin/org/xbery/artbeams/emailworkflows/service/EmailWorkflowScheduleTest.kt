package org.xbery.artbeams.emailworkflows.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowStep
import java.time.Instant

class EmailWorkflowScheduleTest : StringSpec({
    "schedules a step from product acquisition time" {
        val acquired = Instant.parse("2026-10-27T10:00:00Z")
        EmailWorkflowSchedule.dueAt(acquired, EmailWorkflowStep("step", "template", 0, 1440)) shouldBe
            Instant.parse("2026-10-28T10:00:00Z")
    }
    "rejects a negative workflow delay" {
        io.kotest.assertions.throwables.shouldThrow<IllegalArgumentException> {
            EmailWorkflowStep("step", "template", 0, -1)
        }
    }
})
