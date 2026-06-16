package org.xbery.artbeams.articles.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.xbery.artbeams.articles.repository.mapper.ArticleMapper
import org.xbery.artbeams.articles.repository.mapper.ArticleUnmapper
import org.xbery.artbeams.jooq.schema.tables.records.ArticlesRecord
import java.time.Instant

class ArticleMapperUnmapperCourseAssignmentTest {
    private val mapper = ArticleMapper()
    private val unmapper = ArticleUnmapper()

    @Test
    fun `maps and unmaps course and module ids`() {
        val now = Instant.now()
        val record = ArticlesRecord(
            id = "a1",
            externalId = null,
            validFrom = now,
            validTo = null,
            created = now,
            createdBy = "u1",
            modified = now,
            modifiedBy = "u1",
            slug = "slug",
            title = "Title",
            image = null,
            perex = "perex",
            body = "body",
            bodyEdited = "bodyEdited",
            editor = "markdown",
            keywords = "kw",
            showOnBlog = true,
            draft = false,
            courseId = "course-1",
            moduleId = "module-1"
        )

        val article = mapper.map(record)
        Assertions.assertEquals("course-1", article.courseId)
        Assertions.assertEquals("module-1", article.moduleId)

        val r2 = unmapper.unmap(article)
        Assertions.assertEquals("course-1", r2.courseId)
        Assertions.assertEquals("module-1", r2.moduleId)
    }
}
