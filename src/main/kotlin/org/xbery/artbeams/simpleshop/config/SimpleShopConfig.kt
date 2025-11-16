package org.xbery.artbeams.simpleshop.config

import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfig

/**
 * Configuration for SimpleShop.cz API integration.
 * @author Radek Beran
 */
@Component
class SimpleShopConfig(
    private val appConfig: AppConfig
) {
    /**
     * SimpleShop API base URL (e.g., https://api.simpleshop.cz/v2)
     */
    fun getApiBaseUrl(): String = appConfig.findConfig("simpleshop.api.baseUrl")
        ?: "https://api.simpleshop.cz/v2"

    /**
     * SimpleShop user email for authentication
     */
    fun getEmail(): String? = appConfig.findConfig("simpleshop.api.email")

    /**
     * SimpleShop API key for authentication
     */
    fun getApiKey(): String? = appConfig.findConfig("simpleshop.api.key")

    /**
     * Check if SimpleShop integration is configured
     */
    fun isConfigured(): Boolean = !getEmail().isNullOrBlank() && !getApiKey().isNullOrBlank()
}
