package org.xbery.artbeams.courses.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
 * Course entity representing a collection of modules.
 */
data class Course(
    override val common: AssetAttributes,
    val slug: String,
    val title: String,
    val subtitle: String?,
    /** Image for course listing */
    val listingImage: String?,
    /** Image for course detail */
    val image: String?,
    /** Short introduction / perex for the course */
    val perex: String?,
    /** Ordered list of modules within the course */
    val modules: List<Module>
) : Asset()
