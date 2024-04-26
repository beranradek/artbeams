/*
 * This file is generated by jOOQ.
 */
package org.xbery.artbeams.jooq.schema.keys


import org.jooq.ForeignKey
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.xbery.artbeams.jooq.schema.tables.AntispamQuiz
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
import org.xbery.artbeams.jooq.schema.tables.UserRole
import org.xbery.artbeams.jooq.schema.tables.Users
import org.xbery.artbeams.jooq.schema.tables.records.AntispamQuizRecord
import org.xbery.artbeams.jooq.schema.tables.records.ArticlesRecord
import org.xbery.artbeams.jooq.schema.tables.records.AuthCodeRecord
import org.xbery.artbeams.jooq.schema.tables.records.CategoriesRecord
import org.xbery.artbeams.jooq.schema.tables.records.CommentsRecord
import org.xbery.artbeams.jooq.schema.tables.records.ConfigRecord
import org.xbery.artbeams.jooq.schema.tables.records.EntityAccessCountRecord
import org.xbery.artbeams.jooq.schema.tables.records.LocalisationRecord
import org.xbery.artbeams.jooq.schema.tables.records.MediaRecord
import org.xbery.artbeams.jooq.schema.tables.records.OrderItemsRecord
import org.xbery.artbeams.jooq.schema.tables.records.OrdersRecord
import org.xbery.artbeams.jooq.schema.tables.records.ProductsRecord
import org.xbery.artbeams.jooq.schema.tables.records.QueueRecord
import org.xbery.artbeams.jooq.schema.tables.records.RolesRecord
import org.xbery.artbeams.jooq.schema.tables.records.UserAccessRecord
import org.xbery.artbeams.jooq.schema.tables.records.UserRoleRecord
import org.xbery.artbeams.jooq.schema.tables.records.UsersRecord



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val CONSTRAINT_2: UniqueKey<AntispamQuizRecord> = Internal.createUniqueKey(AntispamQuiz.ANTISPAM_QUIZ, DSL.name("CONSTRAINT_2"), arrayOf(AntispamQuiz.ANTISPAM_QUIZ.QUESTION), true)
val CONSTRAINT_B: UniqueKey<ArticlesRecord> = Internal.createUniqueKey(Articles.ARTICLES, DSL.name("CONSTRAINT_B"), arrayOf(Articles.ARTICLES.ID), true)
val AUTH_CODE_PKEY: UniqueKey<AuthCodeRecord> = Internal.createUniqueKey(AuthCode.AUTH_CODE, DSL.name("auth_code_pkey"), arrayOf(AuthCode.AUTH_CODE.CODE, AuthCode.AUTH_CODE.PURPOSE, AuthCode.AUTH_CODE.USER_ID), true)
val CONSTRAINT_4: UniqueKey<CategoriesRecord> = Internal.createUniqueKey(Categories.CATEGORIES, DSL.name("CONSTRAINT_4"), arrayOf(Categories.CATEGORIES.ID), true)
val CONSTRAINT_DC: UniqueKey<CommentsRecord> = Internal.createUniqueKey(Comments.COMMENTS, DSL.name("CONSTRAINT_DC"), arrayOf(Comments.COMMENTS.ID), true)
val CONSTRAINT_A: UniqueKey<ConfigRecord> = Internal.createUniqueKey(Config.CONFIG, DSL.name("CONSTRAINT_A"), arrayOf(Config.CONFIG.ENTRY_KEY), true)
val CONSTRAINT_3: UniqueKey<EntityAccessCountRecord> = Internal.createUniqueKey(EntityAccessCount.ENTITY_ACCESS_COUNT, DSL.name("CONSTRAINT_3"), arrayOf(EntityAccessCount.ENTITY_ACCESS_COUNT.ENTITY_TYPE, EntityAccessCount.ENTITY_ACCESS_COUNT.ENTITY_ID), true)
val CONSTRAINT_C3A: UniqueKey<LocalisationRecord> = Internal.createUniqueKey(Localisation.LOCALISATION, DSL.name("CONSTRAINT_C3A"), arrayOf(Localisation.LOCALISATION.ENTRY_KEY), true)
val CONSTRAINT_62: UniqueKey<MediaRecord> = Internal.createUniqueKey(Media.MEDIA, DSL.name("CONSTRAINT_62"), arrayOf(Media.MEDIA.ID), true)
val CONSTRAINT_7: UniqueKey<OrderItemsRecord> = Internal.createUniqueKey(OrderItems.ORDER_ITEMS, DSL.name("CONSTRAINT_7"), arrayOf(OrderItems.ORDER_ITEMS.ID), true)
val CONSTRAINT_C3: UniqueKey<OrdersRecord> = Internal.createUniqueKey(Orders.ORDERS, DSL.name("CONSTRAINT_C3"), arrayOf(Orders.ORDERS.ID), true)
val CONSTRAINT_C: UniqueKey<ProductsRecord> = Internal.createUniqueKey(Products.PRODUCTS, DSL.name("CONSTRAINT_C"), arrayOf(Products.PRODUCTS.ID), true)
val CONSTRAINT_66: UniqueKey<QueueRecord> = Internal.createUniqueKey(Queue.QUEUE, DSL.name("CONSTRAINT_66"), arrayOf(Queue.QUEUE.ID), true)
val CONSTRAINT_6: UniqueKey<RolesRecord> = Internal.createUniqueKey(Roles.ROLES, DSL.name("CONSTRAINT_6"), arrayOf(Roles.ROLES.ID), true)
val CONSTRAINT_D: UniqueKey<UserAccessRecord> = Internal.createUniqueKey(UserAccess.USER_ACCESS, DSL.name("CONSTRAINT_D"), arrayOf(UserAccess.USER_ACCESS.ID), true)
val CONSTRAINT_6A: UniqueKey<UsersRecord> = Internal.createUniqueKey(Users.USERS, DSL.name("CONSTRAINT_6A"), arrayOf(Users.USERS.ID), true)

