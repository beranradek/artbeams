package org.xbery.artbeams.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.service.CategoryServiceImpl
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.common.access.domain.EntityAccessCount
import org.xbery.artbeams.common.antispam.domain.AntispamQuiz

/**
 * @author Radek Beran
 */
@EnableCaching
@Configuration
open class CachingConfig() {

    @Bean
    @Primary
    open fun cacheManager(): ConcurrentMapCacheManager =
        ConcurrentMapCacheManager(
            Article.CacheName, Category.CacheName, CategoryServiceImpl.ARTICLE_CATEGORIES_CACHE_NAME,
            Comment.CACHE_NAME,
            EntityAccessCount.CacheName,
            AntispamQuiz.CacheName,
            "searchConsoleMetrics", "searchConsolePages", "searchConsoleQueries", "searchConsoleSitemaps",
            "searchSuggestions" // Search autocomplete suggestions cache
        )
}
