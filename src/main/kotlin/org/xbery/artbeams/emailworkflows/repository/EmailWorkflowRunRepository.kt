package org.xbery.artbeams.emailworkflows.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.emailworkflows.domain.EmailWorkflowRun
import java.time.Instant

@Repository
class EmailWorkflowRunRepository(private val dsl: DSLContext) {
    private val runs = table(name("email_workflow_runs"))
    private val id = field(name("id"), String::class.java)
    private val workflowId = field(name("workflow_id"), String::class.java)
    private val userId = field(name("user_id"), String::class.java)
    private val productId = field(name("product_id"), String::class.java)
    private val acquisitionId = field(name("acquisition_id"), String::class.java)
    private val startedAt = field(name("started_at"), Instant::class.java)

    fun createIfAbsent(run: EmailWorkflowRun, at: Instant): Boolean =
        dsl.insertInto(runs)
            .set(id, run.id)
            .set(workflowId, run.workflowId)
            .set(userId, run.userId)
            .set(productId, run.productId)
            .set(acquisitionId, run.acquisitionId)
            .set(startedAt, at)
            .onConflict(workflowId, acquisitionId)
            .doNothing()
            .execute() == 1
}
