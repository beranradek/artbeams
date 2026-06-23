package org.xbery.artbeams.courses.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.mapper.ModuleMapper
import org.xbery.artbeams.jooq.schema.tables.CourseModules
import java.util.UUID

/**
 * JOOQ-based Module repository that loads modules for a given course.
 */
@Repository
class ModuleRepository(
    private val dsl: DSLContext,
    private val mapper: ModuleMapper
) {
    /**
     * Fetch modules belonging to the specified course, ordered by sort order.
     */
    fun findByCourseId(courseId: String): List<Module> = dsl
        .selectFrom(CourseModules.COURSE_MODULES)
        .where(CourseModules.COURSE_MODULES.COURSE_ID.eq(courseId))
        .orderBy(CourseModules.COURSE_MODULES.SORT_ORDER)
        .fetch(mapper)

    /**
     * Persist a module for a course. If edited.id is blank a new id is generated.
     * The method sets a SORT_ORDER value (append to end) and returns persisted
     * Module mapped from the returned record.
     */
    fun save(courseId: String, edited: org.xbery.artbeams.courses.admin.EditedModule): Module? {
        val id = edited.id?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()

        // Compute next sort order as count of existing modules + 1
        val count = dsl
            .selectCount()
            .from(CourseModules.COURSE_MODULES)
            .where(CourseModules.COURSE_MODULES.COURSE_ID.eq(courseId))
            .fetchOne(0, Long::class.java) ?: 0L
        val sortOrder = count.toInt() + 1

        val record = dsl
            .insertInto(CourseModules.COURSE_MODULES)
            .set(CourseModules.COURSE_MODULES.ID, id)
            .set(CourseModules.COURSE_MODULES.COURSE_ID, courseId)
            .set(CourseModules.COURSE_MODULES.TITLE, edited.title)
            .set(CourseModules.COURSE_MODULES.IMAGE, edited.image)
            .set(CourseModules.COURSE_MODULES.SHORT_DESCRIPTION, edited.shortDescription)
            .set(CourseModules.COURSE_MODULES.PEREX, edited.perex)
            .set(CourseModules.COURSE_MODULES.SORT_ORDER, sortOrder)
            .returning()
            .fetchOne()

        return record?.let { mapper.map(it) }
    }

    /**
     * Delete a module constrained by both id and courseId to avoid accidental
     * deletion of modules belonging to another course.
     */
    fun delete(courseId: String, id: String) {
        dsl
            .deleteFrom(CourseModules.COURSE_MODULES)
            .where(CourseModules.COURSE_MODULES.ID.eq(id))
            .and(CourseModules.COURSE_MODULES.COURSE_ID.eq(courseId))
            .execute()
    }
}
