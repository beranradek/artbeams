package org.xbery.artbeams.users.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.activitylog.repository.UserActivityLogRepository
import org.xbery.artbeams.comments.service.CommentService
import org.xbery.artbeams.common.authcode.repository.AuthorizationCodeRepository
import org.xbery.artbeams.consents.repository.ConsentRepository
import org.xbery.artbeams.mailing.api.MailingApi
import org.xbery.artbeams.news.repository.NewsSubscriptionRepository
import org.xbery.artbeams.systemevents.repository.SystemEventLogRepository

/**
 * Removes personal data of a deleted user account from all places outside of the user record itself (GDPR).
 * Orders and order items are kept because they hold only the user id (accounting obligations).
 * History of consents is kept as a proof of consent, but the login (email) in it is pseudonymized.
 *
 * @author Radek Beran
 */
@Service
class AccountDataEraser(
    private val commentService: CommentService,
    private val newsSubscriptionRepository: NewsSubscriptionRepository,
    private val consentRepository: ConsentRepository,
    private val userActivityLogRepository: UserActivityLogRepository,
    private val systemEventLogRepository: SystemEventLogRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
    private val mailingApi: MailingApi
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Erases personal data stored in the database. Must be called in a transaction together with anonymization of the user record.
     *
     * @param pseudonym replacement of the login (email) in the kept records
     */
    fun eraseFromDatabase(userId: String, login: String, email: String?, pseudonym: String) {
        val emails = identifiers(login, email)
        commentService.anonymizeAuthor(userId, email ?: login)
        emails.forEach { newsSubscriptionRepository.deleteByEmail(it) }
        emails.forEach { consentRepository.replaceLogin(it, pseudonym) }
        userActivityLogRepository.clearClientData(userId)
        systemEventLogRepository.clearClientData(userId)
        // Pending codes (e.g. for password reset) must not be usable to revive the anonymized account
        authorizationCodeRepository.deleteByUserId(userId)
    }

    /**
     * Deletes the subscriber from the mailing service. Failure is only logged, because the account is already anonymized
     * and the operation can be repeated manually.
     */
    fun eraseFromMailingService(userId: String, login: String, email: String?) {
        val deletedAll = identifiers(login, email).map { mailingApi.deleteSubscriber(it) }
        if (deletedAll.none { it }) {
            logger.warn("No subscriber was deleted from the mailing service for user ID: $userId (not subscribed or deletion failed)")
        }
    }

    private fun identifiers(login: String, email: String?): Set<String> =
        listOfNotNull(login, email).map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()
}
