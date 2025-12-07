package org.xbery.artbeams.activitylog.domain

import org.xbery.artbeams.common.repository.IdentifiedEntity
import java.time.Instant

/**
 * User activity log entry for tracking user actions.
 *
 * @author Radek Beran
 */
data class UserActivityLog(
    override val id: String,
    val userId: String,
    val actionType: ActionType,
    val actionTime: Instant,
    val entityType: EntityType?,
    val entityId: String?,
    val ipAddress: String?,
    val userAgent: String?,
    val details: String?
) : IdentifiedEntity

/**
 * Types of user actions that can be logged.
 */
enum class ActionType(val value: String) {
    /** User logged into member section */
    LOGIN("LOGIN"),

    /** User logged out */
    LOGOUT("LOGOUT"),

    /** Order was created */
    ORDER_CREATED("ORDER_CREATED"),

    /** Payment was confirmed for an order */
    PAYMENT_CONFIRMED("PAYMENT_CONFIRMED"),

    /** Product was downloaded (free or paid) */
    PRODUCT_DOWNLOADED("PRODUCT_DOWNLOADED"),

    /** User accessed member section page */
    MEMBER_ACCESS("MEMBER_ACCESS"),

    /** Profile was updated */
    PROFILE_UPDATED("PROFILE_UPDATED"),

    /** Password was changed */
    PASSWORD_CHANGED("PASSWORD_CHANGED");

    companion object {
        fun fromValue(value: String): ActionType? = entries.find { it.value == value }
    }
}

/**
 * Types of entities that activities can be related to.
 */
enum class EntityType(val value: String) {
    /** Activity relates to an order */
    ORDER("ORDER"),

    /** Activity relates to a product */
    PRODUCT("PRODUCT"),

    /** Activity relates to a user */
    USER("USER"),

    /** Activity relates to an article */
    ARTICLE("ARTICLE");

    companion object {
        fun fromValue(value: String): EntityType? = entries.find { it.value == value }
    }
}
