package org.xbery.artbeams.common.emailvalidator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.xbery.artbeams.common.emailvalidator.enums.EmailValidationError
import org.xbery.artbeams.common.emailvalidator.enums.EmailValidationWarning

/**
 * Created by tomaspavel on 15.2.17.
 */
class UseTest {
    private val validator = EmailValidatorBuilder().build()

    @Test
    fun okTest() {
        var result = validator.validate("karel.javor@etnetera.cz")
        var isValid = result.isValid
        Assertions.assertTrue(isValid)
        Assertions.assertTrue(result.messages.isEmpty())
        Assertions.assertTrue(result.email.hasMXRecord())

        result = validator.validate("karel@gnail.com")
        isValid = result.isValid
        val warnings: List<EmailValidationWarning> = result.email.warnings
        Assertions.assertEquals(EmailValidationWarning.TYPO, warnings[0])
        Assertions.assertTrue(isValid)
        val sugestion = result.email.suggestion
        Assertions.assertEquals("karel@gmail.com", sugestion)
        Assertions.assertEquals("Did you mean karel@gmail.com?", result.messages[0].text)

        result = validator.validate("marian.@seznam.cz")
        isValid = result.isValid
        Assertions.assertFalse(isValid)
    }

    @Test
    fun typoTest() {
        val result = validator.validate("karel@gnail.com")
        val isValid = result.isValid
        val warnings: List<EmailValidationWarning> = result.email.warnings
        Assertions.assertEquals(EmailValidationWarning.TYPO, warnings[0])
        Assertions.assertTrue(isValid)
        val sugestion = result.email.suggestion
        Assertions.assertEquals("karel@gmail.com", sugestion)
    }

    @Test
    fun nokTest() {
        val result = validator.validate("marian.@seznam.cz")
        val isValid = result.isValid
        Assertions.assertFalse(isValid)
        Assertions.assertEquals(EmailValidationError.BAD_CHARACTER, result.email.error)
    }

    /*@Test //TODO
	void missingAtTest() {
		Email email = new Email("karelgmail.com");
		boolean isValid = email.isValid();
		assertEquals(EmailValidationError.MISSING_AT, email.getError());
		assertFalse(isValid);
		String sugestion = email.getSuggestion();
		assertEquals("karel@gmail.com", sugestion);
	}*/

    @Test
    fun emptyTest() {
        val result = validator.validate("")
        val isValid = result.isValid
        Assertions.assertFalse(isValid)
    }

    @Test
    fun builderTest() {
        val builder = EmailValidatorBuilder()
            .setSmtpPort(20)
            .setSmtpSllPort(444)
            .setCheckDns(true)
            .setDisposable(listOf())
            .setDomains(setOf())
            .setDomainTypingErrors(mapOf())
            .setGmailSuggestion(setOf())
            .setIgnoredSuggestions(setOf())
            .setValidServersList(setOf())
        builder.build()
    }

    @Test
    fun bogusTest() {
        var result = validator.validate("812990365@qq.com")
        var isValid = result.isValid
        var warnings: List<EmailValidationWarning?> = result.email.warnings
        var isBogus = warnings.contains(EmailValidationWarning.BOGUS)
        Assertions.assertTrue(isBogus)
        Assertions.assertTrue(isValid)

        result = validator.validate("812990365@vip.qq.com")
        isValid = result.isValid
        warnings = result.email.warnings
        isBogus = warnings.contains(EmailValidationWarning.BOGUS)
        Assertions.assertTrue(isBogus)
        Assertions.assertTrue(isValid)

        result = validator.validate("812990365@QQ.COM")
        isValid = result.isValid
        warnings = result.email.warnings
        isBogus = warnings.contains(EmailValidationWarning.BOGUS)
        Assertions.assertTrue(isBogus)
        Assertions.assertTrue(isValid)

        result = validator.validate("812990365@VIP.QQ.COM")
        isValid = result.isValid
        warnings = result.email.warnings
        isBogus = warnings.contains(EmailValidationWarning.BOGUS)
        Assertions.assertTrue(isBogus)
        Assertions.assertTrue(isValid)
    }
}
