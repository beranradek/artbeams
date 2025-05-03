package org.xbery.artbeams.userproducts.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.jooq.schema.tables.references.PRODUCTS
import org.xbery.artbeams.jooq.schema.tables.references.USER_PRODUCT
import org.xbery.artbeams.userproducts.domain.UserProductDetail
import org.xbery.artbeams.userproducts.domain.UserProductInfo
import java.time.Instant
import java.util.*

/**
 * @author Radek Beran
 */
@Repository
class UserProductRepository(
    private val dsl: DSLContext
) {

    fun findProductByUserIdAndProductId(userId: String, productId: String): UserProductInfo? {
        return dsl.select(
            USER_PRODUCT.ID,
            PRODUCTS.TITLE,
            PRODUCTS.SUBTITLE,
            PRODUCTS.SLUG,
            PRODUCTS.LISTING_IMAGE
        )
            .from(USER_PRODUCT)
            .innerJoin(PRODUCTS).on(USER_PRODUCT.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(
                USER_PRODUCT.USER_ID.eq(userId),
                USER_PRODUCT.PRODUCT_ID.eq(productId)
            )
            .fetchOne { record ->
                UserProductInfo(
                    id = requireNotNull(record[USER_PRODUCT.ID]),
                    title = requireNotNull(record[PRODUCTS.TITLE]),
                    subtitle = record[PRODUCTS.SUBTITLE],
                    slug = requireNotNull(record[PRODUCTS.SLUG]),
                    listingImage = record[PRODUCTS.LISTING_IMAGE]
                )
            }
    }

    /**
     * Adds a product to user's library.
     * @return true if the product was added, false if it was already present in the library
     */
    fun addProductToUserLibrary(userId: String, productId: String): Boolean {
        val product = findProductByUserIdAndProductId(userId, productId)
        if (product != null) {
            return false
        }
        return dsl.insertInto(USER_PRODUCT)
            .set(USER_PRODUCT.ID, UUID.randomUUID().toString())
            .set(USER_PRODUCT.USER_ID, userId)
            .set(USER_PRODUCT.PRODUCT_ID, productId)
            .set(USER_PRODUCT.CREATED, Instant.now())
            .execute() == 1
    }

    fun findUserProducts(userId: String): List<UserProductInfo> {
        return dsl.select(
            USER_PRODUCT.ID,
            PRODUCTS.TITLE,
            PRODUCTS.SUBTITLE,
            PRODUCTS.SLUG,
            PRODUCTS.LISTING_IMAGE
        )
            .from(USER_PRODUCT)
            .innerJoin(PRODUCTS).on(USER_PRODUCT.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(
                USER_PRODUCT.USER_ID.eq(userId)
            )
            .orderBy(USER_PRODUCT.CREATED.desc()) // from newest to oldest
            .fetch { record ->
                UserProductInfo(
                    id = requireNotNull(record[USER_PRODUCT.ID]),
                    title = requireNotNull(record[PRODUCTS.TITLE]),
                    subtitle = record[PRODUCTS.SUBTITLE],
                    slug = requireNotNull(record[PRODUCTS.SLUG]),
                    listingImage = record[PRODUCTS.LISTING_IMAGE]
                )
            }
    }

    fun findUserProduct(userId: String, productSlug: String): UserProductDetail? {
        return dsl.select(
            USER_PRODUCT.ID,
            PRODUCTS.TITLE,
            PRODUCTS.SUBTITLE,
            PRODUCTS.SLUG,
            PRODUCTS.IMAGE
        )
            .from(USER_PRODUCT)
            .innerJoin(PRODUCTS).on(USER_PRODUCT.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(
                USER_PRODUCT.USER_ID.eq(userId),
                PRODUCTS.SLUG.eq(productSlug)
            )
            .fetchOne { record ->
                UserProductDetail(
                    id = requireNotNull(record[USER_PRODUCT.ID]),
                    title = requireNotNull(record[PRODUCTS.TITLE]),
                    subtitle = record[PRODUCTS.SUBTITLE],
                    slug = requireNotNull(record[PRODUCTS.SLUG]),
                    image = record[PRODUCTS.IMAGE]
                )
            }
    }
}
