package org.xbery.artbeams.common.mapping.repository

import javax.sql.DataSource
import org.slf4j.LoggerFactory
import org.xbery.overview.sql.repo.ScalaSqlRepository

/**
  * Stores key-value pairs.
  * @author Radek Beran
  */
class MapRepository(protected val tableName: String, dataSource: DataSource) extends ScalaSqlRepository[(String, String), String, MapFilter](dataSource, new MapMapper(tableName)) {
  protected val Logger = LoggerFactory.getLogger(this.getClass)
  protected var mapOpt: Option[Map[String, String]] = None

  /**
    * Reloads entries from database and caches them for subsequent {@link #getEntries} calls.
    * @return
    */
  def reloadEntries(): Map[String, String] = {
    Logger.info(s"Loading entries from $tableName")
    val entries = findAllAsSeq()
    val map = entries.toMap
    mapOpt = Some(map)
    map
  }

  /**
    * Returns entries from database (or from cache if already loaded before).
    * @return
    */
  def getEntries(): Map[String, String] = {
    mapOpt match {
      case Some(map) => map
      case None => reloadEntries()
    }
  }
}
