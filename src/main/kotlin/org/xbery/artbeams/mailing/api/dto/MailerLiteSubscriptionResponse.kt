package org.xbery.artbeams.mailing.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Radek Beran
 */
data class MailerLiteSubscriptionResponse(
    @JsonProperty("data") val data: MailerLiteSubscriptionResponseData
)

data class MailerLiteSubscriptionResponseData(
    @JsonProperty("id") val id: String,
    @JsonProperty("email") val email: String
)
