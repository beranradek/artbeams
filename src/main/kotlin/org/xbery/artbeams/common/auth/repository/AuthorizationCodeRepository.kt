package org.xbery.artbeams.common.auth.repository

import org.xbery.artbeams.common.auth.domain.AuthorizationCode

/**
 * Storage for authorization codes.
 *
 * @author Radek Beran
 */
interface AuthorizationCodeRepository {

    fun createCode(code: AuthorizationCode)

    fun updateCode(code: AuthorizationCode)

    fun findByCodePurposeAndUserId(code: String, purpose: String, userId: String): AuthorizationCode?
}
