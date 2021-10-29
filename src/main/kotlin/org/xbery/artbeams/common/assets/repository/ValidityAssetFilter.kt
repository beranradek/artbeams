package org.xbery.artbeams.common.assets.repository

import java.time.Instant

/**
 * Mixin for filters with valid from and valid to attributes.
 * @author Radek Beran
 */
interface ValidityAssetFilter {
    val validityDate: Instant?
}
