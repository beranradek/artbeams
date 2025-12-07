package org.xbery.artbeams.users.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.xbery.artbeams.activitylog.domain.ActionType
import org.xbery.artbeams.activitylog.domain.EntityType
import org.xbery.artbeams.activitylog.service.UserActivityLogService
import org.xbery.artbeams.users.repository.UserRepository

/**
 * Custom authentication success handler that logs user login activities.
 *
 * @author Radek Beran
 */
@Component
class LoginActivityHandler(
    private val activityLogService: UserActivityLogService,
    private val userRepository: UserRepository
) : SavedRequestAwareAuthenticationSuccessHandler() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // Log the login activity
        try {
            val principal = authentication.name
            // Principal format is "userId:login" from LoginVerificationServiceImpl
            val userId = principal.substringBefore(":")
            val username = principal.substringAfter(":")
            
            val user = userRepository.findById(userId)

            if (user != null) {
                activityLogService.logActivity(
                    userId = user.id,
                    actionType = ActionType.LOGIN,
                    entityType = EntityType.USER,
                    entityId = user.id,
                    ipAddress = request.remoteAddr,
                    userAgent = request.getHeader("User-Agent")
                )
                logger.debug("Logged login activity for user: $username")
            } else {
                logger.warn("User not found for login: $username (userId: $userId)")
            }
        } catch (e: Exception) {
            logger.error("Failed to log login activity", e)
            // Don't fail the login if activity logging fails
        }

        // Continue with default behavior (redirect to saved request or default success URL)
        super.onAuthenticationSuccess(request, response, authentication)
    }
}
