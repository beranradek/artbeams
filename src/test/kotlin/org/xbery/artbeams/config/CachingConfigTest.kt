package org.xbery.artbeams.config

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.xbery.artbeams.search.service.SearchService

class CachingConfigTest :
    StringSpec({

        "cache manager contains caches used by SearchService" {
            val cacheManager = CachingConfig().cacheManager()

            cacheManager.getCache(SearchService.SEARCH_RESULTS_CACHE_NAME).shouldNotBeNull()
            cacheManager.getCache(SearchService.SEARCH_SUGGESTIONS_CACHE_NAME).shouldNotBeNull()
        }

        "cache manager returns null for unknown cache name" {
            CachingConfig().cacheManager().getCache("unknownCache") shouldBe null
        }
    })
