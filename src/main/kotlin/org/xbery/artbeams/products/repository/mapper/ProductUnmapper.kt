package org.xbery.artbeams.products.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.jooq.schema.tables.records.ProductsRecord
import org.xbery.artbeams.jooq.schema.tables.references.PRODUCTS
import org.xbery.artbeams.products.domain.Product

/**
 * @author Radek Beran
 */
@Component
class ProductUnmapper : RecordUnmapper<Product, ProductsRecord> {

    override fun unmap(product: Product): ProductsRecord {
        val record = PRODUCTS.newRecord()
        record.id = product.common.id
        record.created = product.common.created
        record.createdBy = product.common.createdBy
        record.modified = product.common.modified
        record.modifiedBy = product.common.modifiedBy
        record.slug = product.slug
        record.title = product.title
        record.subtitle = product.subtitle
        record.filename = product.fileName
        record.listingImage = product.listingImage
        record.image = product.image
        record.confirmationMailingGroupId = product.confirmationMailingGroupId
        record.mailingGroupId = product.mailingGroupId
        record.priceRegular = product.priceRegular.price
        record.priceDiscounted = product.priceDiscounted?.price
        return record
    }
}
