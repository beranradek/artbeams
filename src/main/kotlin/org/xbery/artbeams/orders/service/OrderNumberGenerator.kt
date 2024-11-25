package org.xbery.artbeams.orders.service

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.Dates
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.sequences.repository.SequenceRepository

/**
 * @author Radek Beran
 */
@Service
class OrderNumberGenerator(
    private val appConfig: AppConfig,
    private val sequenceRepository: SequenceRepository,
    private val clock: Clock
) {
    /**
     * Generates new order number.
     */
    fun generateOrderNumber(): String {
        val mainPrefix = getOrderNumberPrefix()
        val localDateTime = clock.now().toLocalDateTime(Dates.DEFAULT_TIME_ZONE)
        val datePrefix = createDatePrefix(localDateTime)
        val ord = sequenceRepository.getAndIncrementNextSequenceValue(Order.ORDER_NUMBER_SEQUENCE_NAME)
        return "${mainPrefix}${datePrefix}${ord}"
    }

    internal fun createDatePrefix(localDateTime: LocalDateTime): String =
        localDateTime.year.toString().substring(2) +
        localDateTime.monthNumber.toString().padStart(2, '0') +
        localDateTime.dayOfMonth.toString().padStart(2, '0')

    private fun getOrderNumberPrefix(): Int {
        return appConfig.findConfigOrDefault(Int::class, "order.number.prefix", 2)
    }
}
