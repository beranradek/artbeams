package org.xbery.artbeams.common.emailvalidator
import org.xbery.artbeams.common.emailvalidator.lists.*
import java.util.*

/**
 * Created by TPa on 10.07.18.
 */
class EmailValidatorBuilder {
    private var smtpPort = 25
    private var smtpSllPort = 465
    private var checkDns = false

    private var domainTypingErrors: Map<String, String> = Suggestions.DOMAIN_TYPING_ERRORS
    private var gmailSuggestion: Set<String> = Suggestions.GMAIL_SUGGESTIONS
    private var ignoredSuggestions: Set<String> = Suggestions.IGNORED
    private var disposable: List<String> = Disposable.disposableList
    private var domains: Set<String> = TopLevelDomain.domains
    private var validServersList: Set<String> = ValidServers.validServersList
    var bogusList: List<String> = Bogus.bogusList
    private var bundle: ResourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag("en-US"))

    fun build(): EmailValidator {
        return EmailValidator(
            smtpPort,
            smtpSllPort,
            checkDns,
            domainTypingErrors,
            gmailSuggestion,
            ignoredSuggestions,
            disposable,
            domains,
            validServersList,
            bogusList,
            bundle
        )
    }

    fun setSmtpPort(smtpPort: Int): EmailValidatorBuilder {
        this.smtpPort = smtpPort
        return this
    }

    fun setSmtpSllPort(smtpSllPort: Int): EmailValidatorBuilder {
        this.smtpSllPort = smtpSllPort
        return this
    }

    fun setCheckDns(checkDns: Boolean): EmailValidatorBuilder {
        this.checkDns = checkDns
        return this
    }

    fun setDomainTypingErrors(domainTypingErrors: Map<String, String>): EmailValidatorBuilder {
        this.domainTypingErrors = domainTypingErrors
        return this
    }

    fun setGmailSuggestion(gmailSuggestion: Set<String>): EmailValidatorBuilder {
        this.gmailSuggestion = gmailSuggestion
        return this
    }

    fun setIgnoredSuggestions(ignoredSuggestions: Set<String>): EmailValidatorBuilder {
        this.ignoredSuggestions = ignoredSuggestions
        return this
    }

    fun setDisposable(disposable: List<String>): EmailValidatorBuilder {
        this.disposable = disposable
        return this
    }

    fun setDomains(domains: Set<String>): EmailValidatorBuilder {
        this.domains = domains
        return this
    }

    fun setValidServersList(validServersList: Set<String>): EmailValidatorBuilder {
        this.validServersList = validServersList
        return this
    }

    fun setBundle(bundle: ResourceBundle): EmailValidatorBuilder {
        this.bundle = bundle
        return this
    }
}
