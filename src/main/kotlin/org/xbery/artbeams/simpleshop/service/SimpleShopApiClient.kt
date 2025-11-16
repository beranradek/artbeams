package org.xbery.artbeams.simpleshop.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.simpleshop.config.SimpleShopConfig
import org.xbery.artbeams.simpleshop.domain.SimpleShopProduct
import java.math.BigDecimal
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*

/**
 * Client for SimpleShop.cz API.
 * @author Radek Beran
 */
@Service
class SimpleShopApiClient(
    private val config: SimpleShopConfig,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    /**
     * Fetch product data from SimpleShop API.
     * @param productId SimpleShop product ID
     * @return Product data or null if not found or error occurred
     */
    fun getProduct(productId: String): SimpleShopProduct? {
        if (!config.isConfigured()) {
            logger.warn("SimpleShop API is not configured. Please set simpleshop.api.email and simpleshop.api.key in config.")
            return null
        }

        try {
            val url = "${config.getApiBaseUrl()}/product/$productId/"
            val authHeader = createBasicAuthHeader(config.getEmail()!!, config.getApiKey()!!)

            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build()

            logger.info("Fetching product from SimpleShop API: $productId")
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 200) {
                return parseProductResponse(response.body(), productId)
            } else {
                logger.error("Failed to fetch product from SimpleShop. Status: ${response.statusCode()}, Body: ${response.body()}")
                return null
            }
        } catch (e: Exception) {
            logger.error("Error fetching product from SimpleShop: ${e.message}", e)
            return null
        }
    }

    private fun createBasicAuthHeader(email: String, apiKey: String): String {
        val credentials = "$email:$apiKey"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())
        return "Basic $encodedCredentials"
    }

    private fun parseProductResponse(json: String, productId: String): SimpleShopProduct? {
        try {
            val rootNode = objectMapper.readTree(json)

            // SimpleShop API returns array with single product
            val productNode = if (rootNode.isArray && rootNode.size() > 0) {
                rootNode[0]
            } else {
                rootNode
            }

            val id = productNode.get("id")?.asText() ?: productId
            val name = productNode.get("name")?.asText() ?: ""
            val title = productNode.get("title")?.asText()
            val type = productNode.get("type")?.asText()

            // Price might be in different formats, try to extract it
            val price = extractPrice(productNode)

            return SimpleShopProduct(
                id = id,
                name = name,
                title = title,
                price = price,
                type = type
            )
        } catch (e: Exception) {
            logger.error("Error parsing SimpleShop product response: ${e.message}", e)
            return null
        }
    }

    private fun extractPrice(productNode: JsonNode): BigDecimal? {
        // Try direct price field
        productNode.get("price")?.let { priceNode ->
            if (priceNode.isNumber) {
                return priceNode.decimalValue()
            }
            if (priceNode.isTextual) {
                return priceNode.asText().toBigDecimalOrNull()
            }
        }

        // Try price in variants (first variant)
        productNode.get("variants")?.let { variants ->
            if (variants.isArray && variants.size() > 0) {
                val firstVariant = variants[0]
                firstVariant.get("price")?.let { variantPrice ->
                    if (variantPrice.isNumber) {
                        return variantPrice.decimalValue()
                    }
                    if (variantPrice.isTextual) {
                        return variantPrice.asText().toBigDecimalOrNull()
                    }
                }
            }
        }

        return null
    }
}
