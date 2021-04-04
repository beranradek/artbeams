package org.xbery.artbeams.users.repository

import java.util.Arrays

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.users.domain.Role
import org.xbery.overview.{Order, Overview}

/**
  * Role repository.
  * @author Radek Beran
  */
@Repository
class RoleRepository @Inject()(dataSource: DataSource) extends AssetRepository[Role, RoleFilter](dataSource, RoleMapper.Instance) {
  protected lazy val DefaultOrdering = Arrays.asList(new Order(RoleMapper.Instance.nameAttr, false))

  def findRoles(): Seq[Role] = {
    this.findByOverviewAsSeq(new Overview(RoleFilter.Empty, DefaultOrdering))
  }

  def findRolesOfUser(userId: String): Seq[Role] = {
    this.findByOverviewAsSeq(new Overview(RoleFilter.Empty.copy(userId = Some(userId)), DefaultOrdering))
  }

  def updateRolesOfUser(userId: String, roles: Seq[Role]): Unit = {
    this.updateAttributeValues("DELETE FROM user_role WHERE user_id = ?", Arrays.asList[AnyRef](userId))
    for {
      role <- roles
    } {
      this.updateAttributeValues("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", Arrays.asList[AnyRef](userId, role.id))
    }
  }
}
