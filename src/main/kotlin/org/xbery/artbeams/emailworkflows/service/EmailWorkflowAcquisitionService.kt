package org.xbery.artbeams.emailworkflows.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowDeliveryRepository
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRepository
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRunRepository
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowRun
import java.time.Clock
import java.util.UUID

@Service
class EmailWorkflowAcquisitionService(
    private val workflows: EmailWorkflowRepository,
    private val runs: EmailWorkflowRunRepository,
    private val deliveries: EmailWorkflowDeliveryRepository,
    private val clock: Clock
) {
    @Transactional
    fun startForAcquisition(userId: String, productId: String, acquisitionId: String) {
        val acquiredAt = clock.instant()
        workflows.findActiveWorkflowIdsForProduct(productId).forEach { workflowId ->
            val run = EmailWorkflowRun(UUID.randomUUID().toString(), workflowId, userId, productId, acquisitionId)
            if (runs.createIfAbsent(run, acquiredAt)) {
                workflows.findSteps(workflowId).forEach { step ->
                    deliveries.append(run.id, step.id, step.emailTemplateId, EmailWorkflowSchedule.dueAt(acquiredAt, step))
                }
            }
        }
    }
}
