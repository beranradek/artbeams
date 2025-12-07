package org.xbery.artbeams.google.searchconsole

import java.time.LocalDate

/**
 * Domain model for Google Search Console metrics.
 *
 * @author Radek Beran
 */
data class SearchConsoleMetrics(
    val impressions: Long,
    val clicks: Long,
    val ctr: Double,
    val position: Double,
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class SearchConsolePageMetrics(
    val page: String,
    val impressions: Long,
    val clicks: Long,
    val ctr: Double,
    val position: Double
)

data class SearchConsoleQueryMetrics(
    val query: String,
    val impressions: Long,
    val clicks: Long,
    val ctr: Double,
    val position: Double
)

data class SearchConsoleSitemapStatus(
    val path: String,
    val isPending: Boolean,
    val isSitemapsIndex: Boolean,
    val lastSubmitted: String?,
    val lastDownloaded: String?,
    val warnings: Long,
    val errors: Long
)

data class SearchConsoleIndexingIssue(
    val severity: String,
    val category: String,
    val description: String,
    val affectedPages: Long
)
