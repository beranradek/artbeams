package org.xbery.artbeams.orders.domain

/**
 * @author Radek Beran
 */
enum class OrderState {
    /**
     * Order was created, not yet confirmed, maybe abandoned.
     */
    CREATED,
    /**
     * Order was confirmed by customer.
     */
    CONFIRMED,
    /**
     * Payment for order was received.
     */
    PAID,
    /**
     * Order is being processed - preparing for shipment.
     */
    PROCESSING,
    /**
     * Ordered items were shipped.
     */
    SHIPPED,
    /**
     * Ordered items were delivered to customer.
     */
    DELIVERED,
    /**
     * Order was cancelled by customer (from a state that allowed cancellation).
     */
    CANCELLED,
    /**
     * Order was returned by customer. Customer does not want to keep the ordered items.
     */
    RETURNED,
    /**
     * Order price was refunded back to customer.
     */
    REFUNDED
}
