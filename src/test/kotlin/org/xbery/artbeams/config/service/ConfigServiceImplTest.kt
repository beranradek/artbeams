package org.xbery.artbeams.config.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.config.domain.Config
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.config.repository.ConfigRepository

/**
 * Test for ConfigServiceImpl sensitive value masking.
 * @author Radek Beran
 */
class ConfigServiceImplTest : StringSpec({

    "should mask sensitive configuration values in findConfigs" {
        val configRepository = mockk<ConfigRepository>()
        val appConfig = mockk<AppConfig>()
        val service = ConfigServiceImpl(configRepository, appConfig)

        val testConfigs = listOf(
            Config("normal.setting", "normal-value"),
            Config("database.password", "super-secret-password"),
            Config("api.key", "secret-api-key"),
            Config("google.oauth.client.json", "{\"client_secret\":\"secret\"}"),
            Config("salt.value", "random-salt"),
            Config("security.token", "jwt-token")
        )

        val mockResultPage = ResultPage(testConfigs, Pagination(0, 10))
        every { configRepository.findConfigs(any(), any()) } returns mockResultPage

        val result = service.findConfigs(Pagination(0, 10), null)

        result.records.size shouldBe 6
        result.records[0].entryValue shouldBe "normal-value" // normal setting not masked
        result.records[1].entryValue shouldBe "*****" // password masked
        result.records[2].entryValue shouldBe "*****" // api.key masked
        result.records[3].entryValue shouldBe "*****" // google.oauth.client.json masked
        result.records[4].entryValue shouldBe "*****" // salt masked
        result.records[5].entryValue shouldBe "*****" // token masked
    }

    "should mask sensitive configuration values in findByKey" {
        val configRepository = mockk<ConfigRepository>()
        val appConfig = mockk<AppConfig>()
        val service = ConfigServiceImpl(configRepository, appConfig)

        // Test normal config - not masked
        val normalConfig = Config("normal.setting", "normal-value")
        every { configRepository.findByKey("normal.setting") } returns normalConfig

        val normalResult = service.findByKey("normal.setting")
        normalResult?.entryValue shouldBe "normal-value"

        // Test sensitive config - masked
        val sensitiveConfig = Config("database.password", "super-secret-password")
        every { configRepository.findByKey("database.password") } returns sensitiveConfig

        val sensitiveResult = service.findByKey("database.password")
        sensitiveResult?.entryValue shouldBe "*****"
    }
})