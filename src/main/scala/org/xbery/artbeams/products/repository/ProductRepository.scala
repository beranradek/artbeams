package org.xbery.artbeams.products.repository

import java.util.Arrays

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.products.domain.Product
import org.xbery.overview.{Order, Overview}

/**
  * Product repository.
  * @author Radek Beran
  */
@Repository
class ProductRepository @Inject() (dataSource: DataSource) extends AssetRepository[Product, ProductFilter](dataSource, ProductMapper.Instance) {
  private lazy val mapper = ProductMapper.Instance
  protected lazy val DefaultOrdering = Arrays.asList(new Order(mapper.titleAttr, false))

  def findProducts(): Seq[Product] = {
    val overview = new Overview(ProductFilter.Empty, DefaultOrdering)
    findByOverviewAsSeq(overview)
  }

  def findBySlug(slug: String): Option[Product] = {
    val filter = ProductFilter.Empty.copy(slug = Some(slug))
    this.findOneByFilter(filter)
  }
}
