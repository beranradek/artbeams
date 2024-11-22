package org.xbery.artbeams.common.context

import java.time.Instant


/**
 * Stamp of origin of some information.
 * This simple data object is used to group fields
 * describing source of some information.
 *
 * @author Radek Beran
 */
data class OriginStamp(
    /**
     * Time when the information was stored in the system.
     */
    val time: Instant,

    /**
     * Source system responsible for the information.
     * It is recommended to use short upper-case constants.
     * A concrete meaning is not strictly defined by this class.
     * It is possible to leave this value unset.
     */
    val origin: String,

    /**
     * User/actor responsible for the information.
     * A typical value is login or some identifier of a user.
     * For automated actors, the value may be some other specification
     * of the source system, or it may remain unset.
     */
    val principal: String?
) {

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Stamp[time=").append(time)
        builder.append(", origin=").append(origin)
        principal?.let { builder.append(", principal=").append(it) }
        builder.append("]")
        return builder.toString()
    }
}
