package org.xbery.artbeams.courses.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.repository.AssetRepository
import org.xbery.artbeams.jooq.schema.tables.records.CoursesRecord
import org.xbery.artbeams.jooq.schema.tables.Courses
import org.xbery.artbeams.courses.domain.Course

/**
 * Course repository.
 *
 * Note: A proper JOOQ table/record for courses should be generated via
 * src/main/resources/sql/create_tables.sql and jOOQ codegen. This implementation
 * uses lightweight jOOQ stubs included in the source tree to allow compilation
 * and tests to run before DB migrations are applied. Once migrations are added,
 * regenerate jOOQ classes and remove these stubs.
 */
@Repository
class CourseRepository(
    override val dsl: DSLContext,
    override val mapper: RecordMapper<CoursesRecord, Course>,
    override val unmapper: RecordUnmapper<Course, CoursesRecord>
) : AssetRepository<Course, CoursesRecord>(
    dsl,
    mapper,
    unmapper
) {
    // Table reference for courses. Generated via jOOQ when SQL schema includes
    // courses and course_modules tables (see src/main/resources/sql/create_tables.sql).
    // NOTE: This file assumes jOOQ-generated classes CoursesRecord and COURSES
    // will exist after running migrations and code generation. For now we use a
    // lightweight generated stub under src/main/jooqGenerated to allow compilation
    // and tests to run in CI before DB migrations are applied.
    override val table: Table<CoursesRecord> = Courses.COURSES
    override val idField: Field<String?> = Courses.COURSES.ID

    // TODO: Implement mapping logic to load course_modules rows and populate
    // the Course.modules list. Currently modules may need to be loaded via a
    // separate repository method that joins COURSE_MODULES by course_id.
}
