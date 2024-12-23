package org.xbery.artbeams.common.emailvalidator

import org.xbery.artbeams.common.emailvalidator.DNSLookup.getIPAddresses
import org.xbery.artbeams.common.emailvalidator.I18N.getTranslation
import org.xbery.artbeams.common.emailvalidator.CharUtils.isAsciiDigit
import org.xbery.artbeams.common.emailvalidator.CharUtils.isAt
import org.xbery.artbeams.common.emailvalidator.CharUtils.isBackSlash
import org.xbery.artbeams.common.emailvalidator.CharUtils.isDot
import org.xbery.artbeams.common.emailvalidator.CharUtils.isDoubleQuote
import org.xbery.artbeams.common.emailvalidator.CharUtils.isHyphen
import org.xbery.artbeams.common.emailvalidator.CharUtils.isNameQuotedSpecialCharacter
import org.xbery.artbeams.common.emailvalidator.CharUtils.isNameSpecialCharacter
import org.xbery.artbeams.common.emailvalidator.CharUtils.isNumber
import org.xbery.artbeams.common.emailvalidator.CharUtils.isSpace
import org.xbery.artbeams.common.emailvalidator.CharUtils.levenshteinDistance
import org.xbery.artbeams.common.emailvalidator.entity.Email
import org.xbery.artbeams.common.emailvalidator.entity.EmailValidationMessage
import org.xbery.artbeams.common.emailvalidator.entity.EmailValidationResult
import org.xbery.artbeams.common.emailvalidator.enums.EmailPart
import org.xbery.artbeams.common.emailvalidator.enums.EmailValidationError
import org.xbery.artbeams.common.emailvalidator.enums.EmailValidationWarning
import org.xbery.artbeams.common.emailvalidator.enums.MessageSeverity
import org.xbery.artbeams.common.emailvalidator.lists.RoleAccounts
import java.util.*

class EmailValidator {
    private val smtpPort: Int
    private val smtpSllPort: Int
    private val checkDns: Boolean
    private val domainTypingErrors: Map<String, String>
    private val gmailSuggestion: Set<String>
    private val ignoredSuggestions: Set<String>
    private val disposable: List<String>
    private val domains: Set<String>
    private val validServersList: Set<String>
    private val bogusList: List<String>
    private val messageBundle: ResourceBundle?

    @Suppress("unused")
    private constructor() {
        this.smtpPort = 0
        this.smtpSllPort = 0
        this.checkDns = false
        this.domainTypingErrors = HashMap()
        this.gmailSuggestion = HashSet()
        this.ignoredSuggestions = HashSet()
        this.disposable = ArrayList()
        this.domains = HashSet()
        this.validServersList = HashSet()
        this.bogusList = ArrayList()
        this.messageBundle = null
    }

    constructor(
        smtpPort: Int,
        smtpSllPort: Int,
        checkDns: Boolean,
        domainTypingErrors: Map<String, String>,
        gmailSuggestion: Set<String>,
        ignoredSuggestions: Set<String>,
        disposable: List<String>,
        domains: Set<String>,
        validServersList: Set<String>,
        bogusList: List<String>,
        messageBundle: ResourceBundle?
    ) {
        this.smtpPort = smtpPort
        this.smtpSllPort = smtpSllPort
        this.checkDns = checkDns
        this.domainTypingErrors = domainTypingErrors
        this.gmailSuggestion = gmailSuggestion
        this.ignoredSuggestions = ignoredSuggestions
        this.disposable = disposable
        this.domains = domains
        this.validServersList = validServersList
        this.bogusList = bogusList
        this.messageBundle = messageBundle
    }

    fun validate(email: String): EmailValidationResult {
        val e = Email(email)
        val isValid = isValid(e)
        return EmailValidationResult(isValid, constructMessages(e), e)
    }

