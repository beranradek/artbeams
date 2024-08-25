package org.xbery.artbeams.evernote.config

import org.springframework.stereotype.Component
import org.xbery.artbeams.config.repository.AppConfig


/**
 * Configuration of Evernote API.
 * @author Radek Beran
 */
@Component
open class EvernoteConfig(private val appConfig: AppConfig) {
    // Real applications authenticate with Evernote using OAuth, but for the
    // purpose of exploring the API, you can get a developer token that allows
    // you to access your own Evernote account. To get a developer token, visit
    // https://www.evernote.com/api/DeveloperToken.action
    fun getDeveloperToken(): String =
        appConfig.requireConfig("evernote.developer-token")
}
