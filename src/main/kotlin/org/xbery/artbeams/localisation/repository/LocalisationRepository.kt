package org.xbery.artbeams.localisation.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.mapping.repository.MapRepository
import javax.sql.DataSource

/**
 * Stores key-value pairs for application localisation.
 *
 * @author Radek Beran
 */
@Repository
open class LocalisationRepository(dataSource: DataSource) : MapRepository("localisation", dataSource)
