package org.xbery.artbeams.google.auth

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient.UriSpec
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import java.nio.charset.StandardCharsets

/**
 * Google API OAuth2 authorization code receiving endpoint.
 *
 * @author Radek Beran
 */
@RestController
@RequestMapping("/admin/google/auth")
class AuthCodeReceiverController(private val googleAuth: GoogleApiAuth, common: ControllerComponents): BaseController(common) {

    @GetMapping("/callback")
    fun receiveAuthCode(
        @RequestParam(value = "code", required = false) code: String?,
        @RequestParam(value = "error", required = false) error: String?
    ): Any {
        try {
            googleAuth.receiveAuthorizationCodeOrError(code, error)
            return if (error != null) {
                val headers = HttpHeaders()
                headers.contentType = MediaType("text", "plain", StandardCharsets.UTF_8)
                ResponseEntity<String>("Token not received: $error", headers, HttpStatus.OK)
            } else if (code != null) {
                val returnUrl = googleAuth.getReturnUrl()
                if (returnUrl != null) {
                    redirect(Urls.urlWithParam(returnUrl, "authorized", "1"))
                } else {
                    val headers = HttpHeaders()
                    headers.contentType = MediaType("text", "plain", StandardCharsets.UTF_8)
                    ResponseEntity<String>("OAuth 2.0 Authentication Token Received", headers, HttpStatus.OK)
                }
            } else {
                val headers = HttpHeaders()
                headers.contentType = MediaType("text", "plain", StandardCharsets.UTF_8)
                ResponseEntity<String>("No Authentication Token or Error Received", headers, HttpStatus.OK)
            }
        } finally {
            googleAuth.finishAuthorizationFlow()
        }
    }
}
