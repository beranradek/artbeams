package org.xbery.artbeams.courses.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.courses.repository.ModuleRepository

@Service
class CourseServiceImpl(
    private val courseRepository: CourseRepository,
    private val articleRepository: ArticleRepository,
    private val moduleRepository: ModuleRepository
) : CourseService {
    override fun findCoursesForUser(userId: String): List<Course> {
        // For now, courses available for a user are retrieved from repository.
        // In future this can be filtered by user's purchased products.
        return courseRepository.findAll()
    }

    override fun findBySlug(slug: String): Course? {
        // CourseRepository does not expose findBySlug helper; lookup in-memory.
        return courseRepository.findAll().firstOrNull { it.slug == slug }
    }

    override fun searchArticlesInCourse(courseId: String, q: String, limit: Int) =
        // Prefer repository search that includes course-bound articles. As an extra
        // safety measure, also enforce course filtering here to avoid leaking
        // public articles in case repository implementation changes.
        articleRepository.findByQueryForCourse(courseId, q, limit).filter { it.courseId == courseId }
}
