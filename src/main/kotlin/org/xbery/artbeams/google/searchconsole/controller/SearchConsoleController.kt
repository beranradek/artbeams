package org.xbery.artbeams.google.searchconsole.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.google.searchconsole.GoogleSearchConsoleService
import java.time.LocalDate

/**
 * Controller for Google Search Console integration.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/search-console")
class SearchConsoleController(
    private val searchConsoleService: GoogleSearchConsoleService,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping
    fun index(
        request: HttpServletRequest,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): ModelAndView {
        logger.info("Search Console metrics page accessed")

        val model = createModel(request, "pageTitle" to "Search Console Metrics")

        // Check authorization
        if (!searchConsoleService.isAuthorized()) {
            model["authorized"] = false
            model["authUrl"] = searchConsoleService.getAuthorizationUrl("/admin/search-console")
            return ModelAndView("admin/searchConsole/index", model)
        }

        model["authorized"] = true

        // Parse date range (default to last 28 days)
        val end = if (!endDate.isNullOrBlank()) LocalDate.parse(endDate) else LocalDate.now().minusDays(1)
        val start = if (!startDate.isNullOrBlank()) LocalDate.parse(startDate) else end.minusDays(27)

        model["startDate"] = start.toString()
        model["endDate"] = end.toString()

        try {
            // Fetch metrics
            val metrics = searchConsoleService.getSearchMetrics(start, end)
            model["metrics"] = metrics

            // Fetch top pages
            val topPages = searchConsoleService.getTopPages(start, end, 10)
            model["topPages"] = topPages

            // Fetch top queries
            val topQueries = searchConsoleService.getTopQueries(start, end, 10)
            model["topQueries"] = topQueries

            // Fetch sitemaps
            val sitemaps = searchConsoleService.getSitemaps()
            model["sitemaps"] = sitemaps

            // Check for indexing issues
            val hasIndexingIssues = sitemaps.any { it.errors > 0 || it.warnings > 0 }
            model["hasIndexingIssues"] = hasIndexingIssues

        } catch (e: Exception) {
            logger.error("Failed to fetch Search Console data: ${e.message}", e)
            model["error"] = "Failed to fetch Search Console data: ${e.message}"
        }

        return ModelAndView("admin/searchConsole/index", model)
    }
}
