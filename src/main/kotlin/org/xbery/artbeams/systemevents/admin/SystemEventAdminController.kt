package org.xbery.artbeams.systemevents.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.systemevents.domain.SystemEventSeverity
import org.xbery.artbeams.systemevents.domain.SystemEventType
import org.xbery.artbeams.systemevents.service.SystemEventLogService
import java.time.LocalDate
import java.time.ZoneId
import jakarta.servlet.http.HttpServletRequest

/**
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/system-events")
class SystemEventAdminController(
    private val systemEventLogService: SystemEventLogService,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping
    fun list(
        request: HttpServletRequest,
        @RequestParam(required = false) offset: Int?,
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) severity: String?,
        @RequestParam(required = false) eventType: String?,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): Any = tryOrErrorResponse(request) {
        val pagination = Pagination(offset ?: 0, limit ?: 50)
        val severityEnum = severity?.takeIf { it.isNotBlank() }?.let { SystemEventSeverity.valueOf(it.uppercase()) }
        val eventTypeEnum = eventType?.takeIf { it.isNotBlank() }?.let { SystemEventType.valueOf(it.uppercase()) }
        val startTime = startDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it).atStartOfDay(ZoneId.systemDefault()).toInstant() }
        val endTime = endDate?.takeIf { it.isNotBlank() }?.let {
            LocalDate
                .parse(it)
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        }

        val resultPage = systemEventLogService.findEvents(pagination, severityEnum, eventTypeEnum, startTime, endTime)
        val model = createModel(
            request,
            "resultPage" to resultPage,
            "severities" to SystemEventSeverity.entries.map { it.name },
            "eventTypes" to SystemEventType.entries.map { it.name },
            "selectedSeverity" to (severity ?: ""),
            "selectedEventType" to (eventType ?: ""),
            "selectedStartDate" to (startDate ?: ""),
            "selectedEndDate" to (endDate ?: "")
        )
        ModelAndView("admin/systemEvents/systemEventList", model)
    }
}
