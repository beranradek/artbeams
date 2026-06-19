-- Execute this script with: psql -U artbeams_user -d artbeams -f src/main/resources/sql/migrations/add_courses_and_modules.sql
-- Adds courses and course_modules tables used by the Courses/Modules feature.
-- NOTE: After running jOOQ code generation (./gradlew generateJooq) the
-- temporary stub classes under src/main/kotlin/org/xbery/artbeams/jooq/schema
-- should be removed to avoid duplicate-class conflicts with generated sources.
-- Paths to remove (examples included in this repository):
--   src/main/kotlin/org/xbery/artbeams/jooq/schema/tables/Courses.kt
--   src/main/kotlin/org/xbery/artbeams/jooq/schema/tables/CourseModules.kt
--   src/main/kotlin/org/xbery/artbeams/jooq/schema/records/CoursesRecord.kt
--   src/main/kotlin/org/xbery/artbeams/jooq/schema/records/CourseModulesRecord.kt

CREATE TABLE IF NOT EXISTS courses (
  id VARCHAR(40) NOT NULL PRIMARY KEY,
  created timestamp DEFAULT NULL,
  created_by VARCHAR(40) DEFAULT NULL,
  modified timestamp DEFAULT NULL,
  modified_by VARCHAR(40) DEFAULT NULL,
  slug VARCHAR(128) DEFAULT NULL,
  title VARCHAR(128) DEFAULT NULL,
  subtitle VARCHAR(256) DEFAULT NULL,
  listing_image VARCHAR(128) DEFAULT NULL,
  image VARCHAR(128) DEFAULT NULL,
  perex VARCHAR(4000) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS course_modules (
  id VARCHAR(40) NOT NULL PRIMARY KEY,
  course_id VARCHAR(40) NOT NULL,
  title VARCHAR(128) DEFAULT NULL,
  image VARCHAR(128) DEFAULT NULL,
  short_description VARCHAR(1000) DEFAULT NULL,
  perex TEXT DEFAULT NULL,
  sort_order integer NOT NULL DEFAULT 0,
  CONSTRAINT fk_course_modules_course_id FOREIGN KEY (course_id) REFERENCES courses (id)
);

-- Index to speed up module lookups per course. CREATE INDEX IF NOT EXISTS is
-- supported by PostgreSQL and is safe to run repeatedly.
CREATE INDEX IF NOT EXISTS idx_course_modules_course_id ON course_modules (course_id);
