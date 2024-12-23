package org.xbery.artbeams.common.emailvalidator.enums

/**
 * @author DDv
 */
enum class EmailValidationWarning(val emailState: EmailState) {
    /**
     * Email address failed FreshAddress’s deliverability check. Our proprietary algorithm utilizes our knowledgebase of confirmed undeliverable email addresses and email specific activity to determine if the email address is valid and available to accept email.
     */
    INVALID_EMAIL_ACCOUNT(EmailState.INVALID_EMAIL_ACCOUNT),  //TODO

    /**
     * Email address is associated with a domain with inactive mail servers.
     */
    BAD_DOMAIN(EmailState.BAD_DOMAIN),  //TODO

    /**
     * Email address with a restricted handle and/or domain which is a likely suspect for being a fake or undesirable entry.
     */
    BOGUS(EmailState.BOGUS),  //TODO

    /**
     * Email address matches an entry on client provided suppression.
     */
    CLIENT_RESTRICTED(EmailState.CLIENT_RESTRICTED),  //TODO

    /**
     * Email address originates from a website that provides temporary email addresses.
     */
    DISPOSABLE(EmailState.DISPOSABLE),

    /**
     * Email addresses belonging to DMA’s "Do Not Email List" (Electronic Mail Preference Service). Those who have registered have requested to be removed from national marketing lists.
     */
    EMPS_SUPPRESSION(EmailState.EMPS_SUPPRESSION),  //TODO

    /**
     * Email address owner is known to submit spam/abuse complaints.
     */
    FREQUENT_COMPLAINER(EmailState.FREQUENT_COMPLAINER),  //TODO

    /**
     * Email address failed a domain specific rule, e.g. gmail.com does not allow underscores in the address handle.
     */
    DOMAIN_SPECIFIC_SYNTAX_ERROR(EmailState.DOMAIN_SPECIFIC_SYNTAX_ERROR),  //TODO

    /**
     * Email address contains derogatory words.
     */
    LANGUAGE(EmailState.LANGUAGE),  //TODO

    /**
     * Email address is considered as problematic – includes spamtraps and potentially toxic addresses.
     */
    PROBLEMATIC(EmailState.PROBLEMATIC),  //TODO

    /**
     * Email addresses such as sales@, info@ and webmaster@.
     */
    ROLE_ACCOUNT(EmailState.ROLE_ACCOUNT),  //TODO

    /**
     * Email address originates from an FCC wireless domain.
     */
    FFC_WIRELESS(EmailState.FFC_WIRELESS),  //TODO

    /**
     * Email might contain typo
     */
    TYPO(EmailState.TYPO)
}
