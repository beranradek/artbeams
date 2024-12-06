package org.xbery.artbeams.comments.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.CommentState
import org.xbery.artbeams.comments.domain.EditedComment
import org.xbery.artbeams.comments.repository.CommentRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.mailer.service.MailgunMailSender
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.users.repository.UserRepository

/**
 * @author Radek Beran
 */
@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val mailSender: MailgunMailSender,
    private val appConfig: AppConfig
) : CommentService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val normalizationHelper: NormalizationHelper = NormalizationHelper()

    @Cacheable(Comment.CacheName)
    override fun findApprovedByEntityId(entityId: String): List<Comment> {
        logger.trace("Finding approved comments by entity id $entityId")
        return commentRepository.findApprovedByEntityId(entityId)
    }

    @CacheEvict(value = [Comment.CacheName], allEntries = true)
    override fun saveComment(
        edited: EditedComment,
        ipAddress: String,
        userAgent: String,
        ctx: OperationCtx
    ): Comment {
        return try {
            val userId = ctx.loggedUser?.id ?: AssetAttributes.EMPTY_ID
            val updatedComment = if (edited.id == AssetAttributes.EMPTY_ID) {
                var comment =
                    approvedOrWaiting(Comment.Empty.updatedWith(edited, userId).copy(ip = ipAddress, userAgent = userAgent))
                comment = commentRepository.create(comment)
                sendNewCommentNotification(comment)
                comment
            } else {
                val comment = commentRepository.requireById(edited.id)
                commentRepository.update(approvedOrWaiting(comment.updatedWith(edited, userId)))
            }
            updatedComment
        } catch (ex: Exception) {
            logger.error("Update of comment ${edited.id} finished with error ${ex.message}", ex)
            throw ex
        }
    }

    override fun findComments(): List<Comment> {
        return commentRepository.findComments()
    }

    @CacheEvict(value = [Comment.CacheName], allEntries = true)
    override fun deleteComment(id: String): Boolean {
        logger.info("Deleting comment $id")
        return commentRepository.deleteById(id)
    }

    @CacheEvict(value = [Comment.CacheName], allEntries = true)
    override fun updateCommentState(id: String, state: CommentState): Boolean {
        logger.info("Updating state of comment $id to $state")
        return commentRepository.updateState(id, state)
    }

    private fun approvedOrWaiting(comment: Comment): Comment {
        if (containsSuspiciousWords(comment.comment)) {
            return comment.copy(state = CommentState.WAITING_FOR_APPROVAL)
        }
        return comment.copy(state = CommentState.APPROVED)
    }

    private fun containsSuspiciousWords(commentText: String): Boolean {
        val suspiciousWords = getSuspiciousWords()
        return suspiciousWords.any { commentText.contains(it, ignoreCase = true) }
    }

    private fun getSuspiciousWords(): List<String> {
        return appConfig.findConfig("comments.suspiciousWords")?.split(",") ?: DEFAULT_SUSPICIOUS_WORDS
    }

    private fun sendNewCommentNotification(comment: Comment) {
        try {
            val articleClassName = Article::class.java.getSimpleName()
            if (comment.entityKey.entityType == articleClassName) {
                val articleId: String = comment.entityKey.entityId
                val article = articleRepository.findById(articleId)
                if (article != null) {
                    val user = userRepository.findById(article.createdBy)
                    if (user != null) {
                        if (user.email.isNotEmpty()) {
                            val subject: String =
                                normalizationHelper.removeDiacriticalMarks("New comment for ${article.title}")
                            val body: String =
                                normalizationHelper.removeDiacriticalMarks("User ${comment.userName}/${comment.email} " +
                                    "commented:\n\n${comment.comment}\n\n" +
                                    "comment state:\n\n${comment.state}"
                                )
                            mailSender.sendMailWithText(user.email, subject, body)
                        } else {
                            logger.warn("Author ${user.login}/${user.firstName} ${user.lastName} has no email set.")
                        }
                    } else {
                        logger.warn("Article ${article.title} has no author (createdBy) set.")
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("Sending new comment notification for comment ${comment.id} finished with error ${ex.message}", ex)
        }
    }

    companion object {
        /**
         * If comment contains any of these words, it needs approval.
         */
        private val DEFAULT_SUSPICIOUS_WORDS = listOf(
            "viagra",
            "levitra",
            "www.",
            "https:",
            "http:",
            // Some letters specific for foreign alphabets
            "б", "в", "г", "д", "ж", "з", "и", "й", "к", "л", "м", "н", "п", "т", "ф", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я"
        )
    }
}
