package org.xbery.artbeams.common.emailvalidator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

/**
 * Created by TPa on 14.02.17.
 */
class IsValidTest {
    @TestFactory
    fun testValid(): Stream<DynamicTest> {
        return DynamicTest.stream(
            listOf(*validEmails).iterator(),
            { email: String -> "Testing $email" },
            { email: String ->
                val validator = EmailValidatorBuilder().build()
                val result = validator.validate(email)
                Assertions.assertTrue(result.isValid)
            })
    }

    @TestFactory
    fun testInValid(): Stream<DynamicTest> {
        return DynamicTest.stream(
            listOf(*invalidEmails).iterator(),
            { email: String -> "Testing $email" },
            { email: String ->
                val validator = EmailValidatorBuilder().build()
                val result = validator.validate(email)
                Assertions.assertFalse(result.isValid)
            })
    }

    companion object {
        private val validEmails = arrayOf(
            "dan@etnetera.cz",
            "prettyandsimple@example.com",
            "very.common@example.com",
            "disposable.style.email.with+symbol@example.com",
            "other.email-with-dash@example.com",
            "x@example.com",  // (one-letter local-part)
            "\"much.more unusual\"@example.com",
            "\"very.unusual.@.unusual.com\"@example.com",
            "\"very.(),:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\"@strange.example.com",
            "example-indeed@strange-example.com",  //"admin@mailserver1",// (local domain name with no TLD)
            "#!$%&'*+-/=?^_`{}|~@example.org",
            "\"()<>[]:,;@\\\\\"!#$%&'-/=?^_`{}| ~.a\"@example.org",
            "\" \"@example.org",  //(space between the quotes)
            //"example@localhost",// (sent from localhost)
            "example@s.solutions",  // (see the List of Internet top-level domains)
            //"user@localserver",
            //"user@tt",//(although ICANN highly discourages dotless email addresses)
            //"user@[IPv6:2001:DB8::1]"
            "abc.\"defghi\".xyz@example.com",
            "\"abcdefghixyz\"@example.com"
        )
        private val invalidEmails = arrayOf(
            "nemohotmail.com",
            "@hotmail.com",
            "nemo@",
            "nemo@@hotmail.com",
            "nem[o@hotmail.com",  //"nemo@aol.com.com",
            "nemo@hotmail.com.",
            "nemo@hotmail.com-",
            "nemo@.hotmail.com",
            "nemo@-hotmail.com",
            ".nemo@hotmail.com",
            "test.@seznam.cz",
            "Abc.example.com",  //(no @ character)
            "A@b@c@example.com",  // (only one @ is allowed outside quotation marks)
            "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com",  // (none of the special characters in this local-part are allowed outside quotation marks)
            //"just\"not\"right@example.com",//(quoted strings must be dot separated or the only element making up the local-part)
            "this is\"not\\allowed@example.com",  //(spaces, quotes, and backslashes may only exist when within quoted strings and preceded by a backslash)
            "this\\ still\\\"not\\\\allowed@example.com",  // (even if escaped (preceded by a backslash), spaces, quotes, and backslashes must still be contained by quotes)
            "john..doe@example.com",  //(double dot before @)
            "john.doe@example..com",  //(double dot after @)
            ".test@.test.com.",
            "test@test..com",  //				"test@test",
            "abc\\\"def\\\"ghi@example.com"
        )
    }
}
