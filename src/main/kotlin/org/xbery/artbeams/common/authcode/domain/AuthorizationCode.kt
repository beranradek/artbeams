package org.xbery.artbeams.common.authcode.domain

import kotlinx.datetime.Instant

/**
 * Code authorizing a user to perform some action.
 *
 * @author Radek Beran
 */
data class AuthorizationCode(
    /**
     * Actual value of the code. Must be unique across all users.
     */
    val code: String,
    /**
     * The use case for which this code should be used. Note that filling this
     * value is important due to security reasons. This value should be validated within a
     * specific use case.
     */
    val purpose: String,
    /**
     * ID of user for which the code has been generated.
     * In the case of need, this can be a unique identifier generated for an anonymous user as well.
     */
    val userId: String,
    /**
     * Time when the code has been generated.
     */
    val created: Instant,
    /**
     * Validity restriction of the code. The code cannot be used after this time has passed.
     */
    val validTo: Instant,
    /**
     * Time when the code has been used, `null` if the code was not used yet.
     */
    val used: Instant?
)
