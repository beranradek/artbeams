package org.xbery.artbeams.comments.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.comments.repository.CommentRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.mailer.service.Mailer
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.UserRepository
import java.util.*

/**
 * @author Radek Beran
 */
@Service
open class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val mailer: Mailer
) : CommentService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val normalizationHelper: NormalizationHelper = NormalizationHelper()

    @Cacheable(Comment.CacheName)
    override fun findByEntityId(entityId: String): List<Comment> {
        logger.trace("Finding comments by entity id $entityId")
        return commentRepository.findByEntityId(entityId)
    }

    @CacheEvict(value = [Comment.CacheName], allEntries = true)
    override fun saveComment(
        edited: EditedComment,
        ipAddress: String,
        userAgent: String,
        ctx: OperationCtx
    ): Comment? {
        return try {
            val userId = ctx.loggedUser?.id ?: AssetAttributes.EmptyId
            val updatedComment = if (edited.id == AssetAttributes.EmptyId) {
                var comment =
                    Comment.Empty.updatedWith(edited, userId).copy(ip = ipAddress, userAgent = userAgent)
                comment = commentRepository.create(comment)
                sendNewCommentNotification(comment)
                comment
            } else {
                val comment = commentRepository.findByIdAsOpt(edited.id)
                if (comment != null) {
                    commentRepository.updateEntity(comment.updatedWith(edited, userId))
                } else {
                    null
                }
            }
            updatedComment
        } catch (ex: Exception) {
            logger.error("Update of comment ${edited.id} finished with error ${ex.message}", ex)
            throw ex
        }
    }

    private fun sendNewCommentNotification(comment: Comment) {
        if (comment.entityKey.entityType == Article::class.java.getSimpleName()) {
            val articleId: String = comment.entityKey.entityId
            val articleOpt: Optional<Article> = articleRepository.findById(articleId)
            if (articleOpt.isPresent) {
                val article: Article = articleOpt.get()
                val userOpt: Optional<User> = userRepository.findById(article.createdBy)
                if (userOpt.isPresent) {
                    val user: User = userOpt.get()
                    if (user.email != null && !user.email.isEmpty()) {
                        val subject: String =
                            normalizationHelper.removeDiacriticalMarks("New comment for ${article.title}")
                        val body: String =
                            normalizationHelper.removeDiacriticalMarks("${comment.userName}/${comment.email}:\n\n${comment.comment}")
                        mailer.sendMail(subject, body, user.email)
                    } else {
                        logger.warn("Author ${user.login}/${user.firstName} ${user.lastName} has no email set.")
                    }
                } else {
                    logger.warn("Article ${article.title} has no author (createdBy) set.")
                }
            }
        }
    }
}
