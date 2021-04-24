package org.xbery.artbeams.common.access.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.access.domain._
import org.xbery.artbeams.common.repository.ScalaSqlRepository

import javax.inject.Inject
import javax.sql.DataSource

/**
  * Repository for count of user accesses to an entity.
  * @author Radek Beran
  */
@Repository
class EntityAccessCountRepository @Inject()(dataSource: DataSource) extends ScalaSqlRepository[EntityAccessCount, EntityKey, EntityAccessCountFilter](dataSource, EntityAccessCountMapper.Instance) {
}
