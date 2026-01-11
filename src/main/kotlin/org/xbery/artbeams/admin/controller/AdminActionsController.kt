package org.xbery.artbeams.admin.controller

import jakarta.annotation.PreDestroy
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.common.security.credential.Pbkdf2PasswordHash
import org.xbery.artbeams.common.security.credential.Pbkdf2PasswordHash.Companion.PBKDF2_HMAC_SHA512_ITERATIONS
import org.xbery.artbeams.search.service.SearchIndexer
import org.xbery.artbeams.users.repository.UserRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Admin actions controller for various administrative operations.
 *
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/actions")
class AdminActionsController(
    private val searchIndexer: SearchIndexer,
    private val userRepository: UserRepository,
    common: ControllerComponents
) : BaseController(common) {

    companion object {
        private const val ADMIN_LOGIN = "admin"
        private const val DEFAULT_ADMIN_PASSWORD = "adminadmin"
    }

    private val backgroundExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    @PreDestroy
    fun cleanup() {
        logger.info("Shutting down AdminActionsController background executor")
        backgroundExecutor.shutdown()
        try {
            if (!backgroundExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate in time, forcing shutdown")
                backgroundExecutor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            logger.error("Interrupted during executor shutdown", e)
            backgroundExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

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

    /**
     * Reset the admin user password to the default value (adminadmin).
     * This action is useful for local development when the admin password was changed
     * (e.g., after syncing from production) and needs to be restored.
     */
    @PostMapping("/reset-admin-password")
    fun resetAdminPassword(request: HttpServletRequest): Any {
        logger.info("Reset admin password action triggered by admin user")

        return try {
            val adminUser = userRepository.findByLogin(ADMIN_LOGIN)
            if (adminUser == null) {
                logger.error("Admin user not found")
                return redirectToReferrerWitParam(request, "adminPasswordResetError", "1")
            }

            val passwordHash = Pbkdf2PasswordHash()
            val newPasswordCredential = passwordHash.encodeToSerializedCredential(
                DEFAULT_ADMIN_PASSWORD,
                PBKDF2_HMAC_SHA512_ITERATIONS
            )

            val updatedUser = adminUser.copy(password = newPasswordCredential)
            userRepository.update(updatedUser)

            logger.info("Admin user password has been reset to default value")
            redirectToReferrerWitParam(request, "adminPasswordReset", "1")
        } catch (e: Exception) {
            logger.error("Failed to reset admin password: ${e.message}", e)
            redirectToReferrerWitParam(request, "adminPasswordResetError", "1")
        }
    }
}
