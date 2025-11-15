package org.xbery.artbeams.comments.service

import org.xbery.artbeams.config.repository.AppConfig

/**
 * Test version of SpamDetector that allows disabling certain checks for testing
 * and exposes protected methods as public for testing.
 */
class TestSpamDetector(appConfig: AppConfig, private val enableLanguageDetection: Boolean = true) : SpamDetector(appConfig) {
    override fun isSpam(comment: String, userName: String, email: String): Boolean {
        if (!enableLanguageDetection) {
            // Skip language detection but keep other checks
            return containsSuspiciousPatterns(comment) ||
                   containsSuspiciousEmail(email) ||
                   containsSuspiciousUsername(userName) ||
                   containsSuspiciousContent(comment) ||
                   containsSuspiciousLinks(comment)
        }
        return super.isSpam(comment, userName, email)
    }
    
    // Override isNonCzechContent to handle special test cases if needed and make it public
    public override fun isNonCzechContent(comment: String): Boolean {
        if (!enableLanguageDetection) {
            return false
        }
        return super.isNonCzechContent(comment)
    }
    
    // Expose other protected methods as public for testing
    public override fun containsSuspiciousPatterns(comment: String): Boolean = super.containsSuspiciousPatterns(comment)
    public override fun containsSuspiciousEmail(email: String): Boolean = super.containsSuspiciousEmail(email)
    public override fun containsSuspiciousUsername(userName: String): Boolean = super.containsSuspiciousUsername(userName)
    public override fun containsSuspiciousContent(comment: String): Boolean = super.containsSuspiciousContent(comment)
    public override fun containsSuspiciousLinks(comment: String): Boolean = super.containsSuspiciousLinks(comment)
} 
