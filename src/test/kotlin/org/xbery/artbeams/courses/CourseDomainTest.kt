package org.xbery.artbeams.courses

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import java.time.Instant

class CourseDomainTest : FunSpec({
    test("course with modules") {
        val now = Instant.now()
        val attrs = AssetAttributes("course-1", now, "user", now, "user")
        val modules = listOf(
            Module("m1", "Module One", "img1.jpg", "Short 1", "Perex 1"),
            Module("m2", "Module Two", null, "Short 2", null)
        )

        val course = Course(attrs, "slug", "Course title", "Subtitle", "list.jpg", "img.jpg", "Perex course", modules)

        course.id shouldBe "course-1"
        course.title shouldBe "Course title"
        course.modules shouldHaveSize 2
        course.modules[0].title shouldBe "Module One"
    }
})
