package org.xbery.artbeams.google.searchconsole

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.error.StatusCode
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.error.OperationException
import org.xbery.artbeams.google.auth.GoogleApiAuth
import org.xbery.artbeams.google.error.GoogleErrorCode
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Google Search Console API service using Java HttpClient.
 * 
 * Provides access to search performance data, indexing status, and sitemap information.
 * Uses Google Webmasters (Search Console) v3 REST API.
 * 
 * NOTE: This implementation requires proper Google OAuth2 authorization with webmasters.readonly scope.
 *
 * @author Radek Beran
 */
@Service
open class GoogleSearchConsoleService(
    private val googleAuth: GoogleApiAuth,
    private val appConfig: AppConfig,
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    open val scopes: List<String> = listOf("https://www.googleapis.com/auth/webmasters.readonly")

    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val apiBaseUrl = "https://www.googleapis.com/webmasters/v3"

    /**
     * Returns the site URL configured for Search Console.
     */
    private fun getSiteUrl(): String {
        val baseUrl = appConfig.findConfig("web.baseUrl") ?: "http://localhost:8080"
        return baseUrl.trimEnd('/')
    }

    /**
     * Fetches overall search performance metrics for the specified date range.
     */
    @Cacheable(value = ["searchConsoleMetrics"], key = "#startDate + '-' + #endDate")
    open fun getSearchMetrics(startDate: LocalDate, endDate: LocalDate): SearchConsoleMetrics {
        logger.info("Fetching Search Console metrics from $startDate to $endDate")
        
        if (!isAuthorized()) {
            throw OperationException(
                GoogleErrorCode.UNAUTHORIZED,
                "Unauthorized access to Google Search Console",
                StatusCode.UNAUTHORIZED
            )
        }
        
        val siteUrl = getSiteUrl()
        val encodedSiteUrl = URLEncoder.encode(siteUrl, "UTF-8")
        val accessToken = getAccessToken()
        
        val requestBody = mapOf(
            "startDate" to startDate.format(dateFormatter),
            "endDate" to endDate.format(dateFormatter),
            "dimensions" to emptyList<String>()
        )
        
        try {
            val url = "$apiBaseUrl/sites/$encodedSiteUrl/searchAnalytics/query"
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer $accessToken")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build()
            
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            val statusCode = response.statusCode()
            val responseBody = response.body()
            
            if (statusCode != 200) {
                logger.error("Search Console API error: $statusCode - $responseBody")
                throw OperationException(
                    GoogleErrorCode.API_ERROR,
                    "Search Console API returned error: $statusCode",
                    StatusCode.INTERNAL_ERROR
                )
            }
            
            val result: Map<String, Any> = objectMapper.readValue(responseBody)
            val rows = result["rows"] as? List<Map<String, Any>>
            
            if (rows.isNullOrEmpty()) {
                logger.info("No search metrics data available for date range")
                return SearchConsoleMetrics(
                    impressions = 0,
                    clicks = 0,
                    ctr = 0.0,
                    position = 0.0,
                    startDate = startDate,
                    endDate = endDate
                )
            }
            
            val row = rows[0]
            return SearchConsoleMetrics(
                impressions = (row["impressions"] as? Number)?.toLong() ?: 0,
                clicks = (row["clicks"] as? Number)?.toLong() ?: 0,
                ctr = (row["ctr"] as? Number)?.toDouble() ?: 0.0,
                position = (row["position"] as? Number)?.toDouble() ?: 0.0,
                startDate = startDate,
                endDate = endDate
            )
        } catch (e: OperationException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to fetch search metrics: ${e.message}", e)
            throw OperationException(
                GoogleErrorCode.API_ERROR,
                "Failed to fetch search metrics: ${e.message}",
                StatusCode.INTERNAL_ERROR
            )
        }
    }

    /**
     * Fetches top performing pages for the specified date range.
     */
    @Cacheable(value = ["searchConsolePages"], key = "#startDate + '-' + #endDate + '-' + #limit")
    open fun getTopPages(startDate: LocalDate, endDate: LocalDate, limit: Int = 10): List<SearchConsolePageMetrics> {
        logger.info("Fetching top $limit pages from $startDate to $endDate")
        
        if (!isAuthorized()) {
            return emptyList()
        }
        
        val siteUrl = getSiteUrl()
        val encodedSiteUrl = URLEncoder.encode(siteUrl, "UTF-8")
        val accessToken = getAccessToken()
        
        val requestBody = mapOf(
            "startDate" to startDate.format(dateFormatter),
            "endDate" to endDate.format(dateFormatter),
            "dimensions" to listOf("page"),
            "rowLimit" to limit
        )
        
        try {
            val url = "$apiBaseUrl/sites/$encodedSiteUrl/searchAnalytics/query"
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer $accessToken")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build()
            
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            val statusCode = response.statusCode()
            val responseBody = response.body()
            
            if (statusCode != 200) {
                logger.error("Search Console API error: $statusCode - $responseBody")
                return emptyList()
            }
            
            val result: Map<String, Any> = objectMapper.readValue(responseBody)
            val rows = result["rows"] as? List<Map<String, Any>> ?: return emptyList()
            
            return rows.map { row ->
                val keys = row["keys"] as? List<String>
                SearchConsolePageMetrics(
                    page = keys?.getOrNull(0) ?: "",
                    impressions = (row["impressions"] as? Number)?.toLong() ?: 0,
                    clicks = (row["clicks"] as? Number)?.toLong() ?: 0,
                    ctr = (row["ctr"] as? Number)?.toDouble() ?: 0.0,
                    position = (row["position"] as? Number)?.toDouble() ?: 0.0
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch top pages: ${e.message}", e)
            return emptyList()
        }
    }

    /**
     * Fetches top performing queries for the specified date range.
     */
    @Cacheable(value = ["searchConsoleQueries"], key = "#startDate + '-' + #endDate + '-' + #limit")
    open fun getTopQueries(startDate: LocalDate, endDate: LocalDate, limit: Int = 10): List<SearchConsoleQueryMetrics> {
        logger.info("Fetching top $limit queries from $startDate to $endDate")
        
        if (!isAuthorized()) {
            return emptyList()
        }
        
        val siteUrl = getSiteUrl()
        val encodedSiteUrl = URLEncoder.encode(siteUrl, "UTF-8")
        val accessToken = getAccessToken()
        
        val requestBody = mapOf(
            "startDate" to startDate.format(dateFormatter),
            "endDate" to endDate.format(dateFormatter),
            "dimensions" to listOf("query"),
            "rowLimit" to limit
        )
        
        try {
            val url = "$apiBaseUrl/sites/$encodedSiteUrl/searchAnalytics/query"
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer $accessToken")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build()
            
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            val statusCode = response.statusCode()
            val responseBody = response.body()
            
            if (statusCode != 200) {
                logger.error("Search Console API error: $statusCode - $responseBody")
                return emptyList()
            }
            
            val result: Map<String, Any> = objectMapper.readValue(responseBody)
            val rows = result["rows"] as? List<Map<String, Any>> ?: return emptyList()
            
            return rows.map { row ->
                val keys = row["keys"] as? List<String>
                SearchConsoleQueryMetrics(
                    query = keys?.getOrNull(0) ?: "",
                    impressions = (row["impressions"] as? Number)?.toLong() ?: 0,
                    clicks = (row["clicks"] as? Number)?.toLong() ?: 0,
                    ctr = (row["ctr"] as? Number)?.toDouble() ?: 0.0,
                    position = (row["position"] as? Number)?.toDouble() ?: 0.0
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch top queries: ${e.message}", e)
            return emptyList()
        }
    }

    /**
     * Fetches sitemap submission status.
     */
    @Cacheable(value = ["searchConsoleSitemaps"])
    open fun getSitemaps(): List<SearchConsoleSitemapStatus> {
        logger.info("Fetching sitemap status")
        
        if (!isAuthorized()) {
            return emptyList()
        }
        
        val siteUrl = getSiteUrl()
        val encodedSiteUrl = URLEncoder.encode(siteUrl, "UTF-8")
        val accessToken = getAccessToken()
        
        try {
            val url = "$apiBaseUrl/sites/$encodedSiteUrl/sitemaps"
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer $accessToken")
                .GET()
                .build()
            
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            val statusCode = response.statusCode()
            val responseBody = response.body()
            
            if (statusCode != 200) {
                logger.error("Search Console API error: $statusCode - $responseBody")
                return emptyList()
            }
            
            val result: Map<String, Any> = objectMapper.readValue(responseBody)
            val sitemaps = result["sitemap"] as? List<Map<String, Any>> ?: return emptyList()
            
            return sitemaps.map { sitemap ->
                SearchConsoleSitemapStatus(
                    path = sitemap["path"] as? String ?: "",
                    isPending = sitemap["isPending"] as? Boolean ?: false,
                    isSitemapsIndex = sitemap["isSitemapsIndex"] as? Boolean ?: false,
                    lastSubmitted = sitemap["lastSubmitted"] as? String,
                    lastDownloaded = sitemap["lastDownloaded"] as? String,
                    warnings = (sitemap["warnings"] as? Number)?.toLong() ?: 0,
                    errors = (sitemap["errors"] as? Number)?.toLong() ?: 0
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to fetch sitemaps: ${e.message}", e)
            return emptyList()
        }
    }

    /**
     * Checks if user is authorized to access Search Console data.
     */
    open fun isAuthorized(): Boolean {
        return googleAuth.isUserAuthorized(scopes)
    }

    /**
     * Returns authorization URL for Search Console access.
     */
    open fun getAuthorizationUrl(returnUrl: String): String {
        return googleAuth.startAuthorizationFlow(scopes, returnUrl)
    }

    /**
     * Returns access token for API calls.
     */
    private fun getAccessToken(): String {
        val credentials = googleAuth.getCredentials(scopes)
        return credentials.accessToken ?: throw OperationException(
            GoogleErrorCode.UNAUTHORIZED,
            "No access token available",
            StatusCode.UNAUTHORIZED
        )
    }
}
