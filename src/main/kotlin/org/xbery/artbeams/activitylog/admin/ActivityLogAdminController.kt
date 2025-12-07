package org.xbery.artbeams.activitylog.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.activitylog.domain.ActionType
import org.xbery.artbeams.activitylog.domain.EntityType
import org.xbery.artbeams.activitylog.service.UserActivityLogService
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.overview.Pagination
import java.time.LocalDate
import java.time.ZoneId

/**
 * Controller for administering user activity logs.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/activity-logs")
class ActivityLogAdminController(
    private val activityLogService: UserActivityLogService,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping
    fun activityLogList(
        request: HttpServletRequest,
        @RequestParam(required = false) offset: Int?,
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) actionType: String?,
        @RequestParam(required = false) entityType: String?,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): Any {
        return tryOrErrorResponse(request) {
            val pagination = Pagination(offset ?: 0, limit ?: 50)

            // Parse filter parameters
            val actionTypeEnum = actionType?.let { ActionType.fromValue(it) }
            val entityTypeEnum = entityType?.let { EntityType.fromValue(it) }
            val startTime = startDate?.let { LocalDate.parse(it).atStartOfDay(ZoneId.systemDefault()).toInstant() }
            val endTime = endDate?.let { LocalDate.parse(it).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() }

            val resultPage = activityLogService.findActivityLogs(
                pagination = pagination,
                userId = userId?.takeIf { it.isNotBlank() },
                actionType = actionTypeEnum,
                entityType = entityTypeEnum,
                startTime = startTime,
                endTime = endTime
            )

            val model = createModel(
                request,
                "resultPage" to resultPage,
                "actionTypes" to ActionType.entries.map { it.value },
                "entityTypes" to EntityType.entries.map { it.value },
                "selectedUserId" to (userId ?: ""),
                "selectedActionType" to (actionType ?: ""),
                "selectedEntityType" to (entityType ?: ""),
                "selectedStartDate" to (startDate ?: ""),
                "selectedEndDate" to (endDate ?: "")
            )

            ModelAndView("admin/activityLogList", model)
        }
    }
}