    private fun constructMessages(email: Email): List<EmailValidationMessage> {
        val messages: MutableList<EmailValidationMessage> = ArrayList<EmailValidationMessage>()
        email.errors.forEach { e ->
            messages.add(
                EmailValidationMessage(
                    MessageSeverity.ERROR, getTranslation(
                        EmailValidationError::class.simpleName + "." + e.name,
                        messageBundle!!
                    )
                )
            )
        }
        for (w in email.warnings) {
            when (w) {
                EmailValidationWarning.TYPO -> {
                    messages.add(
                        EmailValidationMessage(
                            MessageSeverity.WARNING, getTranslation(
                                EmailValidationWarning::class.simpleName + "." + w.name,
                                messageBundle!!, email.suggestion
                            )
                        )
                    )
                }

                else -> {
                    messages.add(
                        EmailValidationMessage(
                            MessageSeverity.WARNING, getTranslation(
                                EmailValidationWarning::class.simpleName + "." + w.name,
                                messageBundle!!
                            )
                        )
                    )
                }
            }
        }
        return messages
    }

    private fun isValidDomain(email: Email): Boolean {
        if (!email.isParsed) parse(email)
        val domain = email.domain
        if (domain == null) {
            return false
        }
        val ips = getIPAddresses(domain)
        return ips.isNotEmpty()
    }

    /**
     * Method tries to correct typos in email
     *
     * @return fixed email
     */
    private fun createSuggestion(email: Email): String? {
        var localPartSuggestion: String? = email.localPart
        var domainSuggestion: String = email.domain ?: ""

        if (email.errors.isNotEmpty()) { //jsou chyby, pokusime se opravit
            for (e in email.errors) {
                when (e) {
                    EmailValidationError.DOUBLE_PERIOD_IN_LOCAL_PART -> {
                        if (localPartSuggestion != null) {
                            localPartSuggestion = localPartSuggestion.replace("\\.{2,}".toRegex(), ".")
                        }
                    }

                    EmailValidationError.STARTS_WITH_A_PERIOD -> {
                        if (localPartSuggestion != null) {
                            localPartSuggestion = localPartSuggestion.replaceFirst("^\\.+".toRegex(), "")
                        }
                    }

                    EmailValidationError.BAD_CHARACTER -> {}
                    EmailValidationError.DOUBLE_PERIOD_IN_DOMAIN -> {
                        if (domainSuggestion.isNotEmpty()) {
                            domainSuggestion = domainSuggestion.replace("\\.{2,}".toRegex(), ".")
                        }
                    }

                    EmailValidationError.ENDS_WITH_HYPHEN -> {
                        if (domainSuggestion.isNotEmpty()) {
                            domainSuggestion = domainSuggestion.replaceFirst("-+$".toRegex(), "")
                        }
                    }

                    EmailValidationError.ENDS_WITH_PERIOD -> {
                        if (domainSuggestion.isNotEmpty()) {
                            domainSuggestion = domainSuggestion.replaceFirst("\\.+$".toRegex(), "")
                        }
                    }

                    EmailValidationError.HYPHEN_FOLLOWING_AT -> {
                        if (domainSuggestion.isNotEmpty()) {
                            domainSuggestion = domainSuggestion.replaceFirst("^-+".toRegex(), "")
                        }
                    }

                    EmailValidationError.PERIOD_FOLLOWING_AT -> {
                        if (domainSuggestion.isNotEmpty()) {
                            domainSuggestion = domainSuggestion.replaceFirst("^\\.+".toRegex(), "")
                        }
                    }

                    else -> {}
                }
            }
        }
        val domain = email.domain ?: ""
        domainSuggestion =
            (if (domainSuggestion != domain) {
                //upravili jsme domenu, zvalidujeme
                getDomainSuggestion(domainSuggestion)
            } else {
                getDomainSuggestion(domain)
            }) ?: ""

        if (domainSuggestion.isNotEmpty() && domainSuggestion != email.domain) {
            val result = "$localPartSuggestion@$domainSuggestion"
            if (result != email.email) {
                email.suggestion = result
            }
            return result
        }

        return null
    }

