package org.xbery.artbeams.products.service

import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.products.domain.EditedProduct
import org.xbery.artbeams.products.domain.Product

/**
 * @author Radek Beran
 */
interface ProductService {
    fun findProducts(): List<Product>
    fun saveProduct(edited: EditedProduct, ctx: OperationCtx): Product?
    fun findBySlug(slug: String): Product?
}
