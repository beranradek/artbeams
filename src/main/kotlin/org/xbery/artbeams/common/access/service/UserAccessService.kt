package org.xbery.artbeams.common.access.service

import org.xbery.artbeams.common.access.domain.EntityKey
import org.xbery.artbeams.common.access.domain.UserAccessReport
import jakarta.servlet.http.HttpServletRequest

/**
 * Operations for user accesses.
 * @author Radek Beran
 */
interface UserAccessService {
    /**
     * Returns report with with detected capabilities of the client.
     * @param request
     * @return
     */
    fun getUserAccessReport(request: HttpServletRequest): UserAccessReport

    /**
     * Saves new user access record if it was accepted as an unique human access. Returns report with an optionally
     * stored user access (if it is and unique and non-bot/crawler access) and with detected capabilities of the client.
     * @param entityKey identification of accessed entity
     * @param request HTTP request
     * @return report about user access
     */
    fun saveUserAccess(entityKey: EntityKey, request: HttpServletRequest): UserAccessReport

    fun findCountOfVisits(entityKey: EntityKey): Long

    fun aggregateUserAccesses(): Unit
}
