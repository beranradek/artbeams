package org.xbery.artbeams.members.service

/**
 * Sends member section login information mail with confirmation of payment.
 * @author Radek Beran
 * @author AI
 */
interface MemberSectionMailer {
    /**
     * Sends email with member section login information after payment.
     * 
     * @param username user login/email to send mail to
     */
    fun sendMemberSectionLoginMail(username: String)
}
