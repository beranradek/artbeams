package org.xbery.artbeams.common.repository

import javax.sql.DataSource
import org.xbery.overview.mapper.EntityMapper
import org.xbery.overview.sql.repo.SqlRepository
import org.xbery.overview.{Order, Overview}

import scala.jdk.CollectionConverters._

/**
  * Abstract SQL repository for Scala language.
  * @author Radek Beran
  */
abstract class ScalaSqlRepository[T, K, F](dataSource: DataSource, entityMapper: EntityMapper[T, F]) extends SqlRepository[T, K, F](dataSource, entityMapper) {

  def findAllAsSeq(): Seq[T] = {
    findAll().asScala.toSeq
  }

  def findByIdAsOpt(id: K): Option[T] = {
    val opt = findById(id)
    if (opt.isPresent()) {
      Some(opt.get())
    } else {
      None
    }
  }

  def findByFilter(filter: F, order: Seq[Order]): Seq[T] = {
    this.findByFilter(filter, order.asJava).asScala.toSeq
  }

  def findOneByFilter(filter: F): Option[T] = {
    this.findByFilter(filter, Seq.empty).headOption
  }

  def updateEntity(entity: T): Option[T] = {
    val entityOpt = super.update(entity)
    if (entityOpt.isPresent()) {
      Some(entityOpt.get())
    } else {
      None
    }
  }

  def findByOverviewAsSeq(overview: Overview[F], entityMapper: EntityMapper[T, F]): Seq[T] = {
    return findByOverview(overview, entityMapper).asScala.toSeq
  }

  def findByOverviewAsSeq(overview: Overview[F]): Seq[T] = {
    return findByOverviewAsSeq(overview, getEntityMapper())
  }
}