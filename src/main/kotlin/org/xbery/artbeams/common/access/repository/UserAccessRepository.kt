package org.xbery.artbeams.common.access.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.access.domain.UserAccess
import org.xbery.artbeams.common.access.domain.UserAccessFilter
import org.xbery.artbeams.common.repository.ExtendedSqlRepository
import javax.sql.DataSource

/**
 * Repository for user access records.
 * @author Radek Beran
 */
@Repository
open class UserAccessRepository(dataSource: DataSource) :
    ExtendedSqlRepository<UserAccess, String, UserAccessFilter>(dataSource, UserAccessMapper.Instance)