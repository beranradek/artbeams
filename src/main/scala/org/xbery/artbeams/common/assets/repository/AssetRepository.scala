package org.xbery.artbeams.common.assets.repository

import java.time.Instant
import java.util.Optional

import javax.sql.DataSource
import org.xbery.artbeams.common.assets.domain.{Asset, AssetAttributes}
import org.xbery.overview.sql.repo.ScalaSqlRepository

/**
  * Abstract repository for asset-derived entities.
  * @author Radek Beran
  */
abstract class AssetRepository[T <: Asset, F <: AssetFilter](dataSource: DataSource, entityMapper: AssetMapper[T, F]) extends ScalaSqlRepository[T, String, F](dataSource, entityMapper) {
  /**
    * Creates new asset with newly generated string id.
    * @param entity
    * @return
    */
  def create(entity: T): T = {
     val entityWithId = entityMapper.entityWithCommonAttributes(entity, entity.common.copy(id = AssetAttributes.newId()))
     create(entityWithId, false)
  }

  override def create(entity: T, autogenerateKey: Boolean): T = {
    // TODO: Set also created by, modified by...
    val now = Instant.now()
    super.create(entityMapper.entityWithCommonAttributes(entity, entity.common.copy(created = now, modified = now)), autogenerateKey)
  }

  override def update(entity: T): Optional[T] = {
    // TODO: Set also modified by...
    val now = Instant.now()
    super.update(entityMapper.entityWithCommonAttributes(entity, entity.common.copy(modified = now)))
  }
}
