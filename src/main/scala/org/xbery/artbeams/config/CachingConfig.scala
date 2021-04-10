package org.xbery.artbeams.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.{Bean, Configuration}
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.common.access.domain.EntityAccessCount

/**
  * @author Radek Beran
  */
@EnableCaching
@Configuration
class CachingConfig {

  @Bean def cacheManager = new ConcurrentMapCacheManager(
    Article.CacheName,
    Category.CacheName,
    Comment.CacheName,
    EntityAccessCount.CacheName
  )
}
