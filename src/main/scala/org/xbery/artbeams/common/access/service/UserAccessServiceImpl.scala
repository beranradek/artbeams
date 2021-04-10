package org.xbery.artbeams.common.access.service

import com.blueconic.browscap.{BrowsCapField, Capabilities, UserAgentService}
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.{CacheEvict, Cacheable}
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.Dates
import org.xbery.artbeams.common.access.domain._
import org.xbery.artbeams.common.access.repository.{EntityAccessCountRepository, UserAccessRepository}
import org.xbery.artbeams.common.assets.domain.AssetAttributes

import java.time.Instant
import java.util
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Radek Beran
  */
@Service
class UserAccessServiceImpl @Inject() (userAccessRepository: UserAccessRepository, entityAccessCountRepository: EntityAccessCountRepository, implicit val ec: ExecutionContext) extends UserAccessService {

  private lazy val logger = LoggerFactory.getLogger(this.getClass)

  private val parser = new UserAgentService().loadParser(util.Arrays.asList(
    BrowsCapField.BROWSER,
    BrowsCapField.BROWSER_TYPE,
    BrowsCapField.BROWSER_MAJOR_VERSION,
    BrowsCapField.DEVICE_TYPE,
    BrowsCapField.PLATFORM,
    BrowsCapField.PLATFORM_VERSION,
    BrowsCapField.IS_CRAWLER,
    BrowsCapField.IS_MOBILE_DEVICE
  ))

  override def getUserAccessReport(request: HttpServletRequest): UserAccessReport = {
    val userAgent = request.getHeader(HttpHeaders.USER_AGENT)
    createUserAccessReport(userAgent)
  }

  override def saveUserAccess(entityKey: EntityKey, request: HttpServletRequest): UserAccessReport = {
    val userAgent = request.getHeader(HttpHeaders.USER_AGENT)
    val report = createUserAccessReport(userAgent)

    // We will store and count only human (non-bot/non-crawler) accesses for the entities such as articles.
    if (!report.crawler) {
      // Run in background:
      val fUserAccess = registerUserAccess(entityKey, request.getRemoteAddr(), userAgent)
    }
    report
  }

  // Spring @Async is limited only to public methods and does not work for calls within the same class
  private def registerUserAccess(entityKey: EntityKey, ipAddress: String, userAgent: String): Future[Any] = {
    Future {
      try {
        val userAccess = UserAccess(AssetAttributes.newId(), Instant.now(), ipAddress, userAgent, entityKey)
        userAccessRepository.create(userAccess, false)
      } catch {
        case _: Exception =>
        // This could be for e.g. also org.postgresql.util.PSQLException...
        // There is no easy cross-platform way how to identify duplicate key exception
        // case _: SQLIntegrityConstraintViolationException =>
        // Non-unique access for given day, access will not be stored for the second time
      }
    }
  }

  @Cacheable(Array(EntityAccessCount.CacheName))
  override def findCountOfVisits(entityKey: EntityKey): Long = {
    val entityFilter = EntityAccessCountFilter.Empty.copy(entityKey = Some(entityKey))
    entityAccessCountRepository.findByFilter(entityFilter, Seq.empty).headOption.map(_.count).getOrElse(0L)
  }

  // Cron pattern: second, minute, hour, day, month, weekday
  @Scheduled(cron = "0 1 0 * * *", zone = Dates.AppZoneIdString)
  @CacheEvict(value = Array(EntityAccessCount.CacheName), allEntries = true)
  override def aggregateUserAccesses(): Unit = {
    val operationMsg = s"User access aggregation task"
    logger.info(s"${operationMsg} - started")

    // Fetch non-aggregated user accesses older (or equal) than current time
    val currentTime = Instant.now()
    // TODO RBe: Create findByFilterAsSeq in ScalaSqlRepository
    val accesses = userAccessRepository.findByFilter(UserAccessFilter.Empty.copy(timeUpperBound = Some(currentTime)), Seq.empty)
    val entitiesToCountIncrements: Map[EntityKey, Long] = accesses.groupBy(_.entityKey).mapValues(userAccesses => userAccesses.size)

    // Aggregate user accesses into summary counts for entities
    val entityKeys = entitiesToCountIncrements.keys.toSeq
    val entityTypes = entityKeys.map(_.entityType)
    val entityIds = entityKeys.map(_.entityId)

    // Fetch current aggregated counts for entities
    val entityAccessCounts = entityAccessCountRepository.findByFilter(EntityAccessCountFilter.Empty.copy(entityTypeIn = Some(entityTypes), entityIdIn = Some(entityIds)), Seq.empty)
    val entityKeysToCounts: Map[EntityKey, EntityAccessCount] = entityAccessCounts.groupBy(_.entityKey).mapValues(entityAccessCounts => entityAccessCounts.head /* only one exists */)

    // Traverse non-aggregated increments and
    // update current aggregated counts or insert new count records if aggregation for increment does not exist yet
    for {
      (entityKey, countIncrement) <- entitiesToCountIncrements
    } {
      // Get current aggregated count
      entityKeysToCounts.get(entityKey) match {
        case Some(currentEntityAccessCount) =>
          entityAccessCountRepository.update(currentEntityAccessCount.copy(count = currentEntityAccessCount.count + countIncrement))
        case None =>
          // No accesses so far, insert new record
          entityAccessCountRepository.create(EntityAccessCount(entityKey, countIncrement), false)
      }
    }

    // Delete already aggregated user accesses
    userAccessRepository.deleteByFilter(UserAccessFilter.Empty.copy(ids = Some(accesses.map(_.id))))
    logger.info(s"${operationMsg} - finished")
  }

  private def parseBooleanField(capabilities: Capabilities, field: BrowsCapField): Boolean = {
    val value = capabilities.getValue(field)
    value != null && java.lang.Boolean.parseBoolean(value)
  }

  private def createUserAccessReport(userAgent: String): UserAccessReport = {
    // Example of resulting client capabilities: http://browscap.org/ua-lookup
    val capabilities = parser.parse(userAgent)
    val crawler = parseBooleanField(capabilities, BrowsCapField.IS_CRAWLER)
    val mobileDevice = parseBooleanField(capabilities, BrowsCapField.IS_MOBILE_DEVICE)
    UserAccessReport(crawler, mobileDevice)
  }
}
