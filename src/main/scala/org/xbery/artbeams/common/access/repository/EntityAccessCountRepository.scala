package org.xbery.artbeams.common.access.repository

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.access.domain._
import org.xbery.overview.sql.repo.ScalaSqlRepository

/**
  * Repository for count of user accesses to an entity.
  * @author Radek Beran
  */
@Repository
class EntityAccessCountRepository @Inject()(dataSource: DataSource) extends ScalaSqlRepository[EntityAccessCount, EntityKey, EntityAccessCountFilter](dataSource, EntityAccessCountMapper.Instance) {
}
