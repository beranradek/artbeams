package org.xbery.artbeams.products.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.jooq.schema.tables.records.ProductsRecord
import org.xbery.artbeams.products.domain.Product

/**
 * @author Radek Beran
 */
@Component
class ProductMapper : RecordMapper<ProductsRecord, Product> {

    override fun map(record: ProductsRecord): Product {
        return Product(
            common = AssetAttributes(
                id = requireNotNull(record.id),
                created = requireNotNull(record.created),
                createdBy = requireNotNull(record.createdBy),
                modified = requireNotNull(record.modified),
                modifiedBy = requireNotNull(record.modifiedBy)
            ),
            slug = requireNotNull(record.slug),
            title = requireNotNull(record.title),
            subtitle = record.subtitle,
            fileName = record.filename,
            listingImage = record.listingImage,
            image = record.image,
            confirmationMailingGroupId = record.confirmationMailingGroupId,
            mailingGroupId = record.mailingGroupId
        )
    }
}
