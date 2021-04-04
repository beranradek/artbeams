package org.xbery.artbeams.common.assets.repository

import java.util

import org.xbery.artbeams.common.assets.domain.{Asset, Validity, ValidityAsset}
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions
import org.xbery.overview.sql.filter.SqlCondition

/**
  * Maps {@link ValidityAsset} entity to set of attributes and vice versa. Abstract superclass for DB mappers
  * of validity-asset-derived entities.
  * @author Radek Beran
  */
abstract class ValidityAssetMapper[T <: Asset with ValidityAsset, F <: AssetFilter with ValidityAssetFilter]() extends AssetMapper[T, F] {

  val validFromAttr = add(Attr.ofInstant(cls, "valid_from").get(e => e.validFrom).updatedEntity((e, a) => entityWithValidity(e, e.validity.copy(validFrom = a))))
  val validToAttr = add(Attr.ofInstant(cls, "valid_to").get(e => e.validTo.orNull).updatedEntity((e, a) => entityWithValidity(e, e.validity.copy(validTo = Option(a)))))

  override def composeFilterConditions(filter: F): util.List[Condition] = {
    val conditions = super.composeFilterConditions(filter)
    filter.validityDate.map(validityDate =>
      conditions.add(
        Conditions.and(
          Conditions.lte(validFromAttr, validityDate),
          Conditions.or(
            new SqlCondition(validToAttr.getName() + " IS NULL"),
            Conditions.gte(validToAttr, validityDate)
          )
        )
      )
    )
    conditions
  }

  def entityWithValidity(entity: T, validity: Validity): T
}
