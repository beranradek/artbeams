package org.xbery.artbeams.faq.service

import org.jooq.exception.DataAccessException
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.faq.domain.FaqEntityType
import org.xbery.artbeams.faq.domain.FaqEntry
import org.xbery.artbeams.faq.repository.FaqEntryRepository

/**
 * @author Radek Beran
 */
@Service
class FaqServiceImpl(
    private val faqEntryRepository: FaqEntryRepository
) : FaqService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Cacheable(
        value = [FaqEntry.CACHE_NAME],
        key = "#entityType.name() + ':' + #entityId",
        unless = "#result.isEmpty()"
    )
    override fun findByEntity(entityType: FaqEntityType, entityId: String): List<FaqEntry> =
        try {
            faqEntryRepository.findByEntity(entityType, entityId)
        } catch (e: DataAccessException) {
            // Safe fallback for deployments where DB migration hasn't been applied yet.
            logger.warn("Unable to load FAQ entries (entityType={}, entityId={}): {}", entityType, entityId, e.message)
            emptyList()
        }

    @CacheEvict(value = [FaqEntry.CACHE_NAME], key = "#entityType.name() + ':' + #entityId")
    override fun create(
        ctx: OperationCtx,
        entityType: FaqEntityType,
        entityId: String,
        question: String,
        answer: String,
        sortOrder: Int
    ): FaqEntry {
        val userId = ctx.loggedUser?.id ?: "SYSTEM"
        val common = AssetAttributes.EMPTY.updatedWith(userId)
        return faqEntryRepository.create(
            FaqEntry(
                common = common,
                entityType = entityType,
                entityId = entityId,
                question = question.trim(),
                answer = answer.trim(),
                sortOrder = sortOrder
            )
        )
    }

    override fun update(ctx: OperationCtx, id: String, question: String, answer: String, sortOrder: Int): FaqEntry {
        val existing = faqEntryRepository.requireById(id)
        val userId = ctx.loggedUser?.id ?: "SYSTEM"
        val updatedCommon = existing.common.updatedWith(userId)
        val updated =
            existing.copy(
                common = updatedCommon,
                question = question.trim(),
                answer = answer.trim(),
                sortOrder = sortOrder
            )
        val stored = faqEntryRepository.update(updated)
        // Cache eviction needs entity context, do it explicitly.
        evictEntityCache(existing.entityType, existing.entityId)
        return stored
    }

    override fun delete(ctx: OperationCtx, id: String): Boolean {
        val existing = faqEntryRepository.findById(id) ?: return false
        val deleted = faqEntryRepository.deleteById(id)
        evictEntityCache(existing.entityType, existing.entityId)
        return deleted
    }

    @CacheEvict(value = [FaqEntry.CACHE_NAME], key = "#entityType.name() + ':' + #entityId")
    fun evictEntityCache(entityType: FaqEntityType, entityId: String) {
        // Intentionally empty: annotation-driven eviction only.
    }
}
