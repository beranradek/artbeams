package org.xbery.artbeams.users.service

import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.users.domain.EditedUser
import org.xbery.artbeams.users.domain.MyProfile
import org.xbery.artbeams.users.password.domain.PasswordSetupData
import org.xbery.artbeams.users.domain.User
import java.time.Instant

/**
 * Operations with user.
 * @author Radek Beran
 */
interface UserService {
    fun saveUser(edited: EditedUser, ctx: OperationCtx): User?
    fun saveMyProfile(profile: MyProfile, ctx: OperationCtx): User?
    fun setPassword(passwordSetupData: PasswordSetupData, ctx: OperationCtx): User?
    fun findCurrentUserLogin(): String?
    fun findByLogin(login: String): User?
    fun findByEmail(email: String): User?
    fun findById(userId: String): User?
    fun updateUser(user: User): User

    /**
     * Confirms user's consent with personal data processing and sending of newsletters.
     * @param email email of user
     * @return time of consent confirmation if consent was successfully confirmed and stored
     */
    fun confirmConsent(email: String): Instant?
}