    private fun getDomainSuggestion(domain: String?): String? {
        if (domain.isNullOrEmpty()) {
            return null
        }

        if (ignoredSuggestions.contains(domain)) {
            return null
        }

        if (domainTypingErrors.containsKey(domain)) {
            return domainTypingErrors[domain]
        }

        if (gmailSuggestion.contains(domain)) {
            return "gmail.com"
        }

        var map: MutableMap<String?, Int> = HashMap()
        for (validDomain in validServersList) {
            map[validDomain] = levenshteinDistance(validDomain, domain)
        }

        if (map.isEmpty()) {
            return null
        }

        map = sortByValue(map)

        val entry: Map.Entry<String?, Int> = map.entries.iterator().next()
        return if (entry.value < 4) {
            entry.key
        } else {
            null
        }
    }

    /**
     * @return true pokud je domena emailu v seznamu overenych emailu
     */
    private fun isDomainInValidMailServersMap(email: Email): Boolean {
        if (!email.isParsed) parse(email)

        val domain = email.domain
        val isValid = validServersList.contains(domain)
        email.isDomainInValidMailServersMap = isValid
        return isValid
    }

    private fun parse(email: Email) {
        email.isParsed = true
        if (email.email.isNullOrEmpty()) {
            addError(email, EmailValidationError.CONTAINS_MULTIPLE_TYPOS)
            return
        }
        var sb = StringBuilder()
        var domainStr = StringBuilder()
        email.domains = mutableListOf()
        var part: EmailPart = EmailPart.LOCAL_PART
        var lastChar = 0.toChar()
        for (ch in email.email.toCharArray()) {
            if (isAsciiDigit(ch) || isNumber(ch)) { //[a-zA-Z0-9]
                sb.append(ch)
                if (part == EmailPart.DOMAIN) {
                    domainStr.append(ch)
                }
            } else if (isAt(ch)) { //@
                if (part == EmailPart.DOMAIN) {
                    sb.append(ch)
                    addError(email, EmailValidationError.MULTIPLE_AT)
                    domainStr.append(ch)
                } else if (part == EmailPart.LOCAL_PART_IN_DOUBLE_QUOTES) { //jsme v uvozovkach
                    sb.append(ch)
                } else { //konec local part
                    email.localPart = sb.toString()
                    sb = StringBuilder()
                    part = EmailPart.DOMAIN
                }
            } else if (isDot(ch)) { //tecka
                sb.append(ch)
                if (sb.length == 1) { //ani local part ani domain nemuze zacinat teckou
                    if (part === EmailPart.DOMAIN) {
                        addError(email, EmailValidationError.PERIOD_FOLLOWING_AT)
                    } else {
                        addError(email, EmailValidationError.STARTS_WITH_A_PERIOD)
                    }
                } else if (isDot(lastChar)) { //posledni znak byla taky tecka
                    sb.append(ch)
                    //nemuzou byt dve tecky v domene ani v local partu
                    if (part === EmailPart.DOMAIN) {
                        addError(email, EmailValidationError.DOUBLE_PERIOD_IN_DOMAIN)
                    } else if (part === EmailPart.LOCAL_PART) {
                        addError(email, EmailValidationError.DOUBLE_PERIOD_IN_LOCAL_PART)
                    }
                }
                if (part === EmailPart.DOMAIN) { //v domene pridame dalsi cast domeny
                    if (domainStr.length > 0) {
                        email.domains.add(domainStr.toString().lowercase(Locale.getDefault()))
                        domainStr = StringBuilder()
                    }
                }
            } else if (isHyphen(ch)) { //spojovnik/minus
                sb.append(ch)
                if (part === EmailPart.DOMAIN && sb.length == 1)  //spojovnik nemuze byt na zacatku domeny
                {
                    addError(email, EmailValidationError.HYPHEN_FOLLOWING_AT)
                }
                if (part === EmailPart.DOMAIN) {
                    domainStr.append(ch)
                }
            } else if (isDoubleQuote(ch)) { //dvojite uvozovky
                sb.append(ch)
                if (part === EmailPart.DOMAIN) { //nepovoleno v domene
                    addError(email, EmailValidationError.BAD_CHARACTER)
                    domainStr.append(ch)
                } else { //v local partu povoleno
                    /*
					 * A quoted string may exist as a dot separated entity within the local-part,
					 * or it may exist when the outermost quotes are the outermost characters
					 * of the local-part (e.g., abc."defghi".xyz@example.com or "abcdefghixyz"@example.com
					 * are allowed.[citation needed] Conversely, abc"defghi"xyz@example.com is not;
					 * neither is abc\"def\"ghi@example.com).
					 */
                    //TODO
                    if (isBackSlash(lastChar)) { //oescapovane uvozovky
                        if (part === EmailPart.LOCAL_PART) {
                            addError(email, EmailValidationError.BAD_CHARACTER)
                        }
                    } else {
                        part = if (part === EmailPart.LOCAL_PART_IN_DOUBLE_QUOTES) {
                            EmailPart.LOCAL_PART
                        } else {
                            EmailPart.LOCAL_PART_IN_DOUBLE_QUOTES
                        }
                    }
                }
            } else if (isSpace(ch)) { //mezera
                sb.append(ch)
                if (part === EmailPart.DOMAIN) {
                    domainStr.append(ch)
                }
                if (part === EmailPart.DOMAIN || part === EmailPart.LOCAL_PART)  //mezera je povolena pouze v uvozovkach
                {
                    addError(email, EmailValidationError.BAD_CHARACTER)
                }
            } else if (isNameSpecialCharacter(ch)) {
                sb.append(ch)
                if (part === EmailPart.DOMAIN) {
                    domainStr.append(ch)
                }
                if (part === EmailPart.DOMAIN) {
                    addError(email, EmailValidationError.BAD_CHARACTER)
                }
            } else if (isNameQuotedSpecialCharacter(ch)) {
                sb.append(ch)
                if (part === EmailPart.DOMAIN) {
                    domainStr.append(ch)
                }
                if (part !== EmailPart.LOCAL_PART_IN_DOUBLE_QUOTES) {
                    addError(email, EmailValidationError.BAD_CHARACTER)
                }
            } else { //jiny znak
                addError(email, EmailValidationError.BAD_CHARACTER)
                sb.append(ch)
                if (part === EmailPart.DOMAIN) {
                    domainStr.append(ch)
                }
            }

            lastChar = ch
        }

        if (part === EmailPart.DOMAIN) {
            email.domain = sb.toString().lowercase(Locale.getDefault())
            if (domainStr.isNotEmpty()) {
                email.domains.add(domainStr.toString().lowercase(Locale.getDefault()))
            }
        } else {
            email.localPart = sb.toString()
        }

        if (email.domain == null) {
            addError(email, EmailValidationError.MISSING_AT)
        }

        if (email.localPart.isNullOrEmpty()) {
            addError(email, EmailValidationError.MISSING_USERNAME)
        }

        //kontrola na posledni znak
        val domain = email.domain
        if (domain != null) {
            if (domain.isEmpty()) {
                addError(email, EmailValidationError.MISSING_DOMAIN)
            }
            if (domain.endsWith(".")) {
                addError(email, EmailValidationError.ENDS_WITH_PERIOD)
            }
            if (domain.endsWith("-")) {
                addError(email, EmailValidationError.ENDS_WITH_HYPHEN)
            }
        }
        val localPart = email.localPart
        if (localPart != null) {
            if (localPart.endsWith(".")) {
                addError(email, EmailValidationError.BAD_CHARACTER)
            }
        }

        if (email.error == null) {
            // kontrola na delky casti
            if (localPart != null && localPart.length > MAX_LOCAL_PART_LENGTH) {
                addError(email, EmailValidationError.BAD_LENGTH)
            }
            if ((domain?.length ?: 0) > MAX_DOMAIN_LENGTH) {
                addError(email, EmailValidationError.BAD_LENGTH)
            }
            if ((domain?.length ?: 0) + (localPart?.length ?: 0) > MAX_LENGTH) {
                addError(email, EmailValidationError.BAD_LENGTH)
            }
        }

        if (email.error == null) {
            if (email.domains.isNotEmpty()) {
                //kontrola na delky domen
                for (domainPart in email.domains) {
                    if (domainPart.length > 63) {
                        addError(email, EmailValidationError.BAD_LENGTH)
                    }
                }

                if (email.domains.size == 1) {
                    addError(email, EmailValidationError.BAD_TLD)
                }

                //kontrola na TLD
                if (email.error == null) {
                    val tldName: String = email.domains[email.domains.size - 1]
                    val tld = domainExists(tldName)
                    if (!tld) {
//						LOG.error("unable to find TLD for: " + tldName + " in email " + this.email);
                        addError(email, EmailValidationError.BAD_TLD)
                    }
                }
            }
        }
        email.suggestion = createSuggestion(email)
    }

