package org.xbery.artbeams.orders.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.jooq.schema.tables.records.OrderItemsRecord
import org.xbery.artbeams.orders.domain.OrderItem

/**
 * @author Radek Beran
 */
@Component
class OrderItemMapper : RecordMapper<OrderItemsRecord, OrderItem> {

    override fun map(record: OrderItemsRecord): OrderItem {
        return OrderItem(
            common = AssetAttributes(
                id = requireNotNull(record.id),
                created = requireNotNull(record.created),
                createdBy = requireNotNull(record.createdBy),
                modified = requireNotNull(record.modified),
                modifiedBy = requireNotNull(record.modifiedBy)
            ),
            orderId = requireNotNull(record.orderId),
            productId = requireNotNull(record.productId),
            quantity = requireNotNull(record.quantity),
            downloaded = record.downloaded
        )
    }
}
