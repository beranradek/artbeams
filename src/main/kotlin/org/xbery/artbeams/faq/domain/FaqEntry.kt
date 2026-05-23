package org.xbery.artbeams.faq.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.repository.IdentifiedEntity

/**
 * @author Radek Beran
 */
data class FaqEntry(
    val common: AssetAttributes,
    val entityType: FaqEntityType,
    val entityId: String,
    val question: String,
    val answer: String,
    val sortOrder: Int = 0
) : IdentifiedEntity {
    override val id: String get() = common.id

    companion object {
        const val CACHE_NAME: String = "faqEntries"
        const val HOMEPAGE_ENTITY_ID: String = "homepage"
    }
}
