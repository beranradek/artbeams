package org.xbery.artbeams.common.emailvalidator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.xbery.artbeams.common.emailvalidator.entity.EmailValidationResult
import java.util.stream.Stream

/**
 * Created by tomaspavel on 13.4.17.
 */
class SuggestionTest {
    @TestFactory
    fun suggestionsTest(): Stream<DynamicTest> {
        return DynamicTest.stream(
            testData.iterator(),
            { testData -> "Testing " + testData.input },
            { testData ->
                val validator = EmailValidatorBuilder().build()
                val validationResult: EmailValidationResult = validator.validate(PREFIX + testData.input)
                Assertions.assertEquals(testData.expected, validationResult.email.suggestion)
            })
    }

    internal class TestData(val input: String, val expected: String?)

    companion object {
        private const val PREFIX = "test@"
        private val testData: MutableList<TestData> = ArrayList()

        init {
            testData.add(TestData("op.pl", null))
            testData.add(TestData("mail.ru", null))
            testData.add(TestData("o2.pl", null))
            testData.add(TestData("gnail.com", PREFIX + "gmail.com"))
            testData.add(TestData("seznma.cz", PREFIX + "seznam.cz"))
        }
    }
}
