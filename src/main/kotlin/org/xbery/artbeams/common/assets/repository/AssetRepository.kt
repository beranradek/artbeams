package org.xbery.artbeams.common.assets.repository

import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.jooq.UpdatableRecord
import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.repository.AbstractMappingRepository

/**
 * Abstract implementation of repository that uses JOOQ mapper and unmapper.
 *
 * @author Radek Beran
 */
abstract class AssetRepository<T : Asset, R : UpdatableRecord<R>>(
    override val dsl: DSLContext,
    override val mapper: RecordMapper<R, T>,
    override val unmapper: RecordUnmapper<T, R>
) : AbstractMappingRepository<T, R>(dsl, mapper, unmapper)