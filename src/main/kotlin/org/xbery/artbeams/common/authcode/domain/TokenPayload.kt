package org.xbery.artbeams.common.authcode.domain

/**
 * Payload with token and additional data to be encrypted/decrypted.
 * @author Radek Beran
 */
data class TokenPayload(val code: String, val purpose: String, val userId: String)
