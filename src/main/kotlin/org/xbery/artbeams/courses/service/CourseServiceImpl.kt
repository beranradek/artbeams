package org.xbery.artbeams.courses.service

import org.springframework.stereotype.Service
import org.xbery.artbeams.articles.repository.ArticleRepository
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.courses.repository.ModuleRepository
import org.xbery.artbeams.userproducts.repository.UserProductRepository

@Service
class CourseServiceImpl(
    private val courseRepository: CourseRepository,
    private val articleRepository: ArticleRepository,
    private val moduleRepository: ModuleRepository,
    private val userProductRepository: UserProductRepository
) : CourseService {
    override fun findCoursesForUser(userId: String): List<Course> {
        // Courses available for a user are determined by user's purchased products.
        val productIds = userProductRepository.findProductIdsForUser(userId)
        if (productIds.isEmpty()) return emptyList()
        // Aggregate courses for each product and deduplicate by id
        val courses = productIds.flatMap { pid -> courseRepository.findCoursesForProduct(pid) }
        return courses.distinctBy { it.id }
    }

    override fun findAllForAdmin(): List<Course> {
        // Admin listing currently returns all courses. Keep method small so
        // controller tests can mock service instead of repository.
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

    override fun saveCourse(edited: org.xbery.artbeams.courses.admin.EditedCourse, ctx: org.xbery.artbeams.common.context.OperationCtx): Course? {
        // Build Course domain object. Modules are handled separately and preserved if updating.
        val userId = ctx.loggedUser?.common?.id ?: org.xbery.artbeams.common.assets.domain.AssetAttributes.EMPTY_ID
        val common = org.xbery.artbeams.common.assets.domain.AssetAttributes.EMPTY
            .updatedWith(userId)
        val course =
            Course(common, edited.slug ?: "", edited.title ?: "", edited.subtitle, edited.listingImage, edited.image, edited.perex, emptyList())
        return try {
            if (edited.id == null || edited.id == org.xbery.artbeams.common.assets.domain.AssetAttributes.EMPTY_ID) {
                courseRepository.create(course)
            } else {
                // Preserve existing modules when updating
                val existing = courseRepository.requireById(edited.id!!)
                val updated = course.copy(common = existing.common.updatedWith(userId), modules = existing.modules)
                courseRepository.update(updated)
            }
        } catch (e: Exception) {
            return null
        }?.also { saved ->
            // Persist product assignments selected in admin UI. EditedCourse.productIds
            // is a comma-separated list of product ids. Convert to list and persist.
            try {
                val ids = edited.productIds
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() } ?: emptyList()
                courseRepository.saveProductCourseAssignments(saved.id, ids)
            } catch (e: Exception) {
                // On failure, return null to indicate save was not fully successful
                return null
            }
        }
    }

    override fun deleteCourse(id: String, ctx: org.xbery.artbeams.common.context.OperationCtx): Boolean =
        courseRepository.deleteById(id)
}
