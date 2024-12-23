package org.xbery.artbeams.common.emailvalidator.entity

import org.xbery.artbeams.common.emailvalidator.enums.MessageSeverity

class EmailValidationMessage(val severity: MessageSeverity, val text: String)
