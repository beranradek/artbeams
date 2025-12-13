package org.xbery.artbeams.orders.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.xbery.artbeams.config.repository.TestAppConfig
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.sequences.repository.SequenceRepository

class OrderNumberGeneratorTest : FunSpec({

    val sequenceRepository = mockk<SequenceRepository>()
    every { sequenceRepository.getAndIncrementNextSequenceValue(Order.ORDER_NUMBER_SEQUENCE_NAME) } returns 1
    val generator = OrderNumberGenerator(
        TestAppConfig(emptyMap()),
        sequenceRepository,
        Clock.fixed(java.time.Instant.parse("2024-11-25T09:16:00Z"), ZoneOffset.UTC)
    )

    test("createDatePrefix") {
        generator.createDatePrefix(LocalDateTime.of(2021, 1, 1, 0, 0)) shouldBe "210101"
        generator.createDatePrefix(LocalDateTime.of(2021, 12, 31, 0, 0)) shouldBe "211231"
    }

    test("generateOrderNumber") {
        generator.generateOrderNumber() shouldBe "22411251"
    }
})
