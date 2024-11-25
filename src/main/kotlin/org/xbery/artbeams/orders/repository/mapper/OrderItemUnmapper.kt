package org.xbery.artbeams.orders.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.jooq.schema.tables.records.OrderItemsRecord
import org.xbery.artbeams.jooq.schema.tables.references.ORDER_ITEMS
import org.xbery.artbeams.orders.domain.OrderItem

/**
 * @author Radek Beran
 */
@Component
class OrderItemUnmapper : RecordUnmapper<OrderItem, OrderItemsRecord> {

    override fun unmap(orderItem: OrderItem): OrderItemsRecord {
        val record = ORDER_ITEMS.newRecord()
        record.id = orderItem.common.id
        record.created = orderItem.common.created
        record.createdBy = orderItem.common.createdBy
        record.modified = orderItem.common.modified
        record.modifiedBy = orderItem.common.modifiedBy
        record.orderId = orderItem.orderId
        record.productId = orderItem.productId
        record.quantity = orderItem.quantity
        record.price = orderItem.price.price
        record.downloaded = orderItem.downloaded
        return record
    }
}
