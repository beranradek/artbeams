package org.xbery.artbeams.mailing.dto

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
data class SubscriptionRequest(
    @JsonProperty("email") val email: String,
    @JsonProperty("fields") val fields: SubscriberFields,
    @JsonProperty("groups") val groups: List<String>,
    @JsonProperty("ip_address") val ipAddress: String?
)

data class SubscriberFields(
    @JsonProperty("name") val name: String,
    @JsonProperty("last_name") val lastName: String?
)
