package org.xbery.artbeams.users.repository

import java.util.Arrays

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.users.domain.User
import org.xbery.overview.{Order, Overview}

/**
  * User repository.
  * @author Radek Beran
  */
@Repository
class UserRepository @Inject() (dataSource: DataSource, roleRepository: RoleRepository) extends AssetRepository[User, UserFilter](dataSource, UserMapper.Instance) {
  protected lazy val DefaultOrdering = Arrays.asList(new Order(UserMapper.Instance.loginAttr, false))

  /**
    * Returns user by given id, including his/her roles.
    * @param id
    * @return
    */
  override def findByIdAsOpt(id: String): Option[User] = {
    super.findByIdAsOpt(id).map { user =>
      user.copy(roles = roleRepository.findRolesOfUser(user.id))
    }
  }

  def findUsers(): Seq[User] = {
    this.findByOverviewAsSeq(new Overview(UserFilter.Empty, DefaultOrdering))
  }

  def findByLogin(login: String): Option[User] = {
    val filter = UserFilter.Empty.copy(login = Some(login))
    this.findOneByFilter(filter)
  }

  def findByEmail(email: String): Option[User] = {
    val filter = UserFilter.Empty.copy(email = Some(email))
    this.findOneByFilter(filter)
  }
}
