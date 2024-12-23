package org.xbery.artbeams.common.emailvalidator.entity

import org.xbery.artbeams.common.emailvalidator.DNSLookup
import org.xbery.artbeams.common.emailvalidator.enums.EmailValidationError
import org.xbery.artbeams.common.emailvalidator.enums.EmailValidationWarning

/**
 * http://stackoverflow.com/questions/2049502/what-characters-are-allowed-in-email-address
 * https://cs.wikipedia.org/wiki/Internetov%C3%A1_dom%C3%A9na
 * https://cs.wikipedia.org/wiki/Dom%C3%A9na_nejvy%C5%A1%C5%A1%C3%ADho_%C5%99%C3%A1du
 *
 * The local-part of the email address may use any of these ASCII characters RFC 5322
 * · Uppercase and lowercase English letters (a–z, A–Z) (ASCII: 65-90, 97-122)
 * · Digits 0 to 9 (ASCII: 48-57)
 * · Characters !#$%&'*+-/=?^_`{|}~ (ASCII: 33, 35-39, 42, 43, 45, 47, 61, 63, 94-96, 123-126)
 * · Character . (dot, period, full stop) (ASCII: 46) provided that it is not the first or last character, and provided also that it does not appear two or more times consecutively (e.g. John..Doe@example.com is not allowed.).
 * · Special characters are allowed with restrictions. They are:
 * o Space and "(),:;<>@[] (ASCII: 32, 34, 40, 41, 44, 58, 59, 60, 62, 64, 91-93)
 * The restrictions for special characters are that they must only be used when contained between quotation marks, and that 3 of them (The space, backslash \ and quotation mark " (ASCII: 32, 92, 34)) must also be preceded by a backslash \ (e.g. "\ \\"").
 *
 * @author DDv, TPa
 */
class Email(
    /**
     * @return whole email
     */
    //TODO zakazane znaky v localPart pro seznam, google atd. https://registrace.seznam.cz/
    val email: String
) {
    var domain: String? = null
    var domains: MutableList<String> = mutableListOf()

    /**
     * @return part of email before @
     */
    var localPart: String? = null
    var error: EmailValidationError? = null
    var warnings: MutableList<EmailValidationWarning> = mutableListOf()
    var errors: MutableList<EmailValidationError> = mutableListOf()
    var isParsed: Boolean = false
    var suggestion: String? = null
    var isDomainInValidMailServersMap: Boolean = false
    var mxServers: MutableList<String> = mutableListOf()

    fun hasMXRecord(): Boolean {
        if (domain == null) {
            return false
        }
        if (this.mxServers.isEmpty()) {
            this.mxServers = DNSLookup.getMXServers(domain).toMutableList()
        }
        return mxServers.isNotEmpty()
    }

    val mXRecord: List<String>?
        /**
         * @return MX record for domain
         */
        get() {
            if (domain == null) {
                return null
            }
            if (this.mxServers.isEmpty()) {
                this.mxServers = DNSLookup.getMXServers(domain).toMutableList()
            }
            return this.mxServers
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        return if (other is Email) {
            email == other.email
        } else {
            other is String && this.email == other
        }
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}