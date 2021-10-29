package org.xbery.artbeams.orders.repository

import org.xbery.artbeams.common.assets.repository.AssetFilter

/**
 * @author Radek Beran
 */
data class OrderFilter(override val id: String?, override val ids: List<String>?, override val createdBy: String?) : AssetFilter