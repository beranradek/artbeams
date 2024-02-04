package org.xbery.artbeams.common.repository

import org.xbery.overview.mapper.EntityMapper
import org.xbery.overview.sql.repo.SqlRepository
import java.util.*
import javax.sql.DataSource

/**
 * Abstract SQL repository with project extensions.
 * @author Radek Beran
 */
abstract class ExtendedSqlRepository<T, K, F>(dataSource: DataSource, entityMapper: EntityMapper<T, F>) : SqlRepository<T, K, F>(dataSource, entityMapper) {
    open fun findByIdAsOpt(id: K): T? {
        val opt: Optional<T> = findById(id)
        return if (opt.isPresent) {
            opt.get()
        } else {
            null
        }
    }

    open fun findOneByFilter(filter: F): T? {
        val list = this.findByFilter(filter, listOf())
        return if (list.isNotEmpty()) {
            list[0]
        } else {
            null
        }
    }

    open fun updateEntity(entity: T): T? {
        val entityOpt: Optional<T> = super.update(entity)
        return if (entityOpt.isPresent) {
            entityOpt.get()
        } else {
            null
        }
    }
}
