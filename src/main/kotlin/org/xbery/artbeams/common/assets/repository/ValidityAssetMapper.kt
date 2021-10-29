package org.xbery.artbeams.common.assets.repository

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.common.assets.domain.ValidityAsset
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.repo.Conditions
import org.xbery.overview.sql.filter.SqlCondition
import java.time.Instant

/**
 * Maps {@link ValidityAsset} entity to set of attributes and vice versa. Abstract superclass for DB mappers
 * of validity-asset-derived entities.
 * @author Radek Beran
 */
abstract class ValidityAssetMapper<T, F> :
    AssetMapper<T, F>() where T : Asset, T : ValidityAsset, F : AssetFilter, F : ValidityAssetFilter {
    val validFromAttr: Attribute<T, Instant> = add(Attr.ofInstant(cls(), "valid_from").get { e -> e.validFrom })
    val validToAttr: Attribute<T, Instant> =
        add(Attr.ofInstant(cls(), "valid_to").get { e -> e.validTo })

    protected fun createValidity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<*, *>>,
        aliasPrefix: String?
    ): Validity {
        val projectedAttributeNames: Set<String> =
            attributes.map { it.name }.toSet()
        return Validity(
            requireValueFromSource(
                validFromAttr,
                attributeSource,
                aliasPrefix
            ),
            if (projectedAttributeNames.contains(validToAttr.name))
                getValueFromSource(
                validToAttr,
                attributeSource,
                aliasPrefix
            ) else null
        )
    }

    override fun composeFilterConditions(filter: F): MutableList<Condition> {
        val conditions = super.composeFilterConditions(filter)
        filter.validityDate?.let { validityDate ->
            conditions.add(
                Conditions.and(
                    Conditions.lte(validFromAttr, validityDate),
                    Conditions.or(
                        SqlCondition(validToAttr.name + " IS NULL"),
                        Conditions.gte(validToAttr, validityDate)
                    )
                )
            )
        }
        return conditions
    }
}
