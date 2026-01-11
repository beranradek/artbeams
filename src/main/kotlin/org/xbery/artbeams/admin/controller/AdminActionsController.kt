package org.xbery.artbeams.admin.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.search.service.SearchIndexer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Admin actions controller for various administrative operations.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/actions")
class AdminActionsController(
    private val searchIndexer: SearchIndexer,
    common: ControllerComponents
) : BaseController(common) {

    private val backgroundExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * Trigger a full reindex of the search index in the background.
     */
    @PostMapping("/reindex")
    fun reindex(request: HttpServletRequest): Any {
        logger.info("Reindex all action triggered by admin user")

        // Launch reindex in background thread
        backgroundExecutor.submit {
            try {
                logger.info("Starting background reindex")
                searchIndexer.reindexAll()
                logger.info("Background reindex completed successfully")
            } catch (e: Exception) {
                logger.error("Background reindex failed: ${e.message}", e)
            }
        }

        return redirectToReferrerWitParam(request, "reindexStarted", "1")
    }
}
