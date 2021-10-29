package org.xbery.artbeams.comments.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
 * @author Radek Beran
 */
data class CommentFilter(override val id: String?, override val ids: List<String>?, override val createdBy: String?, val entityId: String?) : AssetFilter {
  companion object  {
     val Empty: CommentFilter = CommentFilter(null, null, null, null)
    fun forEntityId(entityId: String): CommentFilter = CommentFilter.Empty.copy(entityId = entityId)
  }
}
