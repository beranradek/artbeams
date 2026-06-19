package org.xbery.artbeams.courses.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.courses.repository.ModuleRepository
import java.time.Instant

class CourseServiceTest : StringSpec({
    "findCoursesForUser returns repository courses" {
        val courseRepo = mockk<CourseRepository>()
        val articleRepo = mockk<ArticleRepository>()
        val moduleRepo = mockk<ModuleRepository>(relaxed = true)

        val course = Course(
            common = AssetAttributes.EMPTY,
            slug = "c1",
            title = "Course 1",
            subtitle = null,
            listingImage = null,
            image = null,
            perex = null,
            modules = listOf()
        )
        every { courseRepo.findAll() } returns listOf(course)

        val svc = CourseServiceImpl(courseRepo, articleRepo, moduleRepo)
        svc.findCoursesForUser("user1") shouldBe listOf(course)
    }

    "findBySlug returns repository result" {
        val courseRepo = mockk<CourseRepository>()
        val articleRepo = mockk<ArticleRepository>()
        val moduleRepo = mockk<ModuleRepository>(relaxed = true)

        val c1 = Course(AssetAttributes.EMPTY, "slug-a", "A", null, null, null, null, listOf())
        val c2 = Course(AssetAttributes.EMPTY, "slug-b", "B", null, null, null, null, listOf())
        every { courseRepo.findAll() } returns listOf(c1, c2)

        val svc = CourseServiceImpl(courseRepo, articleRepo, moduleRepo)
        svc.findBySlug("slug-b") shouldBe c2
        svc.findBySlug("missing") shouldBe null
    }

    "searchArticlesInCourse delegates to repository and returns results" {
        val courseRepo = mockk<CourseRepository>()
        val articleRepo = mockk<ArticleRepository>()
        val moduleRepo = mockk<ModuleRepository>(relaxed = true)

        val article = Article(
            common = AssetAttributes("a1", Instant.now(), "u", Instant.now(), "u"),
            validity = Validity(Instant.now(), null),
            externalId = null,
            slug = "s",
            title = "t",
            image = null,
            perex = "p",
            bodyEdited = "", body = "", keywords = "",
            showOnBlog = false, draft = false, editor = "e",
            courseId = "course-1", moduleId = null
        )

        every { articleRepo.findByQueryForCourse("course-1", "query", 5) } returns listOf(article)

        val svc = CourseServiceImpl(courseRepo, articleRepo, moduleRepo)
        val results = svc.searchArticlesInCourse("course-1", "query", 5)
        results shouldContainExactly listOf(article)
        verify { articleRepo.findByQueryForCourse("course-1", "query", 5) }
    }
})
