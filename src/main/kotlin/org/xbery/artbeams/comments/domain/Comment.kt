package org.xbery.artbeams.comments.domain

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
 * Comment entity.
 * @author Radek Beran
 */
data class Comment(
    override val common: AssetAttributes,
    val parentId: String?,
    val state: CommentState,
    val comment: String,
    val userName: String,
    val email: String,
    val entityKey: EntityKey,
    val ip: String,
    val userAgent: String
) : Asset() {
    fun updatedWith(edited: EditedComment, userId: String): Comment {
        return this.copy(
            common = this.common.updatedWith(userId),
            comment = edited.comment,
            userName = edited.userName,
            email = edited.email,
            entityKey = EntityKey.fromClassAndId(Article::class.java, edited.entityId)
        )
    }

    fun toEdited(): EditedComment {
        return EditedComment(this.id, this.entityKey.entityId, this.comment, this.userName, this.email)
    }

    companion object {
        const val CACHE_NAME: String = "comments"
        val EMPTY: Comment = Comment(
            AssetAttributes.EMPTY,
            null,
            CommentState.WAITING_FOR_APPROVAL,
            "", "", "",
            EntityKey.Empty, "", ""
        )
    }
}
