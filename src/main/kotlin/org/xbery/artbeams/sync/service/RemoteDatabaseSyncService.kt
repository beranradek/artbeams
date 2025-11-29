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
     * Syncs articles and localizations from remote database to local database.
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
                    val articleId = remoteRecord.id ?: return@forEach

                    val existingArticle = articleRepository.findById(articleId)

                    if (existingArticle == null) {
                        // Create new article
                        localDsl.insertInto(ARTICLES)
                            .set(remoteRecord)
                            .execute()
                        articlesCreated++
                        logger.debug("Created article: $articleId - ${remoteRecord.title}")
                    } else {
                        // Update existing article
                        localDsl.update(ARTICLES)
                            .set(remoteRecord)
                            .where(ARTICLES.ID.eq(articleId))
                            .execute()
                        articlesUpdated++
                        logger.debug("Updated article: $articleId - ${remoteRecord.title}")
                    }

                    syncedArticleIds.add(articleId)

                    // Sync article categories
                    val remoteCategories = remoteDsl.select(ARTICLE_CATEGORY.CATEGORY_ID)
                        .from(ARTICLE_CATEGORY)
                        .where(ARTICLE_CATEGORY.ARTICLE_ID.eq(articleId))
                        .fetch()
                        .map { it.value1() }
                        .filterNotNull()

                    articleCategoryRepository.updateArticleCategories(articleId, remoteCategories)
                }

                // Remove external IDs from all synced articles to prevent accidental Google Docs updates
                if (syncedArticleIds.isNotEmpty()) {
                    logger.info("Removing external IDs from ${syncedArticleIds.size} synced articles")
                    localDsl.update(ARTICLES)
                        .setNull(ARTICLES.EXTERNAL_ID)
                        .where(ARTICLES.ID.`in`(syncedArticleIds))
                        .execute()
                }
            }

            val result = SyncResult(
                success = true,
                articlesCreated = articlesCreated,
                articlesUpdated = articlesUpdated,
                localisationsCreated = localisationsCreated,
                localisationsUpdated = localisationsUpdated,
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
        val errorMessage: String?
    )
}
