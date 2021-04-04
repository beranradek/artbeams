package org.xbery.artbeams.users.repository

import java.util

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.users.domain.User
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

/**
  * Maps {@link User} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class UserMapper() extends AssetMapper[User, UserFilter] {

  override protected def cls = classOf[User]

  override val getTableName: String = "users"

  override def createEntity(): User = User.Empty

  val loginAttr = add(Attr.ofString(cls, "login").get(e => e.login).updatedEntity((e, a) => e.copy(login = a)))
  val passwordAttr = add(Attr.ofString(cls, "password").get(e => e.password).updatedEntity((e, a) => e.copy(password = a)))
  val firstNameAttr = add(Attr.ofString(cls, "first_name").get(e => e.firstName).updatedEntity((e, a) => e.copy(firstName = a)))
  val lastNameAttr = add(Attr.ofString(cls, "last_name").get(e => e.lastName).updatedEntity((e, a) => e.copy(lastName = a)))
  val emailAttr = add(Attr.ofString(cls, "email").get(e => e.email).updatedEntity((e, a) => e.copy(email = a)))
  val consentAttr = add(Attr.ofInstant(cls, "consent").get(e => e.consent.orNull).updatedEntity((e, a) => e.copy(consent = Option(a))))

  override def composeFilterConditions(filter: UserFilter): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.login.map(login => conditions.add(Conditions.eq(this.loginAttr, login)))
    filter.email.map(email => conditions.add(Conditions.eq(this.emailAttr, email)))
    conditions
  }

  override def entityWithCommonAttributes(entity: User, common: AssetAttributes): User = entity.copy(common = common)
}

object UserMapper {
  lazy val Instance = new UserMapper()
}
