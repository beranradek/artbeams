package org.xbery.artbeams.comments.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.overview.Order
import org.xbery.overview.Overview
import javax.sql.DataSource

/**
 * Comment repository.
 * @author Radek Beran
 */
@Repository
open class CommentRepository(dataSource: DataSource) :
    AssetRepository<Comment, CommentFilter>(dataSource, CommentMapper.Instance) {

    private val defaultOrdering = listOf(Order((entityMapper as CommentMapper).createdAttr, false))

    open fun findComments(): List<Comment> {
        val overview: Overview<CommentFilter> = Overview(CommentFilter.Empty, defaultOrdering)
        return findByOverview(overview)
    }

    open fun findByEntityId(entityId: String): List<Comment> {
        val overview = Overview(CommentFilter.forEntityId(entityId), defaultOrdering)
        return findByOverview(overview)
    }
}
