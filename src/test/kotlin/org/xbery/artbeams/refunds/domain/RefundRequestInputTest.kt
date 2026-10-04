package org.xbery.artbeams.refunds.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RefundRequestInputTest :
    StringSpec({
        "accepts an empty optional reason" {
            RefundRequestInput.normalizeReason(null) shouldBe null
            RefundRequestInput.normalizeReason("   ") shouldBe null
        }

        "trims a valid reason" {
            RefundRequestInput.normalizeReason("  Omylem objednáno.  ") shouldBe "Omylem objednáno."
        }

        "rejects a reason longer than 2000 characters" {
            RefundRequestInput.validateReason("x".repeat(2001)) shouldBe false
        }

        "keeps the requested status distinct from a resolved refund" {
            RefundRequestStatus.REQUESTED.name shouldBe "REQUESTED"
            RefundRequestStatus.RESOLVED.name shouldBe "RESOLVED"
        }
    })
