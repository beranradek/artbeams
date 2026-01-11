-- Migration to fix search_index column types for JOOQ compatibility
-- Run this with: psql -U artbeams_user -d artbeams -f src/main/resources/sql/migrations/migration_search_index_fix_types.sql

-- Change metadata from JSONB to TEXT (JOOQ compatible)
ALTER TABLE search_index ALTER COLUMN metadata TYPE TEXT;

-- Trigger function to automatically generate search_vector on insert/update
CREATE OR REPLACE FUNCTION search_index_update_vector() RETURNS trigger AS $$
BEGIN
    NEW.search_vector := to_tsvector('simple',
        COALESCE(NEW.title, '') || ' ' ||
        COALESCE(NEW.description, '') || ' ' ||
        COALESCE(NEW.keywords, '')
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Drop trigger if exists and recreate
DROP TRIGGER IF EXISTS search_index_vector_trigger ON search_index;
CREATE TRIGGER search_index_vector_trigger
    BEFORE INSERT OR UPDATE ON search_index
    FOR EACH ROW EXECUTE FUNCTION search_index_update_vector();
