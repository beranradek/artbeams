package org.xbery.artbeams.mailing.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * E.g.:
 * <code>
 * {
 *   "email": "dummy@example.com",
 *   "fields": {
 *     "name": "Dummy",
 *     "last_name": "Testerson"
 *   },
 *   "groups": [
 *     "4243829086487936",
 *     "14133878422767533",
 *     "31985378335392975"
 *   ]
 * }
 * </code>
 *
 * @author Radek Beran
 */
data class MailerLiteSubscriptionRequest(
    @JsonProperty("email") val email: String,
    @JsonProperty("fields") val fields: MailerLiteSubscriberFields,
    @JsonProperty("groups") val groups: List<String>,
    @JsonProperty("ip_address") val ipAddress: String?
)

data class MailerLiteSubscriberFields(
    @JsonProperty("name") val name: String,
    @JsonProperty("last_name") val lastName: String?
)
