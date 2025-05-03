package org.xbery.artbeams.comments.service

import org.slf4j.LoggerFactory
import org.xbery.artbeams.common.html.HtmlUtils
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.config.repository.AppConfig
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * Detects spam in comments using multiple methods.
 * @author Radek Beran
 * @author AI
 */
@Component
open class SpamDetector(private val appConfig: AppConfig) {
    private val normalizationHelper = NormalizationHelper()
    private val logger = LoggerFactory.getLogger(this.javaClass)
    
    open fun isSpam(comment: String, userName: String, email: String): Boolean {
        return containsSuspiciousPatterns(comment) ||
               containsSuspiciousEmail(email) ||
               containsSuspiciousUsername(userName) ||
               containsSuspiciousContent(comment) ||
               containsSuspiciousLinks(comment) ||
               isNonCzechContent(comment)
    }

    protected open fun containsSuspiciousPatterns(comment: String): Boolean {
        // Check for common spam patterns
        val normalizedComment = normalizationHelper.removeDiacriticalMarks(comment.lowercase())
        val spam = SPAM_PATTERNS.any { pattern ->
            pattern.matcher(normalizedComment).find()
        }
        if (spam) {
            logger.info("Spam: Suspicious pattern in comment: $comment")
        }
        return spam
    }

    protected open fun containsSuspiciousEmail(email: String): Boolean {
        // Check for disposable email domains and suspicious patterns
        val domain = email.substringAfter('@').lowercase()
        val spam = DISPOSABLE_EMAIL_DOMAINS.any { it in domain } ||
               SUSPICIOUS_EMAIL_PATTERNS.any { pattern ->
                   pattern.matcher(email).find()
               }
        if (spam) {
            logger.info("Spam: Suspicious email: $email")
        }
        return spam
    }

    protected open fun containsSuspiciousUsername(userName: String): Boolean {
        // Check for suspicious username patterns
        val normalizedUsername = normalizationHelper.removeDiacriticalMarks(userName.lowercase())
        val spam = SUSPICIOUS_USERNAME_PATTERNS.any { pattern ->
            pattern.matcher(normalizedUsername).find()
        }
        if (spam) {
            logger.info("Spam: Suspicious username: $userName")
        }
        return spam
    }

    protected open fun containsSuspiciousContent(comment: String): Boolean {
        // Check for suspicious content patterns
        val normalizedComment = normalizationHelper.removeDiacriticalMarks(comment.lowercase())
        val spam = SUSPICIOUS_CONTENT_PATTERNS.any { pattern ->
            pattern.matcher(normalizedComment).find()
        }
        if (spam) {
            logger.info("Spam: Suspicious content in comment: $comment")
        }
        return spam
    }

    protected open fun containsSuspiciousLinks(comment: String): Boolean {
        // Check for suspicious URLs and link patterns
        val normalizedComment = normalizationHelper.removeDiacriticalMarks(comment.lowercase())
        val spam = SUSPICIOUS_LINK_PATTERNS.any { pattern ->
            pattern.matcher(normalizedComment).find()
        }
        if (spam) {
            logger.info("Spam: Suspicious link in comment: $comment")
        }
        return spam
    }
    
    /**
     * Detects if content is likely not in Czech language.
     * This is a simple heuristic that checks for Czech diacritical marks.
     * For production use, consider using a proper language detection library.
     */
    protected open fun isNonCzechContent(comment: String): Boolean {
        // Skip short comments
        if (comment.length < 50) {
            return false
        }
        
        // Count Czech characters
        val czechCharCount = comment.count { it.lowercaseChar() in CZECH_CHARS }
        
        // If the comment is long and has very few Czech characters, it's probably not Czech
        // The threshold is set to 2% of the text - adjust as needed
        val spam = czechCharCount < comment.length * 0.02
        if (spam) {
            logger.info("Spam: Non-Czech content detected in comment: $comment")
        }
        return spam
    }

    companion object {
        // Czech specific characters
        private val CZECH_CHARS = setOf('á', 'č', 'ď', 'é', 'ě', 'í', 'ň', 'ó', 'ř', 'š', 'ť', 'ú', 'ů', 'ý', 'ž')
        
        // Common spam patterns
        private val SPAM_PATTERNS = listOf(
            Pattern.compile("\\b(?:viagra|cialis|levitra|pharmacy|pills)\\b"),
            Pattern.compile("\\b(?:casino|gambling|poker|betting)\\b"),
            Pattern.compile("\\b(?:loan|mortgage|credit|debt)\\b"),
            Pattern.compile("\\b(?:investment|trading|forex|bitcoin)\\b"),
            Pattern.compile("\\b(?:diet|weight loss|fat burner)\\b"),
            Pattern.compile("\\b(?:cultivo|indoor|sustrato|kit)\\b") // Spanish terms from example
        )

        // Disposable email domains
        private val DISPOSABLE_EMAIL_DOMAINS = setOf(
            "tempmail.com", "mailinator.com", "guerrillamail.com", "yopmail.com",
            "throwawaymail.com", "10minutemail.com", "temp-mail.org"
        )

        // Suspicious email patterns
        private val SUSPICIOUS_EMAIL_PATTERNS = listOf(
            Pattern.compile("^[a-z0-9]{8,}@"),
            Pattern.compile("@(?:yahoo|hotmail|outlook)\\.com$")
        )

        // Suspicious username patterns
        private val SUSPICIOUS_USERNAME_PATTERNS = listOf(
            Pattern.compile("^[a-z0-9]{8,}$"),
            Pattern.compile("^[a-z]+[0-9]+$"),
            Pattern.compile("^[a-z]+[0-9]+[a-z]+$")
        )

        // Suspicious content patterns
        private val SUSPICIOUS_CONTENT_PATTERNS = listOf(
            Pattern.compile("\\b(?:buy|sell|cheap|discount|offer|promotion)\\b"),
            Pattern.compile("\\b(?:click here|visit us|check out|learn more)\\b"),
            Pattern.compile("\\b(?:limited time|special offer|exclusive deal)\\b"),
            Pattern.compile("\\b(?:guaranteed|100%|money back|satisfaction)\\b")
        )

        // Suspicious link patterns
        private val SUSPICIOUS_LINK_PATTERNS = listOf(
            Pattern.compile("https?://[^\\s]+"),
            Pattern.compile("\\b(?:www\\.|http://|https://)\\b"),
            Pattern.compile("\\b(?:bit\\.ly|goo\\.gl|tinyurl\\.com|t\\.co)\\b")
        )
    }
} 
