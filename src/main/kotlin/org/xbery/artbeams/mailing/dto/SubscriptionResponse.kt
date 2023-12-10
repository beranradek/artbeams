package org.xbery.artbeams.mailing.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Radek Beran
 */
data class SubscriptionResponse(
    @JsonProperty("data") val data: SubscriptionResponseData
)

data class SubscriptionResponseData(
    @JsonProperty("id") val id: String,
    @JsonProperty("email") val email: String
)
