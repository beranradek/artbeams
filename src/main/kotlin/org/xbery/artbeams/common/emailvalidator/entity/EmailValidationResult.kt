package org.xbery.artbeams.common.emailvalidator.entity

class EmailValidationResult(val isValid: Boolean, val messages: List<EmailValidationMessage>, val email: Email)
