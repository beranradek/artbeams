package org.xbery.artbeams.common.access.repository

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.access.domain.{UserAccess, UserAccessFilter}
import org.xbery.artbeams.common.repository.ScalaSqlRepository

/**
  * Repository for user access records.
  * @author Radek Beran
  */
@Repository
class UserAccessRepository @Inject() (dataSource: DataSource) extends ScalaSqlRepository[UserAccess, String, UserAccessFilter](dataSource, UserAccessMapper.Instance) {
}
