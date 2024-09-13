package org.xbery.artbeams.users.password.recovery.service

import org.xbery.artbeams.users.password.recovery.model.PasswordRecoveryMailData

/**
 * Sends password recovery mail with token bearing user id.
 * @author Radek Beran
 */
interface PasswordRecoveryMailer {
    fun sendPasswordRecoveryMail(data: PasswordRecoveryMailData)
}
