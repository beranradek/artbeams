package org.xbery.artbeams.common.access.repository

import java.util
import org.xbery.artbeams.common.access.domain.{EntityKey, UserAccess, UserAccessFilter}
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

  val idAttr = add(Attr.ofString(cls, "id").get(e => e.id).primary())
  val timeAttr = add(Attr.ofInstant(cls, "access_time").get(e => e.time))
  val dateAttr = add(Attr.of(cls, classOf[java.sql.Date], "access_date").get(e => if (e.time != null) new java.sql.Date(e.time.toEpochMilli) else null))
  val ipAttr = add(Attr.ofString(cls, "ip").get(e => e.ip))
  val userAgentAttr = add(Attr.ofString(cls, "user_agent").get(e => e.userAgent))
  val entityTypeAttr = add(Attr.ofString(cls, "entity_type").get(e => e.entityKey.entityType))
  val entityIdAttr = add(Attr.ofString(cls, "entity_id").get(e => e.entityKey.entityId))

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[UserAccess, _]], aliasPrefix: String): UserAccess = {
    val entityKey = EntityKey(
      entityTypeAttr.getValueFromSource(attributeSource, aliasPrefix),
      entityIdAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
    UserAccess(
      idAttr.getValueFromSource(attributeSource, aliasPrefix),
      timeAttr.getValueFromSource(attributeSource, aliasPrefix),
      ipAttr.getValueFromSource(attributeSource, aliasPrefix),
      userAgentAttr.getValueFromSource(attributeSource, aliasPrefix),
      entityKey
    )
  }

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
