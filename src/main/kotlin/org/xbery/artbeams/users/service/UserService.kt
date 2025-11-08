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
     * @param userId user ID
     * @return user updated
     */
    fun confirmConsent(userId: String): User
    
    /**
     * Updates user's consent with personal data processing and sending of newsletters.
     * @param userId user ID
     * @param consent consent timestamp (null = unsubscribed, not null = subscribed)
     * @return user updated
     */
    fun updateUserConsent(userId: String, consent: java.time.Instant?): User
}
