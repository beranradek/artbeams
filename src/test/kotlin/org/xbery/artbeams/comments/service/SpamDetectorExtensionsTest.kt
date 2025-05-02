package org.xbery.artbeams.comments.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.mockito.Mockito
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.comments.domain.CommentState
import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.config.repository.AppConfig

/**
 * Tests for extension use cases with SpamDetector
 */
class SpamDetectorExtensionsTest : FunSpec({
    // Create a mock AppConfig using Mockito
    val mockAppConfig = Mockito.mock(AppConfig::class.java)
    Mockito.`when`(mockAppConfig.findConfig(Mockito.anyString())).thenReturn(null)
    
    val spamDetector = SpamDetector(mockAppConfig)
    val noLanguageDetector = TestSpamDetector(mockAppConfig, false)

    context("SpamDetector extensions for Comment entity") {
        test("should detect spam in Comment entities") {
            // Helper function to create test comments
            fun createTestComment(comment: String, userName: String, email: String): Comment =
                Comment(
                    common = AssetAttributes.EMPTY,
                    parentId = null,
                    comment = comment,
                    userName = userName,
                    email = email,
                    state = CommentState.APPROVED,
                    entityKey = EntityKey("Article", "123"),
                    ip = "127.0.0.1",
                    userAgent = "Test"
                )

            // Spam comments
            val spamComment1 = createTestComment(
                "Check out our discount offer at www.example.com",
                "Spammer123",
                "random123@yahoo.com"
            )
            val spamComment2 = createTestComment(
                "https://bit.ly/3rT5Yh Buy now and get 50% discount!",
                "UserBuy",
                "user@tempmail.com"
            )
            val spamComment3 = createTestComment(
                "Legitimate looking text with no obvious indicators",
                "JuniorKeera",
                "ghueg1wdw5r@yahoo.com"
            )
            
            // Legitimate comments
            val legitComment1 = createTestComment(
                "Děkuji za skvělý článek, moc se mi líbil.",
                "Jan Novák",
                "jan.novak@seznam.cz"
            )
            val legitComment2 = createTestComment(
                "Máte nějaké další informace k tomuto tématu?",
                "Marie",
                "marie@firma.cz"
            )

            // Test direct extension function - for spam detection we expect it to work
            isCommentSpam(spamComment1, spamDetector) shouldBe true
            isCommentSpam(spamComment2, spamDetector) shouldBe true
            isCommentSpam(spamComment3, spamDetector) shouldBe true
            
            // Test legitimate Czech comments - use the no-language detector
            isCommentSpam(legitComment1, noLanguageDetector) shouldBe false
            isCommentSpam(legitComment2, noLanguageDetector) shouldBe false
        }
    }
})

// Extension function that makes testing with Comments easier
private fun isCommentSpam(comment: Comment, spamDetector: SpamDetector): Boolean {
    return spamDetector.isSpam(comment.comment, comment.userName, comment.email)
}

/**
 * This is a suggestion for a potential extension that could be implemented
 * to enhance SpamDetector with language detection capabilities.
 */
/*
private fun isCommentSpamWithLanguageCheck(comment: Comment): Boolean {
    val spamDetector = SpamDetector()
    
    // Basic spam check
    if (spamDetector.isSpam(comment.comment, comment.userName, comment.email)) {
        return true
    }
    
    // Language check (example implementation)
    val czechChars = setOf('á', 'č', 'ď', 'é', 'ě', 'í', 'ň', 'ó', 'ř', 'š', 'ť', 'ú', 'ů', 'ý', 'ž')
    val czechCharCount = comment.comment.count { it.lowercaseChar() in czechChars }
    val commentLength = comment.comment.length
    
    // If comment is long enough and contains very few Czech characters, it's probably not Czech
    if (commentLength > 50 && czechCharCount < commentLength * 0.02) {
        return true
    }
    
    return false
}
*/

/**
 * This is a suggestion for a potential extension that could detect repeated submissions
 * from the same IP address with different user details, which is a common spam pattern.
 */
/*
private fun detectRepeatedSubmissions(comments: List<Comment>): Boolean {
    // Group comments by IP address
    val commentsByIp = comments.groupBy { it.ip }
    
    // Check each IP with multiple comments
    commentsByIp.forEach { (ip, ipComments) ->
        if (ipComments.size > 1) {
            // Check if there are different user details from the same IP
            val distinctUserNames = ipComments.map { it.userName }.distinct()
            val distinctEmails = ipComments.map { it.email }.distinct()
            
            // If there are multiple user names or emails from the same IP in a short time, flag as spam
            if (distinctUserNames.size > 1 || distinctEmails.size > 1) {
                return true
            }
        }
    }
    
    return false
}
*/ 