-- Adds assignment of article to course and course module (see create_tables.sql).
-- Safe to run repeatedly. Run it on databases created before courses were introduced:
-- psql -U <user> -d artbeams -f src/main/resources/sql/migrations/add_articles_course_module.sql
ALTER TABLE articles ADD COLUMN IF NOT EXISTS course_id VARCHAR(40) DEFAULT NULL;
ALTER TABLE articles ADD COLUMN IF NOT EXISTS module_id VARCHAR(40) DEFAULT NULL;
