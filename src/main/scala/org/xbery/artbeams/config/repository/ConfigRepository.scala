package org.xbery.artbeams.config.repository

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.mapping.repository.MapRepository

/**
  * Stores key-value pairs for application configuration.
 *
  * @author Radek Beran
  */
@Repository
class ConfigRepository @Inject()(dataSource: DataSource) extends MapRepository("config", dataSource) {
  def requireConfig(key: String): String = getEntries().getOrElse(key, throw new IllegalStateException(s"$key not configured"))
}
