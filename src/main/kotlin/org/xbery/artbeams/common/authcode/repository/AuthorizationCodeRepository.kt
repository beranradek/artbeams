package org.xbery.artbeams.common.authcode.repository

import org.xbery.artbeams.common.authcode.domain.AuthorizationCode

/**
 * Storage for authorization codes.
 *
 * @author Radek Beran
 */
interface AuthorizationCodeRepository {

    fun createCode(code: AuthorizationCode)

    fun updateCode(code: AuthorizationCode)

    /**
     * Deletes all authorization codes of given user.
     */
    fun deleteByUserId(userId: String): Int

    fun findByCodePurposeAndUserId(code: String, purpose: String, userId: String): AuthorizationCode?
}
