package org.xbery.artbeams.common.emailvalidator.enums

/**
 * @author DDv, TPa
 */
enum class EmailValidationError {
    /**
     * Email address is missing the "@" sign. For example: "nemohotmail.com"
     */
    MISSING_AT,

    /**
     * Email address is missing the handle portion of the address. For example: "@hotmail.com"
     */
    MISSING_USERNAME,

    /**
     * Email address is missing the domain portion of the address. For example: "nemo@".
     */
    MISSING_DOMAIN,

    /**
     * Email address has multiple "@" signs. For example: "nemo@@hotmail.com".
     */
    MULTIPLE_AT,

    /**
     * Email address has prohibited characters such as a bracket "["or other unusual characters
     */
    BAD_CHARACTER,

    /**
     * Email address has multiple ‘.com’s at the end of the address. For example: "nemo@aol.com.com"
     */
    DOUBLE_TLD,

    /**
     * Email address is missing the top domain, or the top level domain is invalid.
     */
    BAD_TLD,

    /**
     * Email address has less than 6 characters or over 100 characters.
     */
    BAD_LENGTH,

    /**
     * Email address does not meet RFC standards and/or has multiple errors.
     */
    CONTAINS_MULTIPLE_TYPOS,

    /**
     * Email address ends with a period – For example: "nemo@hotmail.com."
     */
    ENDS_WITH_PERIOD,

    /**
     * Email address ends with a hyphen – For example: "nemo@hotmail.com-"
     */
    ENDS_WITH_HYPHEN,

    /**
     * Email address has multiple periods in the domain. For example: "nemo@hotmail..com"
     */
    DOUBLE_PERIOD_IN_DOMAIN,

    /**
     * Email address has multiple periods in the local part. For example: "ne..mo@hotmail.com"
     */
    DOUBLE_PERIOD_IN_LOCAL_PART,

    /**
     * Email address has a period after the '@' sign. For example: "nemo@.hotmail.com"
     */
    PERIOD_FOLLOWING_AT,

    /**
     * Email address has a hyphen after the '@' sign. For example: "nemo@-hotmail.com"
     */
    HYPHEN_FOLLOWING_AT,

    /**
     * Email address starts with a period. For example: ".nemo@hotmail.com"
     */
    STARTS_WITH_A_PERIOD
}
