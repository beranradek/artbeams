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

    override fun unmap(order: Order): OrdersRecord {
        val record = ORDERS.newRecord()
        record.id = order.common.id
        record.created = order.common.created
        record.createdBy = order.common.createdBy
        record.modified = order.common.modified
        record.modifiedBy = order.common.modifiedBy
        record.orderNumber = order.orderNumber
        record.state = order.state.name
        record.paidTime = order.paidTime
        record.paymentMethod = order.paymentMethod
        record.notes = order.notes
        return record
    }
}
