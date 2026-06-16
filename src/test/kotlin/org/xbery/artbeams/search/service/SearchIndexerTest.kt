package org.xbery.artbeams.search.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.search.repository.SearchIndexRepository
import org.xbery.artbeams.categories.repository.CategoryRepository
import org.xbery.artbeams.articles.repository.ArticleRepository

class SearchIndexerTest {
    @Test
    fun `reindexAll skips course assigned articles`() {
        val searchIndexRepository = mockk<SearchIndexRepository>(relaxed = true)
        val articleRepository = mockk<ArticleRepository>(relaxed = true)
        val categoryRepository = mockk<CategoryRepository>(relaxed = true)
        val productRepository = mockk<ProductRepository>(relaxed = true)

        val now = java.time.Instant.now()
        val articlePublic = Article(
            common = AssetAttributes(id = "a1", created = now, createdBy = "u", modified = now, modifiedBy = "u"),
            validity = Validity(validFrom = now, validTo = null),
            externalId = null,
            slug = "s1",
            title = "Title 1",
            image = null,
            perex = "p",
            bodyEdited = "be",
            body = "b",
            keywords = "",
            showOnBlog = true,
            draft = false,
            editor = "markdown",
            courseId = null,
            moduleId = null
        )
        val articleCourse = articlePublic.copy(common = articlePublic.common.copy(id = "a2"), courseId = "course-1")

        every { articleRepository.findLatest(10000) } returns listOf(articlePublic, articleCourse)
        every { categoryRepository.findCategories() } returns emptyList()
        every { productRepository.findProducts() } returns emptyList()

        val indexer = SearchIndexer(searchIndexRepository, articleRepository, categoryRepository, productRepository)

        indexer.reindexAll()

        // deleteAll is called
        verify(atLeast = 1) { searchIndexRepository.deleteAll() }
        // create should be called only for public article
        verify(exactly = 1) { searchIndexRepository.create(match { it.entityId == articlePublic.id }) }
        // course-assigned article must not be indexed
        verify(exactly = 0) { searchIndexRepository.create(match { it.entityId == articleCourse.id }) }
    }
}
