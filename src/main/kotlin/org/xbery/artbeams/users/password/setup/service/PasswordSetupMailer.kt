package org.xbery.artbeams.users.password.setup.service

/**
 * Sends member section invitation mail with password setup and login.
 * @author Radek Beran
 */
interface PasswordSetupMailer {
    fun sendPasswordSetupMail(username: String)
}
