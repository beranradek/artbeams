package org.xbery.artbeams.common.assets.domain

import java.time.Instant

/**
 * Mixin for content entities with valid from and valid to attributes.
 *
 * @author Radek Beran
 */
interface ValidityAsset {
    val validity: Validity
    val validFrom: Instant get() = validity.validFrom
    val validTo: Instant? get() = validity.validTo
}
