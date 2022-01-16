package org.xbery.artbeams.common.access.service

import com.blueconic.browscap.BrowsCapField
import com.blueconic.browscap.Capabilities
import com.blueconic.browscap.UserAgentParser
import com.blueconic.browscap.UserAgentService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.Dates.AppZoneIdString
import org.xbery.artbeams.common.access.domain.*
import org.xbery.artbeams.common.access.repository.EntityAccessCountRepository
import org.xbery.artbeams.common.access.repository.UserAccessRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.parser.Parsers
import java.time.Instant
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletRequest

/**
 * @author Radek Beran
 */
@Service
open class UserAccessServiceImpl(
    private val userAccessRepository: UserAccessRepository,
    private val entityAccessCountRepository: EntityAccessCountRepository
) : UserAccessService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val parser: UserAgentParser = UserAgentService().loadParser(
        listOf(
            BrowsCapField.BROWSER,
            BrowsCapField.BROWSER_TYPE,
            BrowsCapField.BROWSER_MAJOR_VERSION,
            BrowsCapField.DEVICE_TYPE,
            BrowsCapField.PLATFORM,
            BrowsCapField.PLATFORM_VERSION,
            BrowsCapField.IS_CRAWLER,
            BrowsCapField.IS_MOBILE_DEVICE
        )
    )

    override fun getUserAccessReport(request: HttpServletRequest): UserAccessReport {
        val userAgent = request.getHeader(HttpHeaders.USER_AGENT)
        return createUserAccessReport(userAgent)
    }

    override fun saveUserAccess(entityKey: EntityKey, request: HttpServletRequest): UserAccessReport {
        val userAgent: String = request.getHeader(HttpHeaders.USER_AGENT)
        val report = createUserAccessReport(userAgent)
        if (!report.crawler) {
            registerUserAccess(entityKey, request.remoteAddr, userAgent)
        }
        return report
    }

    // Spring @Async is limited only to public methods and does not work for calls within the same class
    private fun registerUserAccess(
        entityKey: EntityKey,
        ipAddress: String,
        userAgent: String
    ): CompletableFuture<Any> {
        return CompletableFuture.supplyAsync {
            try {
                val userAccess = UserAccess(AssetAttributes.newId(), Instant.now(), ipAddress, userAgent, entityKey)
                userAccessRepository.create(userAccess, false)
            } catch (_: Exception) {
                // This could be for e.g. also org.postgresql.util.PSQLException...
                // There is no easy cross-platform way how to identify duplicate key exception
                // case _: SQLIntegrityConstraintViolationException =>
                // Non-unique access for given day, access will not be stored for the second time
            }
        }
    }

    @Cacheable(EntityAccessCount.CacheName)
    override fun findCountOfVisits(entityKey: EntityKey): Long {
        val entityFilter: EntityAccessCountFilter = EntityAccessCountFilter.Empty.copy(entityKey = entityKey)
        return entityAccessCountRepository.findByFilter(entityFilter, listOf()).firstOrNull()?.let { it.count } ?: 0L
    }

    // Cron pattern: second, minute, hour, day, month, weekday
    @Scheduled(cron = "0 1 0 * * *", zone = AppZoneIdString)
    @CacheEvict(value = [ EntityAccessCount.CacheName ], allEntries = true)
    override fun aggregateUserAccesses() {
        val operationMsg = "User access aggregation task"
        logger.info("$operationMsg - started")
        val currentTime: Instant = Instant.now()
        val accesses = userAccessRepository.findByFilter(
            UserAccessFilter.Empty.copy(timeUpperBound = currentTime)
        )
        val entitiesToCountIncrements: Map<EntityKey, Long> = accesses.groupBy { it.entityKey }
            .mapValues { access -> access.value.size.toLong() }
        val entityKeys: List<EntityKey> = entitiesToCountIncrements.keys.toList()
        val entityTypes: List<String> = entityKeys.map { it.entityType }
        val entityIds: List<String> = entityKeys.map { it.entityId }
        val entityAccessCounts = entityAccessCountRepository.findByFilter(
            EntityAccessCountFilter.Empty.copy(entityTypeIn = entityTypes, entityIdIn = entityIds), listOf()
        )
        val entityKeysToCounts: Map<EntityKey, EntityAccessCount?> =
            entityAccessCounts.groupBy { it.entityKey }
                .mapValues { entityAccess -> entityAccess.value.firstOrNull() /* only one exists */ }
        for ((entityKey, countIncrement) in entitiesToCountIncrements) {
            // Get current aggregated count
            val currentEntityAccessCount = entityKeysToCounts[entityKey]
            if (currentEntityAccessCount != null) {
                entityAccessCountRepository.update(currentEntityAccessCount.copy(count = currentEntityAccessCount.count + countIncrement))
            } else {
                // No accesses so far, insert new record
                entityAccessCountRepository.create(EntityAccessCount(entityKey, countIncrement), false)
            }
        }
        userAccessRepository.deleteByFilter(UserAccessFilter.Empty.copy(ids = accesses.map { it.id }))
        logger.info("$operationMsg - finished")
    }

    private fun parseBooleanField(capabilities: Capabilities, field: BrowsCapField): Boolean {
        val value = capabilities.getValue(field)
        return Parsers.parseBoolean(value)
    }

    private fun createUserAccessReport(userAgent: String): UserAccessReport {
        val capabilities: Capabilities = parser.parse(userAgent)
        val crawler: Boolean = parseBooleanField(capabilities, BrowsCapField.IS_CRAWLER)
        val mobileDevice: Boolean = parseBooleanField(capabilities, BrowsCapField.IS_MOBILE_DEVICE)
        return UserAccessReport(crawler, mobileDevice)
    }
}
