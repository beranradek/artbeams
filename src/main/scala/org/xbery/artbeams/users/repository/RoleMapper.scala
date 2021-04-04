package org.xbery.artbeams.users.repository

import java.util

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.users.domain.Role
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.sql.filter.SqlCondition

/**
  * Maps {@link Role} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class RoleMapper() extends AssetMapper[Role, RoleFilter] {

  override protected def cls = classOf[Role]

  override val getTableName: String = "roles"

  override def createEntity(): Role = Role.Empty

  val nameAttr = add(Attr.ofString(cls, "name").get(e => e.name).updatedEntity((e, a) => e.copy(name = a)))

  override def composeFilterConditions(filter: RoleFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.userId.map(userId => {
      val params = new util.ArrayList[Object]()
      params.add(userId)
      conditions.add(new SqlCondition("id IN (SELECT role_id FROM user_role WHERE user_id = ?)", params))
    })
    conditions
  }

  override def entityWithCommonAttributes(entity: Role, common: AssetAttributes): Role = entity.copy(common = common)
}

object RoleMapper {
  lazy val Instance = new RoleMapper()
}

