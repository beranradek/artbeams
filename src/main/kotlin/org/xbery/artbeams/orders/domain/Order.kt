package org.xbery.artbeams.orders.domain

import org.xbery.artbeams.common.assets.domain.Asset
import org.xbery.artbeams.common.assets.domain.AssetAttributes

/**
 * Order entity.
 * @author Radek Beran
 */
data class Order(
    override val common: AssetAttributes,
    val items: List<OrderItem>
) : Asset()
