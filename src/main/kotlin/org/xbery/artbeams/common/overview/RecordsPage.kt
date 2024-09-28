package org.xbery.artbeams.common.overview

/**
 * Page of returned records with pagination settings.
 *
 * @author Radek Beran
 */
open class RecordsPage<T>(
    val records: List<T>,
    /**
     * Pagination settings. Filled if this is result with pagination capability.
     */
    val pagination: Pagination
)
