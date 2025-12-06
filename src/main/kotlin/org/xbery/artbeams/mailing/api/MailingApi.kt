package org.xbery.artbeams.mailing.api

import org.xbery.artbeams.mailing.api.dto.MailerLiteSubscriptionResponse

/**
 * MailerLite API as described at https://developers.mailerlite.com/docs/#mailerlite-api.
 *
 * @author Radek Beran
 */
interface MailingApi {

    fun isSubscribedToGroup(email: String, subscriberGroupId: String): Boolean

    /**
     * Removes subscriber from given subscription group.
     * Returns true if subscriber was removed successfully, false if subscriber was not in the group.
     */
    fun removeFromGroup(email: String, subscriberGroupId: String): Boolean

    /**
     * Subscribes user to given subscription group.
     * Note: Use this for silent subscription (e.g., after payment) when you don't want to trigger email workflows.
     * For subscriptions that should trigger automation workflows, use resubscribeToGroup instead.
     */
    fun subscribeToGroup(email: String, name: String, subscriberGroupId: String, ipAddress: String?): MailerLiteSubscriptionResponse

    /**
     * Resubscribes user to given subscription group to trigger automation workflow.
     * If subscriber is already in the group, removes them first and then re-adds them.
     * This ensures automation workflows are triggered even on resubscription attempts.
     */
    fun resubscribeToGroup(email: String, name: String, subscriberGroupId: String, ipAddress: String?): MailerLiteSubscriptionResponse
}