// -------------------------------------------------------------------------
// FOREIGN KEY definitions
// -------------------------------------------------------------------------

val PARENT_ID_FK: ForeignKey<CommentsRecord, CommentsRecord> = Internal.createForeignKey(Comments.COMMENTS, DSL.name("parent_id_fk"), arrayOf(Comments.COMMENTS.PARENT_ID), org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_DC, arrayOf(Comments.COMMENTS.ID), true)
val ORDER_FK: ForeignKey<OrderItemsRecord, OrdersRecord> = Internal.createForeignKey(OrderItems.ORDER_ITEMS, DSL.name("order_fk"), arrayOf(OrderItems.ORDER_ITEMS.ORDER_ID), org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_C3, arrayOf(Orders.ORDERS.ID), true)
val ORDERED_PRODUCT_FK: ForeignKey<OrderItemsRecord, ProductsRecord> = Internal.createForeignKey(OrderItems.ORDER_ITEMS, DSL.name("ordered_product_fk"), arrayOf(OrderItems.ORDER_ITEMS.PRODUCT_ID), org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_C, arrayOf(Products.PRODUCTS.ID), true)
val FK_ROLE_ID: ForeignKey<UserRoleRecord, RolesRecord> = Internal.createForeignKey(UserRole.USER_ROLE, DSL.name("fk_role_id"), arrayOf(UserRole.USER_ROLE.ROLE_ID), org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_6, arrayOf(Roles.ROLES.ID), true)
val FK_USER_ID: ForeignKey<UserRoleRecord, UsersRecord> = Internal.createForeignKey(UserRole.USER_ROLE, DSL.name("fk_user_id"), arrayOf(UserRole.USER_ROLE.USER_ID), org.xbery.artbeams.jooq.schema.keys.CONSTRAINT_6A, arrayOf(Users.USERS.ID), true)
