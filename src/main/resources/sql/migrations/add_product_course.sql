-- Execute this script with: psql -U artbeams_user -d artbeams -f src/main/resources/sql/migrations/add_product_course.sql
-- Adds product_course many-to-many table linking products and courses.
-- Idempotent: uses IF NOT EXISTS where supported by PostgreSQL.
CREATE TABLE IF NOT EXISTS product_course (
    product_id VARCHAR(40) NOT NULL,
    course_id VARCHAR(40) NOT NULL,
    PRIMARY KEY (product_id, course_id)
);

-- Indexes to speed up lookups from product -> courses and course -> products
CREATE INDEX IF NOT EXISTS idx_product_course_product_id ON product_course (product_id);
CREATE INDEX IF NOT EXISTS idx_product_course_course_id ON product_course (course_id);

-- Note: No foreign key constraints are added here to keep migration simple and
-- compatible with existing data loading flows. The application relies on
-- product_id and course_id textual references and performs lookups via JOOQ.
