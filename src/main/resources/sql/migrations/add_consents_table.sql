-- Migration script to add consents table and migrate existing user consents
-- Execute this script with: psql -U artbeams_user -d artbeams -f src/main/resources/sql/migrations/add_consents_table.sql

-- Create consents table
CREATE TABLE consents (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    valid_from timestamp NOT NULL,
    valid_to timestamp NOT NULL,
    login VARCHAR(64) NOT NULL,
    consent_type VARCHAR(20) NOT NULL,
    origin_product_id VARCHAR(40)
);

CREATE INDEX idx_consents_login ON consents (login);
CREATE INDEX idx_consents_validity ON consents (valid_from, valid_to);
CREATE INDEX idx_consents_login_type_validity ON consents (login, consent_type, valid_from, valid_to);

-- Migrate existing user consents from users table
-- Users with non-null consent timestamp get a NEWS consent
-- valid_from is set to 2025-01-01, valid_to is set to far future (2999-12-31)
INSERT INTO consents (id, valid_from, valid_to, login, consent_type, origin_product_id)
SELECT
    gen_random_uuid()::text AS id,
    TIMESTAMP '2025-01-01 00:00:00' AS valid_from,
    TIMESTAMP '2999-12-31 23:59:59' AS valid_to,
    email AS login,
    'NEWS' AS consent_type,
    NULL AS origin_product_id
FROM users
WHERE consent IS NOT NULL;

-- Optional: Drop the consent column from users table after verifying migration
-- Uncomment the following line after confirming successful migration:
-- ALTER TABLE users DROP COLUMN consent;
