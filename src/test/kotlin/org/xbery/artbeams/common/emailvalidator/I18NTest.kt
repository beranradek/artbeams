package org.xbery.artbeams.common.emailvalidator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.xbery.artbeams.common.emailvalidator.I18N.getTranslation
import java.util.*

/**
 * Created by TPa on 10.07.18.
 */
class I18NTest {
    @Test
    fun basic() {
        Assertions.assertEquals(
            "Email musí obsahovat zavináč",
            getTranslation("EmailValidationError.MISSING_AT", Locale.forLanguageTag("cs-CZ"))
        )
        Assertions.assertEquals(
            "Email musí obsahovať zavináč",
            getTranslation("EmailValidationError.MISSING_AT", Locale.forLanguageTag("sk-SK"))
        )
        Assertions.assertEquals(
            "Email musi zawierać znak",
            getTranslation("EmailValidationError.MISSING_AT", Locale.forLanguageTag("pl-PL"))
        )
        Assertions.assertEquals(
            "Email must contain @",
            getTranslation("EmailValidationError.MISSING_AT", Locale.forLanguageTag("en-US"))
        )
    }

    @Test
    fun pamametrized() {
        Assertions.assertEquals(
            "Nemysleli jste {0}?",
            getTranslation("EmailValidationWarning.TYPO", Locale.forLanguageTag("cs-CZ"))
        )
        Assertions.assertEquals(
            "Nemysleli jste test@gmail.com?",
            getTranslation("EmailValidationWarning.TYPO", Locale.forLanguageTag("cs-CZ"), "test@gmail.com")
        )
    }

    @Test
    fun customBundle() {
        val bundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag("cs-CZ"))
        Assertions.assertEquals("Email musí obsahovat zavináč", getTranslation("EmailValidationError.MISSING_AT", bundle))
    }

    @Test
    fun defaultValue() {
        Assertions.assertEquals("DEFAULT_KEY", getTranslation("DEFAULT_KEY", Locale.forLanguageTag("cs-CZ")))
    }
}
