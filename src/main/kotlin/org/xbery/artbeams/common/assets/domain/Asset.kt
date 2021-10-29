package org.xbery.artbeams.common.assets.domain

import java.io.Serializable
import java.time.Instant

/**
 * Common superclass of all content entities.
 *
 * @author Radek Beran
 */
abstract class Asset() : Serializable {
    abstract val common: AssetAttributes
    val id: String get() = common.id
    val created: Instant get() = common.created
    val createdBy: String get() = common.createdBy
    val modified: Instant get() = common.modified
    val modifiedBy: String get() = common.modifiedBy
}
