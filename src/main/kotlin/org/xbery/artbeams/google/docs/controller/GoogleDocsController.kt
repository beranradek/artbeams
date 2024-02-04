package org.xbery.artbeams.google.docs.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
class GoogleDocsController(private val googleDocsService: GoogleDocsService, private val googleAuth: GoogleApiAuth) {

    /**
     * Redirects to Google authorization URL if not authorized.
     */
    @GetMapping("/authorization")
    fun authorization(): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType("text", "plain", StandardCharsets.UTF_8)

        if (!googleAuth.isUserAuthorized(googleDocsService.scopes)) {
            headers.location = URI.create(googleAuth.getAuthorizationUrl(googleDocsService.scopes))
            return ResponseEntity<String>(headers, HttpStatus.SEE_OTHER)
        }

        return ResponseEntity<String>("Already authorized", headers, HttpStatus.OK)
    }

    @GetMapping("/content/{documentId}")
    fun readFromGoogleDocs(@PathVariable documentId: String): ResponseEntity<String> {
        val content = googleDocsService.readGoogleDoc(documentId)

        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType("text", "plain", StandardCharsets.UTF_8)
        return ResponseEntity<String>("Text content of Google Doc:\n$content", httpHeaders, HttpStatus.OK)
    }
}
