package org.xbery.artbeams.userproducts.controller

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderState
import java.time.Instant

class UserProductControllerTest :
    StringSpec({
        "does not allow downloading a refunded order even when the product is now free" {
            UserProductController.isEligibleForDownload(
                order(OrderState.REFUNDED),
                isFreeProduct = true
            ) shouldBe false
        }

        "allows a free product from an existing non-refunded order" {
            UserProductController.isEligibleForDownload(
                order(OrderState.CONFIRMED),
                isFreeProduct = true
            ) shouldBe true
        }
    }) {
    companion object {
        private fun order(state: OrderState) = Order(
            common = AssetAttributes("order-1", Instant.EPOCH, "user-1", Instant.EPOCH, "user-1"),
            orderNumber = "2026001",
            state = state,
            orderItems = emptyList()
        )
    }
}
