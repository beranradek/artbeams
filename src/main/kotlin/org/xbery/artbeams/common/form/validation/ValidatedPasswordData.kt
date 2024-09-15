package org.xbery.artbeams.common.form.validation

/**
 * @author Radek Beran
 */
interface ValidatedPasswordData {
    /** User login. */
    val login: String
    /** Password. */
    val password: String
    /** Repeat of the password. */
    val password2: String
}
