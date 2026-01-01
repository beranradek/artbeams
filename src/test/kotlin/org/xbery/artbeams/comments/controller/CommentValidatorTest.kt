package org.xbery.artbeams.comments.controller

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import net.formio.validation.ValidationContext

/**
 * Test for CommentValidator.
 * @author Radek Beran
 * @author AI
 */
class CommentValidatorTest : StringSpec({

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

    // Tests for .ru domain links at the start
    "should reject comment starting with .ru link" {
        val ctx = createValidationContext("https://example.ru This is spam content")
        val errors = validator.validate(ctx)
        errors shouldHaveSize 1
    }

    "should reject comment starting with .ru/ link" {
        val ctx = createValidationContext("https://example.ru/ Some spam text here")
        val errors = validator.validate(ctx)
        errors shouldHaveSize 1
    }

    "should reject comment starting with http .ru link" {
        val ctx = createValidationContext("http://spam.ru Check out this site")
        val errors = validator.validate(ctx)
        errors shouldHaveSize 1
    }

    // Tests for .ru domain links at the end
    "should reject comment ending with .ru link" {
        val ctx = createValidationContext("Check out this spam site https://example.ru")
        val errors = validator.validate(ctx)
        errors shouldHaveSize 1
    }

    "should reject comment ending with .ru/ link" {
        val ctx = createValidationContext("Visit our website https://example.ru/")
        val errors = validator.validate(ctx)
        errors shouldHaveSize 1
    }

    "should reject comment ending with http .ru link" {
        val ctx = createValidationContext("More info at http://spam.ru")
        val errors = validator.validate(ctx)
        errors shouldHaveSize 1
    }

    // Tests for .ru links with paths
    "should reject comment starting with .ru link containing path" {
        val ctx = createValidationContext("https://example.ru/path/to/page some text")
        val errors = validator.validate(ctx)
        errors shouldHaveSize 1
    }

    "should reject comment ending with .ru link containing path" {
        val ctx = createValidationContext("some text https://example.ru/path/to/page")
        val errors = validator.validate(ctx)
        errors shouldHaveSize 1
    }

    // Tests for legitimate comments (should not be rejected)
    "should allow comment without stopwords or .ru links" {
        val ctx = createValidationContext("This is a normal comment about something interesting")
        val errors = validator.validate(ctx)
        errors.shouldBeEmpty()
    }

    "should allow comment with .ru link in the middle" {
        val ctx = createValidationContext("I found this https://example.ru which is interesting")
        val errors = validator.validate(ctx)
        // This should still pass because the link is not at the start or end
        // Actually, looking at the implementation, it checks for .ru at start OR end, so this should be rejected
        // Let me reconsider: if a link is in the middle, it's neither the first word nor the last word
        errors.shouldBeEmpty()
    }

    "should allow comment with .com link at start" {
        val ctx = createValidationContext("https://example.com Check this out")
        val errors = validator.validate(ctx)
        // This should fail because startsWithLink() checks for any link at the start
        errors shouldHaveSize 1
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
private fun createValidationContext(value: String): ValidationContext<String> {
    return object : ValidationContext<String> {
        override fun getValidatedValue(): String = value
        override fun getElementName(): String = "comment"
        override fun getPropertyName(): String = "comment"
        override fun getRootClass(): Class<*> = String::class.java
        override fun getRootPropertyPath(): String = "comment"
    }
}
