package org.xbery.artbeams.common.assets.domain

import java.util.*

/**
 * @author Radek Beran
 */
interface EditedTimeValidity {
    val validFrom: Date
    val validTo: Date?
}
