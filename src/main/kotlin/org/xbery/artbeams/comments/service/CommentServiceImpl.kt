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
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.common.text.NormalizationHelper
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
    private val spamDetector: SpamDetector,
    private val appConfig: org.xbery.artbeams.config.repository.AppConfig,
    private val adminNotificationService: org.xbery.artbeams.admin.notification.AdminNotificationService
) : CommentService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val normalizationHelper: NormalizationHelper = NormalizationHelper()

    @Cacheable(Comment.CACHE_NAME)
    override fun findApprovedByEntityId(entityId: String): List<Comment> {
        logger.trace("Finding approved comments by entity id $entityId")
        return commentRepository.findApprovedByEntityId(entityId)
    }

    @CacheEvict(value = [Comment.CACHE_NAME], allEntries = true)
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
                    approvedOrWaiting(Comment.EMPTY.updatedWith(edited, userId).copy(ip = ipAddress, userAgent = userAgent))
                comment = commentRepository.create(comment)
                // Only send notification if comment is approved (not spam)
                if (comment.state == CommentState.APPROVED) {
                    sendNewCommentNotification(comment)
                }
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

    override fun findComments(pagination: Pagination): ResultPage<Comment> {
        return commentRepository.findComments(pagination)
    }

    override fun searchComments(searchTerm: String?, state: CommentState?, pagination: Pagination): ResultPage<Comment> {
        return commentRepository.searchComments(searchTerm, state, pagination)
    }

    @CacheEvict(value = [Comment.CACHE_NAME], allEntries = true)
    override fun deleteComment(id: String): Boolean {
        logger.info("Deleting comment $id")
        return commentRepository.deleteById(id)
    }

    @CacheEvict(value = [Comment.CACHE_NAME], allEntries = true)
    override fun updateCommentState(id: String, state: CommentState): Boolean {
        logger.info("Updating state of comment $id to $state")

        // Get comment before update to check if transitioning from WAITING to APPROVED
        val comment = commentRepository.findById(id)
        val updated = commentRepository.updateState(id, state)

        // Send notification if spam comment is manually approved
        if (updated && comment != null && comment.state == CommentState.WAITING_FOR_APPROVAL && state == CommentState.APPROVED) {
            sendNewCommentNotification(comment.copy(state = state))
        }

        return updated
    }

    private fun approvedOrWaiting(comment: Comment): Comment {
        if (spamDetector.isSpam(comment.comment, comment.userName, comment.email)) {
            logger.info("Comment detected as spam: userName=${comment.userName}, email=${comment.email}, comment=${comment.comment}")
            val spamComment = comment.copy(state = CommentState.WAITING_FOR_APPROVAL)

            // Send admin notification about spam detection
            try {
                adminNotificationService.sendSpamDetectedNotification(spamComment)
            } catch (e: Exception) {
                logger.error("Failed to send admin notification for spam comment", e)
            }

            return spamComment
        }
        return comment.copy(state = CommentState.APPROVED)
    }

    private fun sendNewCommentNotification(comment: Comment) {
        try {
            val articleClassName = Article::class.java.getSimpleName()
            if (comment.entityKey.entityType == articleClassName) {
                val articleId: String = comment.entityKey.entityId
                val article = articleRepository.findById(articleId)
                if (article != null) {
                    val user = userRepository.findById(article.createdBy)
                    if (user != null && user.email != null && user.email.isNotEmpty()) {
                        // Get base URL from configuration
                        val baseUrl = appConfig.findConfig("web.baseUrl") ?: "http://localhost:8080"
                        val articleUrl = "$baseUrl/${article.slug}"

                        val subject: String =
                            normalizationHelper.removeDiacriticalMarks("Novy komentar k clanku: ${article.title}")

                        // Create HTML email with article link
                        val htmlBody: String = """
                            <html>
                            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                                <h2 style="color: #2c5aa0;">Novy komentar k vasemu clanku</h2>
                                <p><strong>Clanek:</strong> ${normalizationHelper.removeDiacriticalMarks(article.title)}</p>
                                <p><strong>Autor komentare:</strong> ${normalizationHelper.removeDiacriticalMarks(comment.userName)}</p>
                                ${if (comment.email.isNotEmpty()) "<p><strong>Email:</strong> ${comment.email}</p>" else ""}

                                <div style="background-color: #f5f5f5; padding: 15px; border-left: 4px solid #2c5aa0; margin: 20px 0;">
                                    <p style="margin: 0;"><strong>Komentar:</strong></p>
                                    <p style="margin: 10px 0 0 0;">${normalizationHelper.removeDiacriticalMarks(comment.comment).replace("\n", "<br>")}</p>
                                </div>

                                <p style="margin-top: 20px;">
                                    <a href="${articleUrl}" style="background-color: #2c5aa0; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block;">
                                        Zobrazit clanek a komentar
                                    </a>
                                </p>

                                <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                                <p style="font-size: 12px; color: #666;">
                                    Tato notifikace byla odeslana, protoze jste autorem clanku.
                                </p>
                            </body>
                            </html>
                        """.trimIndent()

                        mailSender.sendMailWithHtml(user.email, subject, htmlBody)
                        logger.info("Comment notification sent to ${user.email} for article '${article.title}'")
                    } else {
                        val login = user?.login ?: "unknown"
                        val firstName = user?.firstName ?: ""
                        val lastName = user?.lastName ?: ""
                        logger.warn("Author $login/$firstName $lastName has no email set.")
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("Sending new comment notification for comment ${comment.id} finished with error ${ex.message}", ex)
        }
    }

    
}
