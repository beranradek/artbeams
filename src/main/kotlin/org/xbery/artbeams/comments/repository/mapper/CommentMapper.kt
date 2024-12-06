package org.xbery.artbeams.comments.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.CommentState
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.jooq.schema.tables.records.CommentsRecord

/**
 * @author Radek Beran
 */
@Component
class CommentMapper : RecordMapper<CommentsRecord, Comment> {

    override fun map(record: CommentsRecord): Comment {
        return Comment(
            common = AssetAttributes(
                id = requireNotNull(record.id),
                created = requireNotNull(record.created),
                createdBy = requireNotNull(record.createdBy),
                modified = requireNotNull(record.modified),
                modifiedBy = requireNotNull(record.modifiedBy)
            ),
            parentId = record.parentId,
            state = CommentState.valueOf(requireNotNull(record.state)),
            comment = requireNotNull(record.comment),
            userName = requireNotNull(record.username),
            email = requireNotNull(record.email),
            entityKey = EntityKey(
                entityType = requireNotNull(record.entityType),
                entityId = requireNotNull(record.entityId)
            ),
            ip = requireNotNull(record.ip),
            userAgent = requireNotNull(record.userAgent)
        )
    }
}