    private fun domainExists(domain: String): Boolean {
        Objects.requireNonNull(domain)
        return domains.contains(domain.lowercase(Locale.getDefault()))
    }

    /**
     * @return true if the emeil is valid according to RFC 5322 and also check IP adress and MX record
     */
    private fun isValid(email: Email): Boolean {
        if (!email.isParsed) parse(email)
        if (email.error != null) {
            return false
        }

        if (email.domain == null) {
            return false
        }

        if (email.suggestion != null) {
            email.warnings.add(EmailValidationWarning.TYPO)
        }

        if (isDomainInValidMailServersMap(email)) { //domena je v seznamu jiz uspesne dorucenych mail serveru
            return true
        }

        if (checkDns) {
            if (!isValidDomain(email)) { //je domena platna(existuje)???
                email.warnings.add(EmailValidationWarning.BAD_DOMAIN)
                return false
            }
            if (!email.hasMXRecord()) {
                email.warnings.add(EmailValidationWarning.BAD_DOMAIN)
                return false
            }
        }

        val domain = email.domain
        if (disposable.contains(domain)) {
            email.warnings.add(EmailValidationWarning.DISPOSABLE)
        }
        val isBogus = domain != null && bogusList.stream().anyMatch { domain.contains(it) }
        if (isBogus) {
            email.warnings.add(EmailValidationWarning.BOGUS)
        }

        return true
    }

