package org.xbery.artbeams.emailworkflows.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowRun
import org.xbery.artbeams.emailworkflows.repository.EmailWorkflowRunRepository
import java.time.Clock
import java.util.UUID

@Service
class EmailWorkflowRunService(
    private val repository: EmailWorkflowRunRepository,
    private val clock: Clock
) {
    /** Returns false when the same acquisition callback has already started this workflow. */
    fun startOnce(workflowId: String, userId: String, productId: String, acquisitionId: String): Boolean =
        repository.createIfAbsent(
            EmailWorkflowRun(UUID.randomUUID().toString(), workflowId, userId, productId, acquisitionId),
            clock.instant()
        )
}
