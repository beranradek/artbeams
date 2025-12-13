package org.xbery.artbeams.userproducts.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.jooq.schema.tables.references.PRODUCTS
import org.xbery.artbeams.jooq.schema.tables.references.USER_PRODUCT
import org.xbery.artbeams.jooq.schema.tables.references.ORDER_ITEMS
import org.xbery.artbeams.jooq.schema.tables.references.ORDERS
import org.xbery.artbeams.userproducts.domain.UserProductDetail
import org.xbery.artbeams.userproducts.domain.UserProductInfo
import org.xbery.artbeams.orders.domain.OrderState
import java.math.BigDecimal
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
            .innerJoin(ORDER_ITEMS).on(ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))
            .innerJoin(ORDERS).on(ORDER_ITEMS.ORDER_ID.eq(ORDERS.ID))
            .where(
                USER_PRODUCT.USER_ID.eq(userId)
                    .and(ORDERS.CREATED_BY.eq(userId))
                    .and(
                        ORDERS.STATE.`in`(OrderState.AFTER_PAYMENT_STATES).or(PRODUCTS.PRICE_REGULAR.le(BigDecimal.ZERO))
                    )
            )
            .orderBy(USER_PRODUCT.CREATED.desc())
            .fetch { record ->
                UserProductInfo(
                    id = requireNotNull(record[USER_PRODUCT.ID]),
                    title = requireNotNull(record[PRODUCTS.TITLE]),
                    subtitle = record[PRODUCTS.SUBTITLE],
                    slug = requireNotNull(record[PRODUCTS.SLUG]),
                    listingImage = record[PRODUCTS.LISTING_IMAGE]
                )
            }
            .distinctBy { it.id }
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
