package org.xbery.artbeams.mailing.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import org.xbery.artbeams.config.repository.TestConfigRepository

/**
 * @author Radek Beran
 */
internal class MailerLiteMailingApiTest {

    @Test
    @Disabled("Only for manual testing of real API call")
    fun subscribeToGroup() {
        val config = TestMailingApiConfig
        val api = MailerLiteMailingApi(config.mailerLiteApiRestTemplate(RestTemplateBuilder()), config)
        val email = "radek.bn+231209t@gmail.com"
        val response = api.subscribeToGroup(email, "Radek", requireNotNull(config.offer1SubscriptionGroupId))
        assertEquals(email, response.data.email)
        assertNotNull(response.data.id)
    }

    object TestMailingApiConfig: MailingApiConfig(TestConfigRepository(mapOf(
        "mailerlite.api.baseUrl" to "https://connect.mailerlite.com",
        "mailerlite.api.token" to "FILL IN API TOKEN",
        "mailerlite.subscriber.group1" to "FILL IN SUBSCRIBER GROUP ID"
    )))
}
