package org.xbery.artbeams.common.access.service

import javax.servlet.http.HttpServletRequest
import org.xbery.artbeams.common.access.domain.{EntityKey, UserAccessReport}

/**
  * Operations for user accesses.
  * @author Radek Beran
  */
trait UserAccessService {

  /**
    * Returns report with with detected capabilities of the client.
    * @param request
    * @return
    */
  def getUserAccessReport(request: HttpServletRequest): UserAccessReport

  /**
    * Saves new user access record if it was accepted as an unique human access. Returns report with an optionally
    * stored user access (if it is and unique and non-bot/crawler access) and with detected capabilities of the client.
    * @param entityKey identification of accessed entity
    * @param request HTTP request
    * @return report about user access
    */
  def saveUserAccess(entityKey: EntityKey, request: HttpServletRequest): UserAccessReport

  def findCountOfVisits(entityKey: EntityKey): Long

  def aggregateUserAccesses(): Unit
}
