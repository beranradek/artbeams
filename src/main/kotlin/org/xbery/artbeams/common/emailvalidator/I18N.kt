package org.xbery.artbeams.common.emailvalidator

import java.text.MessageFormat
import java.util.*
import java.util.logging.Logger

/**
 * Created by TPa on 10.07.18.
 */
object I18N {
    private val LOG: Logger = Logger.getLogger(I18N::class.java.name)

    fun getTranslation(key: String, locale: Locale): String {
        val bundle = ResourceBundle.getBundle("messages", locale)
        if (bundle.containsKey(key)) {
            return MessageFormat.format(bundle.getString(key))
        } else {
            LOG.warning("key $key not found in messages bundle")
            return key
        }
    }

    fun getTranslation(key: String, locale: Locale, vararg params: Any?): String {
        val bundle = ResourceBundle.getBundle("messages", locale)
        return getTranslation(key, bundle, *params)
    }

    @JvmStatic
	fun getTranslation(key: String, bundle: ResourceBundle, vararg params: Any?): String {
        if (bundle.containsKey(key)) {
            return MessageFormat.format(bundle.getString(key), *params)
        } else {
            LOG.warning("key $key not found in messages bundle")
            return key
        }
    }
}
