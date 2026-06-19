package org.xbery.artbeams.courses.service

import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.courses.domain.Course

/**
 * Service API for courses used by controllers and other business logic.
 */
interface CourseService {
    /**
     * Returns courses available for the user (based on user's purchased products).
     */
    fun findCoursesForUser(userId: String): List<Course>

    /**
     * Finds a course by its slug.
     */
    fun findBySlug(slug: String): Course?

    /**
     * Searches articles within a specific course. Results must be limited and
     * must only contain articles that belong to the given course.
     */
    fun searchArticlesInCourse(courseId: String, q: String, limit: Int): List<Article>
}
