package org.xbery.artbeams.users.service

import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.activitylog.repository.UserActivityLogRepository
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.authcode.repository.AuthorizationCodeRepository
import org.xbery.artbeams.consents.repository.ConsentRepository
import org.xbery.artbeams.mailing.api.MailingApi
import org.xbery.artbeams.news.repository.NewsSubscriptionRepository
import org.xbery.artbeams.refunds.repository.RefundRequestRepository
import org.xbery.artbeams.systemevents.repository.SystemEventLogRepository

class AccountDataEraserTest :
    StringSpec({
        val commentService = mockk<CommentService>(relaxed = true)
        val newsSubscriptionRepository = mockk<NewsSubscriptionRepository>(relaxed = true)
        val consentRepository = mockk<ConsentRepository>(relaxed = true)
        val userActivityLogRepository = mockk<UserActivityLogRepository>(relaxed = true)
        val systemEventLogRepository = mockk<SystemEventLogRepository>(relaxed = true)
        val authorizationCodeRepository = mockk<AuthorizationCodeRepository>(relaxed = true)
        val refundRequestRepository = mockk<RefundRequestRepository>(relaxed = true)
        val mailingApi = mockk<MailingApi>(relaxed = true)
        val eraser = AccountDataEraser(
            commentService,
            newsSubscriptionRepository,
            consentRepository,
            userActivityLogRepository,
            systemEventLogRepository,
            authorizationCodeRepository,
            refundRequestRepository,
            mailingApi
        )

        "erases personal data from all places in the database" {
            eraser.eraseFromDatabase("user-1", "Jane@Example.com", "jane@example.com", "deleted_user-1")

            verify { commentService.anonymizeAuthor("user-1", "jane@example.com") }
            verify(exactly = 1) { newsSubscriptionRepository.deleteByEmail("jane@example.com") }
            verify(exactly = 1) { consentRepository.replaceLogin("jane@example.com", "deleted_user-1") }
            verify { userActivityLogRepository.clearClientData("user-1") }
            verify { systemEventLogRepository.clearClientData("user-1") }
            verify { authorizationCodeRepository.deleteByUserId("user-1") }
            verify { refundRequestRepository.clearReasonsForUser("user-1") }
        }

        "handles different login and email and missing email" {
            eraser.eraseFromDatabase("user-2", "jane", null, "deleted_user-2")

            verify { commentService.anonymizeAuthor("user-2", "jane") }
            verify { newsSubscriptionRepository.deleteByEmail("jane") }
            verify { consentRepository.replaceLogin("jane", "deleted_user-2") }
        }

        "mailing service failure does not throw" {
            every { mailingApi.deleteSubscriber(any()) } returns false

            eraser.eraseFromMailingService("user-3", "jane@example.com", null)

            verify { mailingApi.deleteSubscriber("jane@example.com") }
        }
    })
