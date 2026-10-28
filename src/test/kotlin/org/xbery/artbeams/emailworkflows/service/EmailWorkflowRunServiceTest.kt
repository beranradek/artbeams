package org.xbery.artbeams.emailworkflows.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRunRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class EmailWorkflowRunServiceTest : StringSpec({
    "reports a duplicate acquisition without starting another run" {
        val repository = mockk<EmailWorkflowRunRepository>()
        every { repository.createIfAbsent(any(), any()) } returns false
        val service = EmailWorkflowRunService(repository, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC))
        service.startOnce("workflow", "user", "product", "order-item") shouldBe false
    }
})
