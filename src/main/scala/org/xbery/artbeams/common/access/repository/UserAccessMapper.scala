package org.xbery.artbeams.common.access.repository

import java.util

import org.xbery.artbeams.common.access.domain.{UserAccess, UserAccessFilter}
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions
import scala.jdk.CollectionConverters._

/**
  * Maps {@link UserAccess} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class UserAccessMapper() extends DynamicEntityMapper[UserAccess, UserAccessFilter] {

  private val cls = classOf[UserAccess]

  override val getTableName: String = "user_access"

  override def createEntity(): UserAccess = UserAccess.Empty

  val idAttr = add(Attr.ofString(cls, "id").get(e => e.id).updatedEntity((e, a) => e.copy(id = a)).primary())
  val timeAttr = add(Attr.ofInstant(cls, "access_time").get(e => e.time).updatedEntity((e, a) => e.copy(time = a)))
  val dateAttr = add(Attr.of(cls, classOf[java.sql.Date], "access_date").get(e => if (e.time != null) new java.sql.Date(e.time.toEpochMilli) else null).updatedEntity((e, a) => e /* Only in DB, not in entity */))
  val ipAttr = add(Attr.ofString(cls, "ip").get(e => e.ip).updatedEntity((e, a) => e.copy(ip = a)))
  val userAgentAttr = add(Attr.ofString(cls, "user_agent").get(e => e.userAgent).updatedEntity((e, a) => e.copy(userAgent = a)))
  val entityTypeAttr = add(Attr.ofString(cls, "entity_type").get(e => e.entityKey.entityType).updatedEntity((e, a) => e.copy(entityKey = e.entityKey.copy(entityType = a))))
  val entityIdAttr = add(Attr.ofString(cls, "entity_id").get(e => e.entityKey.entityId).updatedEntity((e, a) => e.copy(entityKey = e.entityKey.copy(entityId = a))))

  override def composeFilterConditions(filter: UserAccessFilter): util.List[Condition] = {
    val conditions = new util.ArrayList[Condition]
    filter.timeUpperBound.map(timeUpperBound => conditions.add(Conditions.lte(this.timeAttr, timeUpperBound)))
    filter.ids.map(ids => conditions.add(Conditions.in(this.idAttr, ids.asJava)))
    conditions
  }
}

object UserAccessMapper {
  lazy val Instance = new UserAccessMapper()
}
