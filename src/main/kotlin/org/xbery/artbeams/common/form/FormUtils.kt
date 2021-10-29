package org.xbery.artbeams.common.form

import net.formio.Forms
import net.formio.format.Location

/**
 * API for form definition and processing.
 * @author Radek Beran
 */
object FormUtils {
    const val DateTimePattern = "d.M.yyyy HH:mm"

    private fun configBuilder() = Forms.config().defaultInstantiator(DataClassInstantiator())

    val CzConfig = configBuilder().location(Location.CZECH).build()
}
