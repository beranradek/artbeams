package org.xbery.artbeams.courses

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.RecordUnmapper
import org.jooq.SelectConditionStep
import org.jooq.Table
import org.xbery.artbeams.courses.domain.Course
import org.xbery.artbeams.courses.domain.Module
import org.xbery.artbeams.courses.repository.CourseRepository
import org.xbery.artbeams.courses.repository.ModuleRepository
import org.xbery.artbeams.jooq.schema.tables.records.CoursesRecord

/**
 * Unit tests verifying CourseRepository (src/main/kotlin/org/xbery/artbeams/courses/repository/CourseRepository.kt)
 * populates Course.modules using ModuleRepository.findByCourseId(..).
 *
 * These tests mock the JOOQ DSLContext query chain to return Course instances
 * from the underlying AbstractMappingRepository, and then assert that
 * CourseRepository.findById and findAll replace the modules list with the
 * value returned by ModuleRepository. This keeps tests deterministic.
 */
class CourseRepositoryModulesTest :
    FunSpec({
        test("findById populates modules from ModuleRepository") {
            val dsl = mockk<DSLContext>()
            val mapper = mockk<RecordMapper<CoursesRecord, Course>>()
            val unmapper = mockk<RecordUnmapper<Course, CoursesRecord>>()
            val moduleRepo = mockk<ModuleRepository>()

            // Prepare a course returned by the parent repository (super.findById)
            val courseId = "course-1"
            val originalCourse = Course(
                common = org.xbery.artbeams.common.assets.domain.AssetAttributes(
                    id = courseId,
                    created = java.time.Instant.now(),
                    createdBy = "u",
                    modified = java.time.Instant.now(),
                    modifiedBy = "u"
                ),
                slug = "slug",
                title = "Title",
                subtitle = null,
                listingImage = null,
                image = null,
                perex = null,
                modules = emptyList() // initially empty as returned by parent
            )

            // Modules that ModuleRepository should provide
            val modules = listOf(
                Module(id = "m1", title = "Mod1", shortDescription = null, perex = null, image = null),
                Module(id = "m2", title = "Mod2", shortDescription = null, perex = null, image = null)
            )

            // Mock the jOOQ query chain to return originalCourse when fetchOne(mapper) is called
            val selectWhere = mockk<org.jooq.SelectWhereStep<CoursesRecord>>()
            val selectCondition = mockk<SelectConditionStep<CoursesRecord>>()
            every { dsl.selectFrom(any<Table<CoursesRecord>>()) } returns selectWhere
            every { selectWhere.where(any<Condition>()) } returns selectCondition
            every { selectCondition.fetchOne(mapper) } returns originalCourse

            every { moduleRepo.findByCourseId(courseId) } returns modules

            val repo = CourseRepository(dsl, mapper, unmapper, moduleRepo)

            val result = repo.findById(courseId)
            result shouldBe originalCourse.copy(modules = modules)
            // Sanity: modules were populated exactly as returned by ModuleRepository
            result?.modules.shouldContainExactly(modules)
        }

        test("findAll populates modules for each course from ModuleRepository") {
            val dsl = mockk<DSLContext>()
            val mapper = mockk<RecordMapper<CoursesRecord, Course>>()
            val unmapper = mockk<RecordUnmapper<Course, CoursesRecord>>()
            val moduleRepo = mockk<ModuleRepository>()

            val c1 = Course(
                common = org.xbery.artbeams.common.assets.domain.AssetAttributes(
                    id = "c1",
                    created = java.time.Instant.now(),
                    createdBy = "u",
                    modified = java.time.Instant.now(),
                    modifiedBy = "u"
                ),
                slug = "s1",
                title = "t1",
                subtitle = null,
                listingImage = null,
                image = null,
                perex = null,
                modules = emptyList()
            )
            val c2 = Course(
                common = org.xbery.artbeams.common.assets.domain.AssetAttributes(
                    id = "c2",
                    created = java.time.Instant.now(),
                    createdBy = "u",
                    modified = java.time.Instant.now(),
                    modifiedBy = "u"
                ),
                slug = "s2",
                title = "t2",
                subtitle = null,
                listingImage = null,
                image = null,
                perex = null,
                modules = emptyList()
            )

            val selectWhere = mockk<org.jooq.SelectWhereStep<CoursesRecord>>()
            val selectCondition = mockk<SelectConditionStep<CoursesRecord>>()
            every { dsl.selectFrom(any<Table<CoursesRecord>>()) } returns selectWhere
            // for findAll we expect fetch(mapper) to be invoked on the returned select
            every { selectWhere.fetch(mapper) } returns listOf(c1, c2)

            val modules1 = listOf(Module(id = "m1", title = "M1", shortDescription = null, perex = null, image = null))
            val modules2 = listOf(Module(id = "m2", title = "M2", shortDescription = null, perex = null, image = null))

            every { moduleRepo.findByCourseId("c1") } returns modules1
            every { moduleRepo.findByCourseId("c2") } returns modules2

            val repo = CourseRepository(dsl, mapper, unmapper, moduleRepo)

            val all = repo.findAll()
            // Each course must have modules replaced by values from ModuleRepository
            all.size shouldBe 2
            all[0].modules.shouldContainExactly(modules1)
            all[1].modules.shouldContainExactly(modules2)
        }
    })
