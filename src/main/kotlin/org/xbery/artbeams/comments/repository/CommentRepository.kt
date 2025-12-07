package org.xbery.artbeams.comments.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.CommentState
import org.xbery.artbeams.comments.repository.mapper.CommentMapper
import org.xbery.artbeams.comments.repository.mapper.CommentUnmapper
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
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

    fun findComments(pagination: Pagination): ResultPage<Comment> =
        findByCriteria(
            null,
            COMMENTS.CREATED.desc(),
            pagination,
            mapper
        )

    /**
     * Finds comments with optional search filters.
     * @param searchTerm Search term to match against author name, email, or content
     * @param state Filter by comment state (optional)
     * @param pagination Pagination parameters
     * @return Page of comments matching the criteria
     */
    fun searchComments(
        searchTerm: String?,
        state: CommentState?,
        pagination: Pagination
    ): ResultPage<Comment> {
        val conditions = mutableListOf<org.jooq.Condition>()

        // Add search term condition (search in username, email, and comment)
        if (!searchTerm.isNullOrBlank()) {
            val searchPattern = "%${searchTerm.lowercase()}%"
            conditions.add(
                DSL.lower(COMMENTS.USERNAME).like(searchPattern)
                    .or(DSL.lower(COMMENTS.EMAIL).like(searchPattern))
                    .or(DSL.lower(COMMENTS.COMMENT).like(searchPattern))
            )
        }

        // Add state filter
        if (state != null) {
            conditions.add(COMMENTS.STATE.eq(state.name))
        }

        val condition = if (conditions.isNotEmpty()) {
            conditions.reduce { acc, c -> acc.and(c) }
        } else {
            null
        }

        return findByCriteria(
            condition,
            COMMENTS.CREATED.desc(),
            pagination,
            mapper
        )
    }

    /**
     * Finds approved comments for an entity (e.g. article id).
     * @param entityId
     * @return
     */
    fun findApprovedByEntityId(entityId: String): List<Comment> =
        dsl.selectFrom(table)
            .where(COMMENTS.ENTITY_ID.eq(entityId), COMMENTS.STATE.eq(CommentState.APPROVED.name))
            .orderBy(COMMENTS.CREATED)
            .fetch(mapper)

    fun updateState(id: String, state: CommentState): Boolean {
        return dsl.update(COMMENTS)
            .set(COMMENTS.STATE, state.name)
            .where(COMMENTS.ID.eq(id))
            .execute() > 0
    }
}
