package org.xbery.artbeams.orders.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.jooq.schema.tables.records.OrdersRecord
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.orders.domain.OrderState

/**
 * @author Radek Beran
 */
@Component
class OrderMapper : RecordMapper<OrdersRecord, Order> {

    override fun map(record: OrdersRecord): Order {
        return Order(
            common = AssetAttributes(
                id = requireNotNull(record.id),
                created = requireNotNull(record.created),
                createdBy = requireNotNull(record.createdBy),
                modified = requireNotNull(record.modified),
                modifiedBy = requireNotNull(record.modifiedBy)
            ),
            orderNumber = requireNotNull(record.orderNumber),
            state = OrderState.valueOf(requireNotNull(record.state)),
            items = emptyList(),
            paidTime = record.paidTime,
            paymentMethod = record.paymentMethod,
            notes = record.notes
        )
    }
}
