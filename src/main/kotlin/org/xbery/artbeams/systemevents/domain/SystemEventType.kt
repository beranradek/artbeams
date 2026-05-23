package org.xbery.artbeams.systemevents.domain

/**
 * High-value events for debugging important flows.
 * @author Radek Beran
 */
enum class SystemEventType {
    USER_REGISTRATION_FAILED,
    CONSENT_CONFIRMATION_FAILED,
    LOGIN_FAILED,
    ORDER_CREATE_FAILED,
    PAYMENT_CONFIRMATION_FAILED,
    PRODUCT_DOWNLOAD_FAILED,
    EMAIL_SEND_FAILED,
    SEARCH_REINDEX_JOB_FAILED,
    REMOTE_DB_SYNC_FAILED
}
