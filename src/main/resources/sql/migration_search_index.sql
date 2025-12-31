-- Migration script to add search functionality to existing database
-- Run this with: psql -U artbeams_user -d artbeams -f src/main/resources/sql/migration_search_index.sql

-- Enable PostgreSQL extensions for full-text search
CREATE EXTENSION IF NOT EXISTS pg_trgm;  -- Trigram matching for autocomplete
CREATE EXTENSION IF NOT EXISTS unaccent; -- Remove diacritics

-- Search index table for full-text search across articles, categories, and products
CREATE TABLE IF NOT EXISTS search_index (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    entity_type VARCHAR(20) NOT NULL,  -- 'ARTICLE', 'CATEGORY', 'PRODUCT'
    entity_id VARCHAR(40) NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    keywords VARCHAR(1000),
    slug VARCHAR(128),
    search_vector tsvector,             -- Pre-computed FTS vector
    metadata JSONB,                      -- Flexible metadata (image, perex, price, etc.)
    valid_from timestamp,
    valid_to timestamp,
    created timestamp NOT NULL,
    modified timestamp NOT NULL
);

-- Indexes for search performance
CREATE INDEX IF NOT EXISTS idx_search_entity ON search_index(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_search_fts ON search_index USING GIN(search_vector);
CREATE INDEX IF NOT EXISTS idx_search_trigram_title ON search_index USING GIN(title gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_search_slug ON search_index(slug);
CREATE INDEX IF NOT EXISTS idx_search_validity ON search_index(valid_from, valid_to);
CREATE INDEX IF NOT EXISTS idx_search_modified ON search_index(modified DESC);

-- Initial population of search index from existing data
-- This will be done by the application's SearchIndexer service
-- Or you can run the following SQL to populate manually:

/*
-- Populate from articles
INSERT INTO search_index (id, entity_type, entity_id, title, description, keywords, slug, search_vector, metadata, valid_from, valid_to, created, modified)
SELECT
    gen_random_uuid()::varchar,
    'ARTICLE',
    id,
    title,
    perex,
    keywords,
    slug,
    to_tsvector('simple', COALESCE(title, '') || ' ' || COALESCE(perex, '') || ' ' || COALESCE(keywords, '')),
    jsonb_build_object('image', image, 'showOnBlog', show_on_blog),
    valid_from,
    valid_to,
    created,
    modified
FROM articles
WHERE valid_from <= NOW() AND (valid_to IS NULL OR valid_to >= NOW());

-- Populate from categories
INSERT INTO search_index (id, entity_type, entity_id, title, description, keywords, slug, search_vector, metadata, valid_from, valid_to, created, modified)
SELECT
    gen_random_uuid()::varchar,
    'CATEGORY',
    id,
    title,
    description,
    '',
    slug,
    to_tsvector('simple', COALESCE(title, '') || ' ' || COALESCE(description, '')),
    jsonb_build_object(),
    valid_from,
    valid_to,
    created,
    modified
FROM categories
WHERE valid_from <= NOW() AND (valid_to IS NULL OR valid_to >= NOW());

-- Populate from products
INSERT INTO search_index (id, entity_type, entity_id, title, description, keywords, slug, search_vector, metadata, valid_from, valid_to, created, modified)
SELECT
    gen_random_uuid()::varchar,
    'PRODUCT',
    id,
    title,
    subtitle,
    '',
    slug,
    to_tsvector('simple', COALESCE(title, '') || ' ' || COALESCE(subtitle, '')),
    jsonb_build_object('image', image, 'listingImage', listing_image, 'priceRegular', price_regular, 'priceDiscounted', price_discounted),
    NULL,
    NULL,
    created,
    modified
FROM products;
*/
