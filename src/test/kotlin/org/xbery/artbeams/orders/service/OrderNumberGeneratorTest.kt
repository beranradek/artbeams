package org.xbery.artbeams.orders.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.xbery.artbeams.common.clock.FixedClock
import org.xbery.artbeams.common.clock.TestClockConfiguration
import org.xbery.artbeams.config.repository.TestAppConfig
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.sequences.repository.SequenceRepository

class OrderNumberGeneratorTest : FunSpec({

    val sequenceRepository = mockk<SequenceRepository>()
    every { sequenceRepository.getAndIncrementNextSequenceValue(Order.ORDER_NUMBER_SEQUENCE_NAME) } returns 1
    val generator = OrderNumberGenerator(
        TestAppConfig(emptyMap()),
        sequenceRepository,
        FixedClock(TestClockConfiguration.FIXED_TIME)
    )

    test("createDatePrefix") {
        generator.createDatePrefix(LocalDateTime(2021, 1, 1, 0, 0)) shouldBe "210101"
        generator.createDatePrefix(LocalDateTime(2021, 12, 31, 0, 0)) shouldBe "211231"
    }

    test("generateOrderNumber") {
        generator.generateOrderNumber() shouldBe "22411251"
    }
})
