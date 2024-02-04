package org.xbery.artbeams.common.assets.repository

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.repository.ExtendedSqlRepository
import java.time.Instant
import java.util.*
import javax.sql.DataSource

/**
 * Abstract repository for asset-derived entities.
 * @author Radek Beran
 */
abstract class AssetRepository<T : Asset, F : AssetFilter>(
    dataSource: DataSource,
    entityMapper: AssetMapper<T, F>
) : ExtendedSqlRepository<T, String, F>(dataSource, entityMapper) {

    /**
     * Creates new asset with newly generated string id.
     * @param entity
     * @return
     */
    open fun create(entity: T): T {
        val now: Instant = Instant.now()
        // TODO: Set also created by, modified by...
        val entityWithId: T = (entityMapper as AssetMapper).entityWithCommonAttributes(
            entity,
            entity.common.copy(
                id = AssetAttributes.newId(),
                created = now,
                modified = now
            )
        )
        return super.create(entityWithId, false)
    }

    override fun create(entity: T, autogenerateKey: Boolean): T {
        val now: Instant = Instant.now()
        // TODO: Set also created by, modified by...
        return super.create(
            (entityMapper as AssetMapper).entityWithCommonAttributes(
                entity,
                entity.common.copy(
                    created = now,
                    modified = now
                )
            ), autogenerateKey
        )
    }

    override fun update(entity: T): Optional<T> {
        val now: Instant = Instant.now()
        // TODO: Set also modified by...
        return super.update(
            (entityMapper as AssetMapper).entityWithCommonAttributes(
                entity, entity.common.copy(
                    modified = now
                )
            )
        )
    }
}