    companion object {
        //The format of email addresses is local-part@domain where the local part may be up to 64 characters long and the domain
        // may have a maximum of 255 characters[2]—but the maximum of 256-character length of a forward or reverse path restricts
        // the entire email address to be no more than 254 characters long.
        private const val MAX_LOCAL_PART_LENGTH = 64
        private const val MAX_DOMAIN_LENGTH = 255
        private const val MAX_LENGTH = 254

        // TODO validate role accounts
        private val PROHIBITED_ROLE_ACCOUNT_LOCAL_PARTS: MutableList<String> =
            ArrayList<String>(RoleAccounts.ROLE_ACCOUNT_LOCAL_PARTS)

        init {
            PROHIBITED_ROLE_ACCOUNT_LOCAL_PARTS.removeAll(RoleAccounts.ALLOWED_ROLE_ACCOUNT_LOCAL_PARTS)
        }

        private fun <K, V : Comparable<V>?> sortByValue(map: Map<K, V>): MutableMap<K, V> {
            val list: List<Map.Entry<K, V>> = LinkedList(map.entries)
            list.sortedWith(Comparator.comparing { e -> e.value })

            val result: MutableMap<K, V> = LinkedHashMap()
            for ((key, value) in list) {
                result[key] = value
            }
            return result
        }

        private fun addError(email: Email, error: EmailValidationError?) {
            if (error == null) {
                return
            }

            email.errors.add(error)
            if (error == email.error) { //stejne chyby nebudeme prevadet na EmailValidationError.CONTAINS_MULTIPLE_TYPOS
                return
            }

            if (email.error != null) {
                email.error = EmailValidationError.CONTAINS_MULTIPLE_TYPOS
            } else {
                email.error = error
            }
        }
    }
}
