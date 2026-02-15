package org.xbery.artbeams.comments.controller

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import net.formio.validation.ValidationContext

/**
 * Test for CommentValidator.
 * @author Radek Beran
 * @author AI
 */
class CommentValidatorTest :
    StringSpec({

        val validator = CommentValidator.INSTANCE

        // Tests for stopwords
        "should reject comment containing viagra" {
            val ctx = createValidationContext("Buy viagra online now!")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        "should reject comment containing cialis" {
            val ctx = createValidationContext("Cialis for the best price")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        "should reject comment containing levitra" {
            val ctx = createValidationContext("Discount levitra pills available")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        "should reject comment containing stopword in any case" {
            val ctx1 = createValidationContext("VIAGRA is on sale")
            val errors1 = validator.validate(ctx1)
            errors1 shouldHaveSize 1

            val ctx2 = createValidationContext("Buy ViAgRa here")
            val errors2 = validator.validate(ctx2)
            errors2 shouldHaveSize 1
        }

        "should reject comment containing stopword as part of word" {
            val ctx = createValidationContext("This is about viagra123")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        // Tests for links in comments
        "should reject comment starting with link" {
            val ctx = createValidationContext("https://example.com Check this out")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        "should reject comment ending with link" {
            val ctx = createValidationContext("Check out this site https://example.ru")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        "should reject comment with link in the middle" {
            val ctx = createValidationContext("Explore the latest additions here — http://www.tiroavolobologna.it/media/pgs/le-code-promo-1xbet_bonus.html")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        "should reject comment with http link" {
            val ctx = createValidationContext("Visit http://spam.ru for more info")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        "should reject comment with https link" {
            val ctx = createValidationContext("More at https://example.ru/path/to/page")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        "should reject comment with www link" {
            val ctx = createValidationContext("Visit www.example.com for details")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        // Tests for legitimate comments (should not be rejected)
        "should allow comment without links or stopwords" {
            val ctx = createValidationContext("This is a normal comment about something interesting")
            val errors = validator.validate(ctx)
            errors.shouldBeEmpty()
        }

        "should allow comment mentioning Russia (not the stopwords)" {
            val ctx = createValidationContext("I love traveling to Russia")
            val errors = validator.validate(ctx)
            errors.shouldBeEmpty()
        }

        "should allow empty comment" {
            val ctx = createValidationContext("")
            val errors = validator.validate(ctx)
            errors.shouldBeEmpty()
        }

        // Tests for HTML markup (existing validation)
        "should reject comment containing HTML markup" {
            val ctx = createValidationContext("<script>alert('xss')</script>")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }

        // Tests for Russian characters (existing validation)
        "should reject comment with many Russian characters" {
            val ctx = createValidationContext("Привет, это спам комментарий с русскими буквами")
            val errors = validator.validate(ctx)
            errors shouldHaveSize 1
        }
    })

/**
 * Helper function to create a ValidationContext for testing.
 */
private fun createValidationContext(value: String): ValidationContext<String> = mockk<ValidationContext<String>> {
    every { validatedValue } returns value
    every { elementName } returns "comment"
}
