package org.xbery.artbeams.articles.admin

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ArticleFormFieldsTest {
    @Test
    fun `form contains courseId and moduleId fields`() {
        val fields = ArticleForm.definition.fields
        Assertions.assertTrue(fields.containsKey("courseId"), "Form should contain courseId field")
        Assertions.assertTrue(fields.containsKey("moduleId"), "Form should contain moduleId field")
    }
}
