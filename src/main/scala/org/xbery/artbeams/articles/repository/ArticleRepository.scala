package org.xbery.artbeams.articles.repository

import java.time.Instant
import java.util.Arrays

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.overview.{Order, Overview, Pagination}

import scala.collection.JavaConverters._

/**
  * Article repository.
  * @author Radek Beran
  */
@Repository
class ArticleRepository @Inject() (dataSource: DataSource) extends AssetRepository[Article, ArticleFilter](dataSource, ArticleMapper.Instance) {
  private lazy val mapper = ArticleMapper.Instance
  private lazy val infoAttrNames = mapper.infoAttributes.map(a => a.getName).asJava
  protected lazy val DefaultOrdering = Arrays.asList(new Order(mapper.validFromAttr, true))

  def findArticles(): Seq[Article] = {
    val overview = new Overview(ArticleFilter.Empty, Arrays.asList(new Order(mapper.modifiedAttr, true)))
    findArticleInfos(overview)
  }

  def findLatest(limit: Int): Seq[Article] = {
    val filter = ArticleFilter.validOnBlog()
    val pagination = new Pagination(0, limit)
    val overview = new Overview(filter, DefaultOrdering, pagination)
    findArticleInfos(overview)
  }

  def findByCategoryId(categoryId: String, limit: Int): Seq[Article] = {
    val filter = ArticleFilter.validOnBlogWithCategory(categoryId)
    val pagination = new Pagination(0, limit)
    val overview = new Overview(filter, DefaultOrdering, pagination)
    findArticleInfos(overview)
  }

  def findBySlug(slug: String): Option[Article] = {
    val filter = ArticleFilter.Empty.copy(slug = Some(slug), validityDate = Some(Instant.now()))
    this.findOneByFilter(filter)
  }

  def findByQuery(query: String, limit: Int): Seq[Article] = {
    val filter = ArticleFilter.validByQuery(query)
    val pagination = new Pagination(0, limit)
    val overview = new Overview(filter, DefaultOrdering, pagination)
    findArticleInfos(overview)
  }

  def findArticlesWithExternalIds(): Seq[Article] = {
    val filter = ArticleFilter.Empty.copy(withExternalId = Some(true))
    val overview = new Overview(filter)
    this.findByOverviewAsSeq(overview)
  }

  protected def findArticleInfos(overview: Overview[ArticleFilter]): Seq[Article] = {
    findByOverview(overview, infoAttrNames, getEntityMapper().getTableName(), getEntityMapper()).asScala
  }
}
