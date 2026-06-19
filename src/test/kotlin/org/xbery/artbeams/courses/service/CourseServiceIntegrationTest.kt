package org.xbery.artbeams.courses.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.courses.repository.ModuleRepository
import java.time.Instant

class CourseServiceIntegrationTest :
    StringSpec({
        "searchArticlesInCourse does not return public articles" {
            val courseRepo = mockk<CourseRepository>(relaxed = true)
            val moduleRepo = mockk<ModuleRepository>(relaxed = true)

            val articleRepo = mockk<ArticleRepository>()

            val expectedCourseId = "course-42"

            // Repository returns a mixed list (simulating a buggy implementation).
            val a1 =
                Article(
                    AssetAttributes("1", Instant.now(), "u", Instant.now(), "u"),
                    Validity(Instant.now(), null),
                    null,
                    "s1",
                    "t1",
                    null,
                    "p",
                    "",
                    "",
                    "",
                    false,
                    false,
                    "e",
                    expectedCourseId,
                    null
                )
            val a2 =
                Article(
                    AssetAttributes("2", Instant.now(), "u", Instant.now(), "u"),
                    Validity(Instant.now(), null),
                    null,
                    "s2",
                    "t2",
                    null,
                    "p2",
                    "",
                    "",
                    "",
                    false,
                    false,
                    "e",
                    null,
                    null
                ) // public
            every { articleRepo.findByQueryForCourse(expectedCourseId, "q", 10) } returns listOf(a1, a2)

            val svc = CourseServiceImpl(courseRepo, articleRepo, moduleRepo)

            val results = svc.searchArticlesInCourse(expectedCourseId, "q", 10)

            // Ensure we returned something and that all returned articles belong to the expected course
            results.shouldNotBeEmpty()
            results.all { it.courseId == expectedCourseId } shouldBe true
        }
    })
