package org.xbery.artbeams.comments.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.jooq.schema.tables.records.CommentsRecord
import org.xbery.artbeams.jooq.schema.tables.references.COMMENTS

/**
 * @author Radek Beran
 */
@Component
class CommentUnmapper : RecordUnmapper<Comment, CommentsRecord> {

    override fun unmap(comment: Comment): CommentsRecord {
        val record = COMMENTS.newRecord()
        record.id = comment.common.id
        record.created = comment.common.created
        record.createdBy = comment.common.createdBy
        record.modified = comment.common.modified
        record.modifiedBy = comment.common.modifiedBy
        record.parentId = comment.parentId
        record.state = comment.state.name
        record.comment = comment.comment
        record.username = comment.userName
        record.email = comment.email
        record.entityType = comment.entityKey.entityType
        record.entityId = comment.entityKey.entityId
        record.ip = comment.ip
        record.userAgent = comment.userAgent
        return record
    }
}
