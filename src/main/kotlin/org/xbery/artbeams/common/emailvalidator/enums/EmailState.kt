package org.xbery.artbeams.common.emailvalidator.enums

enum class EmailState {
    /**
     * Email address with a restricted handle and/or domain which is a likely suspect for being a fake or undesirable entry.
     */
    BOGUS,

    /**
     * Email address is associated with a domain with inactive mail servers.
     */
    BAD_DOMAIN,

    /**
     * Email address failed FreshAddress’s deliverability check. Our proprietary algorithm utilizes our knowledgebase of confirmed undeliverable email addresses and email specific activity to determine if the email address is valid and available to accept email.
     */
    INVALID_EMAIL_ACCOUNT,

    /**
     * Email address matches an entry on client provided suppression.
     */
    CLIENT_RESTRICTED,

    /**
     * Email address originates from a website that provides temporary email addresses.
     */
    DISPOSABLE,

    /**
     * Email addresses belonging to DMA’s "Do Not Email List" (Electronic Mail Preference Service). Those who have registered have requested to be removed from national marketing lists.
     */
    EMPS_SUPPRESSION,

    /**
     * Email address owner is known to submit spam/abuse complaints.
     */
    FREQUENT_COMPLAINER,

    /**
     * Email address failed a domain specific rule, e.g. gmail.com does not allow underscores in the address handle.
     */
    DOMAIN_SPECIFIC_SYNTAX_ERROR,

    /**
     * Email address contains derogatory words.
     */
    LANGUAGE,

    /**
     * Email address is considered as problematic – includes spamtraps and potentially toxic addresses.
     */
    PROBLEMATIC,

    /**
     * Email addresses such as sales@, info@ and webmaster@.
     */
    ROLE_ACCOUNT,

    /**
     * Email address originates from an FCC wireless domain.
     */
    FFC_WIRELESS,
    TYPO
}
