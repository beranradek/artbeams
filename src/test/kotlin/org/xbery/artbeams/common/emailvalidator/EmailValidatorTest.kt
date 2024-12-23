package org.xbery.artbeams.common.emailvalidator

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for [EmailValidator].
 *
 * @author Radek Beran
 */
class EmailValidatorTest : ShouldSpec({

    val validEmails = arrayOf(
        "dan@etnetera.cz",
        "prettyandsimple@example.com",
        "very.common@example.com",
        "disposable.style.email.with+symbol@example.com",
        "other.email-with-dash@example.com",
        "x@example.com", // (one-letter local-part)
        "\"much.more unusual\"@example.com",
        "\"very.unusual.@.unusual.com\"@example.com",
        "\"very.(),:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\"@strange.example.com",
        "example-indeed@strange-example.com", // "admin@mailserver1",// (local domain name with no TLD)
        "#!$%&'*+-/=?^_`{}|~@example.org",
        "\"()<>[]:,;@\\\\\"!#$%&'-/=?^_`{}| ~.a\"@example.org",
        "\" \"@example.org", // (space between the quotes)
        // "example@localhost",// (sent from localhost)
        "example@s.solutions", // (see the List of Internet top-level domains)
        // "user@localserver",
        // "user@tt",//(although ICANN highly discourages dotless email addresses)
        // "user@[IPv6:2001:DB8::1]"
        "abc.\"defghi\".xyz@example.com",
        "\"abcdefghixyz\"@example.com"
    )

    val invalidEmails = arrayOf(
        "",
        "nemohotmail.com",
        "@hotmail.com",
        "nemo@",
        "nemo@@hotmail.com",
        "nem[o@hotmail.com", // "nemo@aol.com.com",
        "nemo@hotmail.com.",
        "nemo@hotmail.com-",
        "nemo@.hotmail.com",
        "nemo@-hotmail.com",
        ".nemo@hotmail.com",
        "test.@seznam.cz",
        "Abc.example.com", // (no @ character)
        "A@b@c@example.com", // (only one @ is allowed outside quotation marks)
        // (none of the special characters in this local-part are allowed outside quotation marks):
        "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com",
        // "just\"not\"right@example.com",//(quoted strings must be dot separated or the only element making up the local-part)
        // (spaces, quotes, and backslashes may only exist when within quoted strings and preceded by a backslash):
        "this is\"not\\allowed@example.com",
        // (even if escaped (preceded by a backslash), spaces, quotes, and backslashes must still be contained by quotes):
        "this\\ still\\\"not\\\\allowed@example.com",
        "john..doe@example.com", // (double dot before @)
        "john.doe@example..com", // (double dot after @)
        ".test@.test.com.",
        "test@test..com",
        "abc\\\"def\\\"ghi@example.com",
        "m.g.le.n.enk.otpank.s.wi.m.pul.@gmail.com"
    )

    context("Email validator") {
        should("distinguish valid and invalid emails") {
            val validator = EmailValidatorBuilder().build()
            for (email in validEmails) {
                validator.validate(email).isValid shouldBe true
            }
            for (email in invalidEmails) {
                validator.validate(email).isValid shouldBe false
            }
        }
    }
})
