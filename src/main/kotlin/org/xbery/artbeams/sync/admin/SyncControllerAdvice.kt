package org.xbery.artbeams.sync.admin

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import org.xbery.artbeams.sync.service.RemoteDatabaseSyncService

/**
 * Controller advice to add remote database sync configuration status to all admin pages.
 * @author Radek Beran
 */
@ControllerAdvice(basePackages = ["org.xbery.artbeams"])
class SyncControllerAdvice(
    private val syncService: RemoteDatabaseSyncService
) {
    @ModelAttribute("_isRemoteDbConfigured")
    fun isRemoteDbConfigured(): Boolean {
        return syncService.isRemoteDbConfigured()
    }
}
