package org.xbery.artbeams.emailworkflows.service

import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowRun
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowStep
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowDeliveryRepository
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRepository
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRunRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class EmailWorkflowAcquisitionServiceTest : StringSpec({
    "creates durable deliveries only for a newly started workflow run" {
        val workflows = mockk<EmailWorkflowRepository>()
        val runs = mockk<EmailWorkflowRunRepository>()
        val deliveries = mockk<EmailWorkflowDeliveryRepository>(relaxed = true)
        every { workflows.findActiveWorkflowIdsForProduct("product-1") } returns listOf("workflow-1")
        every { workflows.findSteps("workflow-1") } returns listOf(EmailWorkflowStep("step-1", "template-1", 0, 60))
        every { runs.createIfAbsent(any(), any()) } returns true

        EmailWorkflowAcquisitionService(workflows, runs, deliveries, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC))
            .startForAcquisition("user-1", "product-1", "order-item-1")

        verify { runs.createIfAbsent(match<EmailWorkflowRun> { it.workflowId == "workflow-1" && it.acquisitionId == "order-item-1" }, Instant.EPOCH) }
        verify { deliveries.append(any(), "step-1", "template-1", Instant.parse("1970-01-01T01:00:00Z")) }
    }

    "does not enqueue duplicate deliveries when the acquisition was already processed" {
        val workflows = mockk<EmailWorkflowRepository>()
        val runs = mockk<EmailWorkflowRunRepository>()
        val deliveries = mockk<EmailWorkflowDeliveryRepository>(relaxed = true)
        every { workflows.findActiveWorkflowIdsForProduct("product-1") } returns listOf("workflow-1")
        every { runs.createIfAbsent(any(), any()) } returns false

        EmailWorkflowAcquisitionService(workflows, runs, deliveries, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC))
            .startForAcquisition("user-1", "product-1", "order-item-1")

        verify(exactly = 0) { workflows.findSteps(any()) }
        verify(exactly = 0) { deliveries.append(any(), any(), any(), any()) }
    }
})
