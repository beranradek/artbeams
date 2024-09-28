/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema


import kotlin.collections.List

import org.jooq.Catalog
import org.jooq.Table
import org.jooq.impl.SchemaImpl
import org.xbery.artbeams.jooq.schema.tables.AntispamQuiz
import org.xbery.artbeams.jooq.schema.tables.ArticleCategory
import org.xbery.artbeams.jooq.schema.tables.Articles
import org.xbery.artbeams.jooq.schema.tables.AuthCode
import org.xbery.artbeams.jooq.schema.tables.Categories
import org.xbery.artbeams.jooq.schema.tables.Comments
import org.xbery.artbeams.jooq.schema.tables.Config
import org.xbery.artbeams.jooq.schema.tables.EntityAccessCount
import org.xbery.artbeams.jooq.schema.tables.Localisation
import org.xbery.artbeams.jooq.schema.tables.Media
import org.xbery.artbeams.jooq.schema.tables.OrderItems
import org.xbery.artbeams.jooq.schema.tables.Orders
import org.xbery.artbeams.jooq.schema.tables.Products
import org.xbery.artbeams.jooq.schema.tables.Queue
import org.xbery.artbeams.jooq.schema.tables.Roles
import org.xbery.artbeams.jooq.schema.tables.UserAccess
import org.xbery.artbeams.jooq.schema.tables.UserProduct
import org.xbery.artbeams.jooq.schema.tables.UserRole
import org.xbery.artbeams.jooq.schema.tables.Users


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class DefaultSchema : SchemaImpl("", DefaultCatalog.DEFAULT_CATALOG) {
    public companion object {

        /**
         * The reference instance of <code>DEFAULT_SCHEMA</code>
         */
        val DEFAULT_SCHEMA: DefaultSchema = DefaultSchema()
    }

    /**
     * The table <code>antispam_quiz</code>.
     */
    val ANTISPAM_QUIZ: AntispamQuiz get() = AntispamQuiz.ANTISPAM_QUIZ

    /**
     * The table <code>article_category</code>.
     */
    val ARTICLE_CATEGORY: ArticleCategory get() = ArticleCategory.ARTICLE_CATEGORY

    /**
     * The table <code>articles</code>.
     */
    val ARTICLES: Articles get() = Articles.ARTICLES

    /**
     * The table <code>auth_code</code>.
     */
    val AUTH_CODE: AuthCode get() = AuthCode.AUTH_CODE

    /**
     * The table <code>categories</code>.
     */
    val CATEGORIES: Categories get() = Categories.CATEGORIES

    /**
     * The table <code>comments</code>.
     */
    val COMMENTS: Comments get() = Comments.COMMENTS

    /**
     * The table <code>config</code>.
     */
    val CONFIG: Config get() = Config.CONFIG

    /**
     * The table <code>entity_access_count</code>.
     */
    val ENTITY_ACCESS_COUNT: EntityAccessCount get() = EntityAccessCount.ENTITY_ACCESS_COUNT

    /**
     * The table <code>localisation</code>.
     */
    val LOCALISATION: Localisation get() = Localisation.LOCALISATION

    /**
     * The table <code>media</code>.
     */
    val MEDIA: Media get() = Media.MEDIA

    /**
     * The table <code>order_items</code>.
     */
    val ORDER_ITEMS: OrderItems get() = OrderItems.ORDER_ITEMS

    /**
     * The table <code>orders</code>.
     */
    val ORDERS: Orders get() = Orders.ORDERS

    /**
     * The table <code>products</code>.
     */
    val PRODUCTS: Products get() = Products.PRODUCTS

    /**
     * The table <code>queue</code>.
     */
    val QUEUE: Queue get() = Queue.QUEUE

    /**
     * The table <code>roles</code>.
     */
    val ROLES: Roles get() = Roles.ROLES

    /**
     * The table <code>user_access</code>.
     */
    val USER_ACCESS: UserAccess get() = UserAccess.USER_ACCESS

    /**
     * The table <code>user_product</code>.
     */
    val USER_PRODUCT: UserProduct get() = UserProduct.USER_PRODUCT

    /**
     * The table <code>user_role</code>.
     */
    val USER_ROLE: UserRole get() = UserRole.USER_ROLE

    /**
     * The table <code>users</code>.
     */
    val USERS: Users get() = Users.USERS

    override fun getCatalog(): Catalog = DefaultCatalog.DEFAULT_CATALOG

    override fun getTables(): List<Table<*>> = listOf(
        AntispamQuiz.ANTISPAM_QUIZ,
        ArticleCategory.ARTICLE_CATEGORY,
        Articles.ARTICLES,
        AuthCode.AUTH_CODE,
        Categories.CATEGORIES,
        Comments.COMMENTS,
        Config.CONFIG,
        EntityAccessCount.ENTITY_ACCESS_COUNT,
        Localisation.LOCALISATION,
        Media.MEDIA,
        OrderItems.ORDER_ITEMS,
        Orders.ORDERS,
        Products.PRODUCTS,
        Queue.QUEUE,
        Roles.ROLES,
        UserAccess.USER_ACCESS,
        UserProduct.USER_PRODUCT,
        UserRole.USER_ROLE,
        Users.USERS
    )
}
