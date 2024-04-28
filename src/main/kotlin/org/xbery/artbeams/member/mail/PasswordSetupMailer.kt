package org.xbery.artbeams.member.mail

/**
 * Sends member section invitation mail with password setup and login.
 * @author Radek Beran
 */
interface PasswordSetupMailer {
    /**
     * Returns user object if user should be logged.
     */
    fun sendPasswordSetupMail(username: String)
}
