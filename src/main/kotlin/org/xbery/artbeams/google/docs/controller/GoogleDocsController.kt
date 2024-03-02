package org.xbery.artbeams.google.docs.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.xbery.artbeams.common.Urls
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.google.auth.GoogleApiAuth
import org.xbery.artbeams.google.docs.GoogleDocsService
import java.net.URI
import java.nio.charset.StandardCharsets

/**
 * API for firing authorization of application, so it can access Google Documents.
 * Auxiliary endpoint to retrieve text content of given Google Document.
 *
 * @author Radek Beran
 */
@RestController
@RequestMapping("/admin/google-docs")
class GoogleDocsController(
    private val googleDocsService: GoogleDocsService,
    private val googleAuth: GoogleApiAuth,
    private val common: ControllerComponents): BaseController(common) {

    /**
     * Redirects to Google authorization URL if not authorized.
     */
    @GetMapping("/authorization")
    fun authorization(request: HttpServletRequest): ResponseEntity<String> {
        val headers = HttpHeaders()
        val returnUrl = getFullUrl(request)
        if (!googleAuth.isUserAuthorized(googleDocsService.scopes)) {
            // Redirect to authorization URL with final return back to returnUrl (referrer)
            val authorizationUrl = googleAuth.startAuthorizationFlow(googleDocsService.scopes, returnUrl)
            headers.location = URI.create(authorizationUrl)
            return ResponseEntity<String>(headers, HttpStatus.SEE_OTHER)
        }
        headers.location = URI.create(Urls.urlWithParam(returnUrl, "alreadyAuthorized", "1"))
        return ResponseEntity<String>(headers, HttpStatus.SEE_OTHER)
    }

    @GetMapping("/content/{documentId}")
    fun readFromGoogleDocs(@PathVariable documentId: String): ResponseEntity<String> {
        val content = googleDocsService.readGoogleDoc(documentId)

        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType("text", "plain", StandardCharsets.UTF_8)
        return ResponseEntity<String>("Text content of Google Doc:\n$content", httpHeaders, HttpStatus.OK)
    }
}
