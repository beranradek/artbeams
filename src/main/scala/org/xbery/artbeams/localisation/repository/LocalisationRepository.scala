package org.xbery.artbeams.localisation.repository

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.mapping.repository.MapRepository

/**
  * Stores key-value pairs for application localisation.
 *
  * @author Radek Beran
  */
@Repository
class LocalisationRepository @Inject()(dataSource: DataSource) extends MapRepository("localisation", dataSource) {
  // nothing new here
}
