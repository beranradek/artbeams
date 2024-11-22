package org.xbery.artbeams.comments.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.repository.mapper.CommentMapper
import org.xbery.artbeams.comments.repository.mapper.CommentUnmapper
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.jooq.schema.tables.records.CommentsRecord
import org.xbery.artbeams.jooq.schema.tables.references.COMMENTS

/**
 * Comment repository.
 * @author Radek Beran
 */
@Repository
class CommentRepository(
    override val dsl: DSLContext,
    override val mapper: CommentMapper,
    override val unmapper: CommentUnmapper
) : AssetRepository<Comment, CommentsRecord>(
    dsl, mapper, unmapper
) {
    override val table: Table<CommentsRecord> = COMMENTS
    override val idField: Field<String?> = COMMENTS.ID

    fun findComments(): List<Comment> =
        dsl.selectFrom(table)
            .orderBy(COMMENTS.CREATED)
            .fetch(mapper)

    fun findByEntityId(entityId: String): List<Comment> =
        dsl.selectFrom(table)
            .where(COMMENTS.ENTITY_ID.eq(entityId))
            .orderBy(COMMENTS.CREATED)
            .fetch(mapper)
}
