-- Execute this script with: psql -U artbeams_user -d artbeams -f src/main/resources/sql/migrations/add_faq_entries_table.sql
-- FAQ entries for articles, products and homepage (entity_type + entity_id).
CREATE TABLE faq_entries (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    created timestamp NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified timestamp NOT NULL,
    modified_by VARCHAR(40) NOT NULL,
    entity_type VARCHAR(20) NOT NULL,
    entity_id VARCHAR(40) NOT NULL,
    question VARCHAR(500) NOT NULL,
    answer TEXT NOT NULL,
    sort_order integer NOT NULL DEFAULT 0
);

CREATE INDEX idx_faq_entries_entity_sort ON faq_entries(entity_type, entity_id, sort_order);
CREATE INDEX idx_faq_entries_entity ON faq_entries(entity_type, entity_id);
