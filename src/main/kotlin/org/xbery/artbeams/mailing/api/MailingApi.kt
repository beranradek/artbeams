package org.xbery.artbeams.mailing.api

import org.xbery.artbeams.mailing.dto.SubscriptionResponse

/**
 * MailerLite API as described at https://developers.mailerlite.com/docs/#mailerlite-api.
 *
 * @author Radek Beran
 */
interface MailingApi {

    /**
     * Subscribes user to given subscription group.
     * If a subscriber already exists, it will be updated with new values.
     * This is non-destructive operation, so omitting fields or groups will not remove them from subscriber.
     */
    fun subscribeToGroup(email: String, name: String, subscriberGroupId: String): SubscriptionResponse
}
