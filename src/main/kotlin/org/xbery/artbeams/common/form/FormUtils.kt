package org.xbery.artbeams.common.form

import net.formio.Config
import net.formio.Forms
import net.formio.format.Location

/**
 * API for form definition and processing.
 * @author Radek Beran
 */
object FormUtils {
    const val DATE_TIME_PATTERN = "d.M.yyyy HH:mm"

    private fun configBuilder() = Forms.config().defaultInstantiator(DataClassInstantiator())

    val CZ_CONFIG: Config = configBuilder().location(Location.CZECH).build()
}
