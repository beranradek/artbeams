package org.xbery.artbeams.categories.repository

import java.time.Instant
import java.util.Arrays

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.overview.{Order, Overview}

/**
  * Category repository.
  * @author Radek Beran
  */
@Repository
class CategoryRepository @Inject() (dataSource: DataSource) extends AssetRepository[Category, CategoryFilter](dataSource, CategoryMapper.Instance) {
  private lazy val mapper = CategoryMapper.Instance
  protected lazy val DefaultOrdering = Arrays.asList(new Order(mapper.titleAttr, false))

  def findCategories(): Seq[Category] = {
    val overview = new Overview(CategoryFilter.Empty, DefaultOrdering)
    findByOverviewAsSeq(overview)
  }

  def findBySlug(slug: String): Option[Category] = {
    val filter = CategoryFilter.Empty.copy(slug = Some(slug), validityDate = Some(Instant.now()))
    this.findOneByFilter(filter)
  }
}
