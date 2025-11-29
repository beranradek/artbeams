package org.xbery.artbeams.sync.service

import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleCategoryRepository
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.jooq.schema.tables.references.ARTICLES
import org.xbery.artbeams.jooq.schema.tables.references.ARTICLE_CATEGORY
import org.xbery.artbeams.jooq.schema.tables.references.LOCALISATION
import org.xbery.artbeams.jooq.schema.tables.references.MEDIA
import org.xbery.artbeams.localisation.repository.LocalisationRepository
import org.xbery.artbeams.localisation.domain.Localisation
import java.sql.DriverManager

/**
 * Service for syncing content from a remote ArtBeams database.
 * @author Radek Beran
 */
@Service
class RemoteDatabaseSyncService(
    private val appConfig: AppConfig,
    private val localDsl: DSLContext,
    private val articleRepository: ArticleRepository,
    private val articleCategoryRepository: ArticleCategoryRepository,
    private val localisationRepository: LocalisationRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Checks if remote database connection is configured.
     */
    fun isRemoteDbConfigured(): Boolean {
        val connectionString = appConfig.findConfig("remote.db.connection")
        return !connectionString.isNullOrBlank()
    }

    /**
     * Syncs articles, localizations, and media from remote database to local database.
     * - Articles are matched by slug (not by ID)
     * - Localizations are matched by key (not by ID)
     * - Media are matched by filename, content type, and size (not by ID)
     * After sync, removes external IDs from all synced articles to prevent unintentional updates to Google Docs.
     */
    @CacheEvict(value = [Article.CacheName], allEntries = true)
    fun syncFromRemoteDatabase(): SyncResult {
        val connectionString = appConfig.findConfig("remote.db.connection")
        require(!connectionString.isNullOrBlank()) { "Remote database connection is not configured (remote.db.connection)" }

        logger.info("Starting sync from remote database")
        var articlesCreated = 0
        var articlesUpdated = 0
        var localisationsCreated = 0
        var localisationsUpdated = 0
        var mediaCreated = 0
        var mediaUpdated = 0

        try {
            // Parse connection string to extract JDBC URL, username, and password
            // Expected format: jdbc:postgresql://host:port/database?user=username&password=password
            val connection = DriverManager.getConnection(connectionString)

            connection.use { conn ->
                val remoteDsl = DSL.using(conn, localDsl.dialect())

                // Sync localizations
                logger.info("Syncing localizations from remote database")
                val remoteLocalisations = remoteDsl.selectFrom(LOCALISATION).fetch()

                remoteLocalisations.forEach { remoteRecord ->
                    val entryKey = remoteRecord.entryKey ?: return@forEach
                    val entryValue = remoteRecord.entryValue ?: ""

                    val existingLocalisation = localisationRepository.findByKey(entryKey)

                    val localisation = Localisation(entryKey, entryValue)

                    if (existingLocalisation == null) {
                        localisationRepository.create(localisation)
                        localisationsCreated++
                        logger.debug("Created localisation: $entryKey")
                    } else {
                        localisationRepository.update(entryKey, localisation)
                        localisationsUpdated++
                        logger.debug("Updated localisation: $entryKey")
                    }
                }

                // Reload localisations cache
                localisationRepository.reloadEntries()

                // Sync articles
                logger.info("Syncing articles from remote database")
                val remoteArticles = remoteDsl.selectFrom(ARTICLES).fetch()
                val syncedArticleIds = mutableListOf<String>()

                remoteArticles.forEach { remoteRecord ->
                    val slug = remoteRecord.slug ?: return@forEach

                    // Find existing local article by slug (not by ID)
                    val existingLocalArticle = localDsl.selectFrom(ARTICLES)
                        .where(ARTICLES.SLUG.eq(slug))
                        .fetchOne()

                    val localArticleId: String
                    if (existingLocalArticle == null) {
                        // Create new article with remote data (including remote ID)
                        localDsl.insertInto(ARTICLES)
                            .set(remoteRecord)
                            .execute()
                        localArticleId = remoteRecord.id!!
                        articlesCreated++
                        logger.debug("Created article: $slug (ID: $localArticleId) - ${remoteRecord.title}")
                    } else {
                        // Update existing local article (keep local ID, update with remote data)
                        localArticleId = existingLocalArticle.id!!
                        localDsl.update(ARTICLES)
                            .set(ARTICLES.EXTERNAL_ID, remoteRecord.externalId)
                            .set(ARTICLES.VALID_FROM, remoteRecord.validFrom)
                            .set(ARTICLES.VALID_TO, remoteRecord.validTo)
                            .set(ARTICLES.CREATED, remoteRecord.created)
                            .set(ARTICLES.CREATED_BY, remoteRecord.createdBy)
                            .set(ARTICLES.MODIFIED, remoteRecord.modified)
                            .set(ARTICLES.MODIFIED_BY, remoteRecord.modifiedBy)
                            .set(ARTICLES.SLUG, remoteRecord.slug)
                            .set(ARTICLES.TITLE, remoteRecord.title)
                            .set(ARTICLES.IMAGE, remoteRecord.image)
                            .set(ARTICLES.PEREX, remoteRecord.perex)
                            .set(ARTICLES.BODY, remoteRecord.body)
                            .set(ARTICLES.BODY_EDITED, remoteRecord.bodyEdited)
                            .set(ARTICLES.EDITOR, remoteRecord.editor)
                            .set(ARTICLES.KEYWORDS, remoteRecord.keywords)
                            .set(ARTICLES.SHOW_ON_BLOG, remoteRecord.showOnBlog)
                            .where(ARTICLES.ID.eq(localArticleId))
                            .execute()
                        articlesUpdated++
                        logger.debug("Updated article: $slug (local ID: $localArticleId) - ${remoteRecord.title}")
                    }

                    syncedArticleIds.add(localArticleId)

                    // Sync article categories (using local article ID)
                    val remoteCategories = remoteDsl.select(ARTICLE_CATEGORY.CATEGORY_ID)
                        .from(ARTICLE_CATEGORY)
                        .where(ARTICLE_CATEGORY.ARTICLE_ID.eq(remoteRecord.id))
                        .fetch()
                        .map { it.value1() }
                        .filterNotNull()

                    articleCategoryRepository.updateArticleCategories(localArticleId, remoteCategories)
                }

                // Remove external IDs from all synced articles to prevent accidental Google Docs updates
                if (syncedArticleIds.isNotEmpty()) {
                    logger.info("Removing external IDs from ${syncedArticleIds.size} synced articles")
                    localDsl.update(ARTICLES)
                        .setNull(ARTICLES.EXTERNAL_ID)
                        .where(ARTICLES.ID.`in`(syncedArticleIds))
                        .execute()
                }

                // Sync media
                logger.info("Syncing media from remote database")
                val remoteMedia = remoteDsl.selectFrom(MEDIA).fetch()

                remoteMedia.forEach { remoteRecord ->
                    val filename = remoteRecord.filename ?: return@forEach
                    val contentType = remoteRecord.contentType
                    val size = remoteRecord.size

                    // Check if media exists with the same filename, content_type, and size
                    val existingMedia = localDsl.selectFrom(MEDIA)
                        .where(
                            MEDIA.FILENAME.eq(filename)
                                .and(MEDIA.CONTENT_TYPE.eq(contentType))
                                .and(MEDIA.SIZE.eq(size))
                        )
                        .fetchOne()

                    if (existingMedia == null) {
                        // Create new media record
                        localDsl.insertInto(MEDIA)
                            .set(remoteRecord)
                            .execute()
                        mediaCreated++
                        logger.debug("Created media: $filename (${contentType ?: "unknown"}, $size bytes)")
                    } else {
                        // Update existing media record (keep the existing id)
                        localDsl.update(MEDIA)
                            .set(MEDIA.DATA, remoteRecord.data)
                            .set(MEDIA.PRIVATE_ACCESS, remoteRecord.privateAccess)
                            .set(MEDIA.WIDTH, remoteRecord.width)
                            .set(MEDIA.HEIGHT, remoteRecord.height)
                            .where(MEDIA.ID.eq(existingMedia.id))
                            .execute()
                        mediaUpdated++
                        logger.debug("Updated media: $filename (${contentType ?: "unknown"}, $size bytes)")
                    }
                }
            }

            val result = SyncResult(
                success = true,
                articlesCreated = articlesCreated,
                articlesUpdated = articlesUpdated,
                localisationsCreated = localisationsCreated,
                localisationsUpdated = localisationsUpdated,
                mediaCreated = mediaCreated,
                mediaUpdated = mediaUpdated,
                errorMessage = null
            )

            logger.info("Sync completed successfully: $result")
            return result

        } catch (ex: Exception) {
            logger.error("Error syncing from remote database", ex)
            return SyncResult(
                success = false,
                articlesCreated = articlesCreated,
                articlesUpdated = articlesUpdated,
                localisationsCreated = localisationsCreated,
                localisationsUpdated = localisationsUpdated,
                mediaCreated = mediaCreated,
                mediaUpdated = mediaUpdated,
                errorMessage = ex.message ?: "Unknown error occurred"
            )
        }
    }

    data class SyncResult(
        val success: Boolean,
        val articlesCreated: Int,
        val articlesUpdated: Int,
        val localisationsCreated: Int,
        val localisationsUpdated: Int,
        val mediaCreated: Int,
        val mediaUpdated: Int,
        val errorMessage: String?
    )
}
