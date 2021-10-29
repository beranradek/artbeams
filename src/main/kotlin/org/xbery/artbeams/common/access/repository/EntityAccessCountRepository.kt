package org.xbery.artbeams.common.access.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.access.domain.EntityAccessCount
import org.xbery.artbeams.common.access.domain.EntityAccessCountFilter
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.repository.ExtendedSqlRepository
import javax.sql.DataSource

/**
 * Repository for count of user accesses to an entity.
 * @author Radek Beran
 */
@Repository
open class EntityAccessCountRepository(dataSource: DataSource) :
    ExtendedSqlRepository<EntityAccessCount, EntityKey, EntityAccessCountFilter>(
        dataSource,
        EntityAccessCountMapper.Instance
    )
