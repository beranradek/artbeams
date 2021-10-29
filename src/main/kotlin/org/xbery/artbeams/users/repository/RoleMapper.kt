package org.xbery.artbeams.users.repository

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.repository.AssetMapper
import org.xbery.artbeams.users.domain.Role
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.sql.filter.SqlCondition
import java.util.*

/**
 * Maps {@link Role} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class RoleMapper : AssetMapper<Role, RoleFilter>() {
    override fun cls(): Class<Role> = Role::class.java
    override fun getTableName(): String = "roles"

    val nameAttr: Attribute<Role, String> = add(Attr.ofString(cls(), "name").get { e -> e.name })

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<Role, *>>,
        aliasPrefix: String?
    ): Role {
        val assetAttributes: AssetAttributes =
            createAssetAttributes(attributeSource, attributes as List<Attribute<*, *>>, aliasPrefix)
        return Role(assetAttributes, nameAttr.getValueFromSource(attributeSource, aliasPrefix ?: ""))
    }

    override fun composeFilterConditions(filter: RoleFilter): MutableList<Condition> {
        val conditions = super.composeFilterConditions(filter)
        filter.userId?.let { userId ->
            val params = mutableListOf<Any>()
            params.add(userId)
            conditions.add(
                SqlCondition("id IN (SELECT role_id FROM user_role WHERE user_id = ?)", params)
            )
        }
        return conditions
    }

    override fun entityWithCommonAttributes(entity: Role, common: AssetAttributes): Role = entity.copy(common = common)

    companion object {
        val Instance: RoleMapper = RoleMapper()
    }
}
