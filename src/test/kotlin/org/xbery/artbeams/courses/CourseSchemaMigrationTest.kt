package org.xbery.artbeams.courses

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File

class CourseSchemaMigrationTest :
    FunSpec({
        test("create_tables.sql contains courses and course_modules") {
            val createSql = File("src/main/resources/sql/create_tables.sql").readText()
            createSql.shouldContain("CREATE TABLE courses")
            createSql.shouldContain("CREATE TABLE course_modules")
        }

        test("migration file exists") {
            val mig = File("src/main/resources/sql/migrations/add_courses_and_modules.sql")
            mig.exists().shouldBe(true)
            val migText = mig.readText()
            // Migration must include FK and index for course_modules.course_id
            migText.shouldContain("fk_course_modules_course_id")
            migText.shouldContain("idx_course_modules_course_id")
        }
    })
