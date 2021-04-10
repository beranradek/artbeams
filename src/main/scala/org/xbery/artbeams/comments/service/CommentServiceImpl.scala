package org.xbery.artbeams.comments.service

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.{CacheEvict, Cacheable}
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.comments.domain.{Comment, EditedComment}
import org.xbery.artbeams.comments.repository.CommentRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.common.mailer.service.Mailer
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.users.repository.UserRepository

import javax.inject.Inject

/**
  * @author Radek Beran
  */
@Service
class CommentServiceImpl @Inject()(commentRepository: CommentRepository, articleRepository: ArticleRepository, userRepository: UserRepository, mailer: Mailer) extends CommentService {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)
  private lazy val normalizationHelper = new NormalizationHelper()

  @Cacheable(Array(Comment.CacheName))
  override def findByEntityId(entityId: String): Seq[Comment] = {
    logger.trace(s"Finding comments by entity id $entityId")
    commentRepository.findByEntityId(entityId)
  }

  @CacheEvict(value = Array(Comment.CacheName), allEntries = true)
  override def saveComment(edited: EditedComment, ipAddress: String, userAgent: String)(implicit ctx: OperationCtx): Either[Exception, Option[Comment]] = {
    try {
      val userId = ctx.loggedUser.map(_.id).getOrElse(AssetAttributes.EmptyId)
      val updatedCommentOpt = if (edited.id == AssetAttributes.EmptyId) {
        var comment = Comment.Empty.updatedWith(edited, userId).copy(ip = ipAddress, userAgent = userAgent)
        comment = commentRepository.create(comment)
        sendNewCommentNotification(comment)
        Option(comment)
      } else {
        commentRepository.findByIdAsOpt(edited.id) flatMap { entity =>
          commentRepository.updateEntity(entity.updatedWith(edited, userId))
        }
      }
      Right(updatedCommentOpt)
    } catch {
      case ex: Exception =>
        logger.error(s"Update of comment ${edited.id} finished with error ${ex.getMessage()}", ex)
        Left(ex)
    }
  }

  private def sendNewCommentNotification(comment: Comment): Unit = {
    if (comment.entityKey.entityType == classOf[Article].getSimpleName()) {
      val articleId = comment.entityKey.entityId
      val articleOpt = articleRepository.findById(articleId)
      if (articleOpt.isPresent()) {
        val article = articleOpt.get()
        val userOpt = userRepository.findById(article.createdBy)
        if (userOpt.isPresent()) {
          val user = userOpt.get()
          if (user.email != null && !user.email.isEmpty()) {
            val subject = normalizationHelper.removeDiacriticalMarks(s"New comment for ${article.title}")
            val body = normalizationHelper.removeDiacriticalMarks(s"${comment.userName}/${comment.email}:\n\n${comment.comment}")
            mailer.sendMail(subject, body, user.email)
          } else {
            logger.warn(s"Author ${user.login}/${user.firstName} ${user.lastName} has no email set.")
          }
        } else {
          logger.warn(s"Article ${article.title} has no author (createdBy) set.")
        }
      }
    }
  }
}
