package org.xbery.artbeams.faq.service

import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.faq.domain.FaqEntityType
import org.xbery.artbeams.faq.domain.FaqEntry

/**
 * @author Radek Beran
 */
interface FaqService {
    fun findByEntity(entityType: FaqEntityType, entityId: String): List<FaqEntry>

    fun create(ctx: OperationCtx, entityType: FaqEntityType, entityId: String, question: String, answer: String, sortOrder: Int): FaqEntry

    fun update(ctx: OperationCtx, id: String, question: String, answer: String, sortOrder: Int): FaqEntry

    fun delete(ctx: OperationCtx, id: String): Boolean
}
