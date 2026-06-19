package org.xbery.artbeams.courses

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.jooq.schema.tables.records.CoursesRecord

class CourseRepositorySmokeTest :
    FunSpec({
        test("course repository is assignable to AssetRepository") {
            val dsl = mockk<DSLContext>()
            val mapper = mockk<RecordMapper<CoursesRecord, Course>>()
            val unmapper = mockk<RecordUnmapper<Course, CoursesRecord>>()

            val repo = CourseRepository(dsl, mapper, unmapper)

            (repo is org.xbery.artbeams.common.assets.repository.AssetRepository<*, *>).shouldBeTrue()
        }
    })
