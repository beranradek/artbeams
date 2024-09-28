package org.xbery.artbeams.userproducts.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.jooq.schema.tables.references.PRODUCTS
import org.xbery.artbeams.jooq.schema.tables.references.USER_PRODUCT
import org.xbery.artbeams.userproducts.domain.UserProduct

/**
 * @author Radek Beran
 */
@Repository
internal class UserProductRepository(
    private val dsl: DSLContext
) {

    fun findUserProducts(userId: String): List<UserProduct> {
        return dsl.select(
            USER_PRODUCT.ID,
            PRODUCTS.TITLE
        )
            .from(USER_PRODUCT)
            .innerJoin(PRODUCTS).on(USER_PRODUCT.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(
                USER_PRODUCT.USER_ID.eq(userId)
            )
            .orderBy(USER_PRODUCT.CREATED.desc()) // from newest to oldest
            .fetch { record ->
                UserProduct(
                    id = requireNotNull(record[USER_PRODUCT.ID]),
                    productName = requireNotNull(record[PRODUCTS.TITLE])
                )
            }
    }
}
