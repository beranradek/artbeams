package org.xbery.artbeams.localisation.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.mapping.repository.CachedSqlRepository
import javax.sql.DataSource

/**
 * Stores key-value pairs for application localisation.
 *
 * @author Radek Beran
 */
@Repository
open class LocalisationRepository(dataSource: DataSource) : CachedSqlRepository("localisation", dataSource)
