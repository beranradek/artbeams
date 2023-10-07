package org.xbery.artbeams.users.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.users.domain.User
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.repo.Conditions
import java.time.Instant

/**
 * Maps {@link User} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class UserMapper : AssetMapper<User, UserFilter>() {
    override fun cls(): Class<User> = User::class.java
    override fun getTableName(): String = "users"

    val loginAttr: Attribute<User, String> = add(Attr.ofString(cls(), "login").get { e -> e.login})
    val passwordAttr: Attribute<User, String> = add(Attr.ofString(cls(), "password").get { e -> e.password})
    val firstNameAttr: Attribute<User, String> = add(Attr.ofString(cls(), "first_name").get { e -> e.firstName})
    val lastNameAttr: Attribute<User, String> = add(Attr.ofString(cls(), "last_name").get { e -> e.lastName})
    val emailAttr: Attribute<User, String> = add(Attr.ofString(cls(), "email").get { e -> e.email})
    val consentAttr: Attribute<User, Instant> =
        add(Attr.ofInstant(cls(), "consent").get { e -> e.consent })

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<User, *>>,
        aliasPrefix: String?
    ): User {
        val assetAttributes: AssetAttributes =
            createAssetAttributes(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        return User(
            assetAttributes,
            loginAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            passwordAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            firstNameAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            lastNameAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            emailAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""),
            listOf(),
            consentAttr.getValueFromSource(attributeSource, aliasPrefix ?: "")
        )
    }

    override fun composeFilterConditions(filter: UserFilter): MutableList<Condition> {
        val conditions = super.composeFilterConditions(filter)
        filter.login?.let { login -> conditions.add(Conditions.eq(this.loginAttr, login)) }
        filter.email?.let { email -> conditions.add(Conditions.eq(this.emailAttr, email)) }
        return conditions
    }

    override fun entityWithCommonAttributes(entity: User, common: AssetAttributes): User = entity.copy(common = common)

    companion object {
        val Instance = UserMapper()
    }
}
