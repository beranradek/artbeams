package org.xbery.artbeams.users.service

import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.users.domain.EditedUser
import org.xbery.artbeams.users.domain.MyProfile
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.password.domain.PasswordSetupData

/**
 * Operations with user.
 * @author Radek Beran
 */
interface UserService {
    fun saveUser(edited: EditedUser, ctx: OperationCtx): User
    fun saveMyProfile(profile: MyProfile, ctx: OperationCtx): User?
    fun setPassword(passwordSetupData: PasswordSetupData, ctx: OperationCtx): User
    fun findCurrentUserLogin(): String?
    fun requireByLogin(login: String): User
    fun findByLogin(login: String): User?
    fun findById(userId: String): User?
    fun updateUser(user: User): User

    /**
     * Confirms user's consent with personal data processing and sending of newsletters.
     * Creates a NEWS consent for the user.
     * @param userId user ID
     * @param originProductId optional product ID if consent was created by product subscription/download
     */
    fun confirmConsent(userId: String, originProductId: String? = null)

    /**
     * Updates user's consent with personal data processing and sending of newsletters.
     * @param userId user ID
     * @param hasConsent true = give consent, false = revoke consent
     * @param originProductId optional product ID if consent was created by product subscription/download
     */
    fun updateUserConsent(userId: String, hasConsent: Boolean, originProductId: String? = null)

    /**
     * Deletes (anonymizes) user account for GDPR compliance.
     * - Anonymizes personal data (firstName, lastName, email, password)
     * - Keeps user record for referential integrity (orders, comments remain linked)
     * - Prevents future login
     * - Logs the deletion action
     * @param userId user ID to delete
     * @param ctx operation context
     * @return true if deletion was successful
     */
    fun deleteAccount(userId: String, ctx: OperationCtx): Boolean
}
