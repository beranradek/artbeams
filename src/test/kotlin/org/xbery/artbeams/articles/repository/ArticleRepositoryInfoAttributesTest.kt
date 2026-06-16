package org.xbery.artbeams.articles.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.xbery.artbeams.jooq.schema.tables.references.ARTICLES

class ArticleRepositoryInfoAttributesTest {
    @Test
    fun `info attributes include course and module fields`() {
        Assertions.assertTrue(ArticleRepository.INFO_ATTRIBUTES.contains(ARTICLES.COURSE_ID), "INFO_ATTRIBUTES should contain COURSE_ID")
        Assertions.assertTrue(ArticleRepository.INFO_ATTRIBUTES.contains(ARTICLES.MODULE_ID), "INFO_ATTRIBUTES should contain MODULE_ID")
    }
}
