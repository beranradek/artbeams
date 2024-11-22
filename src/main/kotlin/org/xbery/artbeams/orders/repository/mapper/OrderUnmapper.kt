package org.xbery.artbeams.orders.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.jooq.schema.tables.records.OrdersRecord
import org.xbery.artbeams.jooq.schema.tables.references.ORDERS
import org.xbery.artbeams.orders.domain.Order

/**
 * @author Radek Beran
 */
@Component
class OrderUnmapper : RecordUnmapper<Order, OrdersRecord> {

    override fun unmap(orderItem: Order): OrdersRecord {
        val record = ORDERS.newRecord()
        record.id = orderItem.common.id
        record.created = orderItem.common.created
        record.createdBy = orderItem.common.createdBy
        record.modified = orderItem.common.modified
        record.modifiedBy = orderItem.common.modifiedBy
        return record
    }
}
