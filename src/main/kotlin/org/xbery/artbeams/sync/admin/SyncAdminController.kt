package org.xbery.artbeams.sync.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.sync.service.RemoteDatabaseSyncService

/**
 * Controller for syncing content from remote database.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/sync")
open class SyncAdminController(
    private val syncService: RemoteDatabaseSyncService,
    private val common: ControllerComponents
) : BaseController(common) {
    private val TplBasePath: String = "admin/sync"

    @GetMapping("/confirm")
    fun confirmSync(request: HttpServletRequest): Any {
        if (!syncService.isRemoteDbConfigured()) {
            val model = createModel(
                request,
                "errorMessage" to "Remote database connection is not configured (remote.db.connection config value is missing)"
            )
            return ModelAndView("$TplBasePath/syncError", model)
        }

        val model = createModel(request)
        return ModelAndView("$TplBasePath/syncConfirm", model)
    }

    @PostMapping("/execute")
    fun executeSync(request: HttpServletRequest): Any {
        if (!syncService.isRemoteDbConfigured()) {
            val model = createModel(
                request,
                "errorMessage" to "Remote database connection is not configured"
            )
            return ModelAndView("$TplBasePath/syncError", model)
        }

        val result = syncService.syncFromRemoteDatabase()

        val model = createModel(
            request,
            "result" to result
        )

        return ModelAndView("$TplBasePath/syncResult", model)
    }
}
