package org.xbery.artbeams.users.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.users.domain.User
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

import java.util

/**
  * Maps {@link User} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class UserMapper() extends AssetMapper[User, UserFilter] {

  override protected def cls = classOf[User]

  override val getTableName: String = "users"

  val loginAttr = add(Attr.ofString(cls, "login").get(e => e.login))
  val passwordAttr = add(Attr.ofString(cls, "password").get(e => e.password))
  val firstNameAttr = add(Attr.ofString(cls, "first_name").get(e => e.firstName))
  val lastNameAttr = add(Attr.ofString(cls, "last_name").get(e => e.lastName))
  val emailAttr = add(Attr.ofString(cls, "email").get(e => e.email))
  val consentAttr = add(Attr.ofInstant(cls, "consent").get(e => e.consent.orNull))

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[User, _]], aliasPrefix: String): User = {
    val assetAttributes = createAssetAttributes(attributeSource, attributes.asInstanceOf[util.List[Attribute[_, _]]], aliasPrefix)
    User(
      assetAttributes,
      loginAttr.getValueFromSource(attributeSource, aliasPrefix),
      passwordAttr.getValueFromSource(attributeSource, aliasPrefix),
      firstNameAttr.getValueFromSource(attributeSource, aliasPrefix),
      lastNameAttr.getValueFromSource(attributeSource, aliasPrefix),
      emailAttr.getValueFromSource(attributeSource, aliasPrefix),
      Seq.empty,
      Option(consentAttr.getValueFromSource(attributeSource, aliasPrefix))
    )
  }

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
