package org.xbery.artbeams.comments.repository

import java.util.Arrays

import javax.inject.Inject
import javax.sql.DataSource
import org.springframework.stereotype.Repository
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.overview.{Order, Overview}

/**
  * Comment repository.
  * @author Radek Beran
  */
@Repository
class CommentRepository @Inject() (dataSource: DataSource) extends AssetRepository[Comment, CommentFilter](dataSource, CommentMapper.Instance) {
  private lazy val mapper = CommentMapper.Instance
  protected lazy val DefaultOrdering = Arrays.asList(new Order(mapper.createdAttr, false))

  def findComments(): Seq[Comment] = {
    val overview = new Overview(CommentFilter.Empty, DefaultOrdering)
    findByOverviewAsSeq(overview)
  }

  def findByEntityId(entityId: String): Seq[Comment] = {
    val overview = new Overview(CommentFilter.forEntityId(entityId), DefaultOrdering)
    findByOverviewAsSeq(overview)
  }
}
