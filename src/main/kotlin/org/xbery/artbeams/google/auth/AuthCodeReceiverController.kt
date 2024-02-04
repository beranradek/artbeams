package org.xbery.artbeams.google.auth

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

/**
 * Google API OAuth2 authorization code receiving endpoint.
 *
 * @author Radek Beran
 */
@RestController
@RequestMapping("/admin/google/auth")
class AuthCodeReceiverController(private val googleAuth: GoogleApiAuth) {

    @GetMapping("/callback")
    fun receiveAuthCode(
        @RequestParam(value = "code", required = false) code: String?,
        @RequestParam(value = "error", required = false) error: String?
    ): ResponseEntity<String> {
        try {
            // TODO: Validate code input, e.g. 4%2F0AfJohXnaRaPO1os9nfXxUDYUzgF6_L8VBr9KyIBqUxkweqDM0CClhAUM6roe2T-0YsvROg
            if (code != null && code.length > 60) {
                googleAuth.authCodeServerReceiver?.code = code
            }
            if (!error.isNullOrEmpty()) {
                googleAuth.authCodeServerReceiver?.error = error
            }

            val headers = HttpHeaders()
            headers.contentType = MediaType("text", "plain", StandardCharsets.UTF_8)
            if (error != null) {
                return ResponseEntity<String>("Token not received: $error", headers, HttpStatus.OK)
            } else if (code != null) {
                return ResponseEntity<String>("OAuth 2.0 Authentication Token Received", headers, HttpStatus.OK)
            } else {
                return ResponseEntity<String>("No Authentication Token or Error Received", headers, HttpStatus.OK)
            }
        } finally {
            googleAuth.authCodeServerReceiver?.waitUnlessSignaled?.release()
        }
    }
}
