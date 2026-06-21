package org.xbery.artbeams.courses.admin

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class AdminLayoutContainsCoursesLinkTest {
    @Test
    fun `admin layout contains courses link`() {
        val file = File("src/main/resources/templates/adminLayout.ftl")
        val content = file.readText()
        Assertions.assertTrue(content.contains("/admin/courses"), "adminLayout.ftl should contain link to /admin/courses")
    }
}
