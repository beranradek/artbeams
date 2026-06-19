-- Usage: psql -U artbeams_user -d artbeams -f scripts/seed_courses_e2e.sql
--
-- Idempotent seed script for end-to-end tests for Courses feature.
-- This script is safe to run multiple times. It uses INSERT ... ON CONFLICT
-- or INSERT ... SELECT WHERE NOT EXISTS patterns so duplicate rows are
-- not created.
--
-- Verifying queries (run after the seed to ensure duplicates were not created):
--  SELECT login, id FROM users WHERE login IN ('testadmin','testmember');
--  SELECT slug, id FROM products WHERE title = 'Kurz zdravého stravování';
--  SELECT slug, id FROM courses WHERE slug = 'zdrave-stravovani';
--  -- Count relationships
--  SELECT COUNT(*) FROM user_role ur JOIN users u ON ur.user_id = u.id WHERE u.login = 'testadmin';

-- Create roles (idempotent)
INSERT INTO roles (id, created, created_by, modified, modified_by, name)
VALUES
  ('role-admin', now(), 'seed', now(), 'seed', 'ADMIN'),
  ('role-member', now(), 'seed', now(), 'seed', 'MEMBER')
ON CONFLICT (id) DO NOTHING;

-- Create test users (idempotent by login to avoid duplicates)
-- Passwords here are PBKDF2-HMAC-SHA512 serialized credentials that
-- correspond to the plaintext passwords used in E2E instructions so that
-- test login (testmember/testmember123 and testadmin/testadmin123) works
-- after running this seed. The credentials were generated deterministically
-- with fixed salts so they are safe to include in the seed script.
INSERT INTO users (id, created, created_by, modified, modified_by, login, password, first_name, last_name, email)
SELECT v.id, now(), 'seed', now(), 'seed', v.login, v.password, v.first_name, v.last_name, v.email
FROM (VALUES
  ('user-testadmin','testadmin','{"credentialData":{"hashIterations":210000,"algorithm":"PBKDF2WithHmacSHA512"},"secretData":{"value":"sWRDZH7vJiaVy57fRvG8iBWig0poY2DkNi7lqlHyuIAoo0X0HuSlXBnEIfDpQGDh8uO/PWrENSU3mtP65zPbRg==","salt":[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]},"type":"password"}','Test','Admin','testadmin@example.com'),
  ('user-testmember','testmember','{"credentialData":{"hashIterations":210000,"algorithm":"PBKDF2WithHmacSHA512"},"secretData":{"value":"6ihh5Pqx4Vxfh3tj2PgCxCV+HAL6dgyedPiAOBMefUxJioAlDaj3SPNOf853s7c/58xtsVF3sCxLxz+xvPMglA==","salt":[15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0]},"type":"password"}','Test','Member','testmember@example.com')
) AS v(id, login, password, first_name, last_name, email)
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.login = v.login);

-- Assign roles to users (idempotent using WHERE NOT EXISTS)
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ADMIN' WHERE u.login = 'testadmin' AND NOT EXISTS (
  SELECT 1 FROM user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
);

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'MEMBER' WHERE u.login = 'testmember' AND NOT EXISTS (
  SELECT 1 FROM user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- Create a product representing the course (idempotent by slug)
-- NOTE: products.slug is not defined UNIQUE in the schema, so ON CONFLICT (slug)
-- would fail in PostgreSQL. Use INSERT ... SELECT WHERE NOT EXISTS pattern
-- and generate a stable id based on the slug to avoid accidental PK collisions.
INSERT INTO products (id, created, created_by, modified, modified_by, slug, title, subtitle, filename, listing_image, image, price_regular)
SELECT md5('product-kurz-zdraveho-stravovani'), now(), 'seed', now(), 'seed', 'kurz-zdraveho-stravovani', 'Kurz zdravého stravování', 'Krátký popis kurzu', NULL, NULL, NULL, 0.00
WHERE NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'kurz-zdraveho-stravovani');

-- Create the course record (idempotent by slug)
-- Use a stable id derived from the slug (md5) to avoid collisions with arbitrary
-- fixed ids that might already exist in some DB instances.
INSERT INTO courses (id, created, created_by, modified, modified_by, slug, title, subtitle, listing_image, image, perex)
SELECT md5('course-zdrave-stravovani'), now(), 'seed', now(), 'seed', 'zdrave-stravovani', 'Kurz zdravého stravování', 'Krátký popis kurzu', NULL, NULL, 'Perex kurzu'
WHERE NOT EXISTS (SELECT 1 FROM courses c WHERE c.slug = 'zdrave-stravovani');

-- Optionally create a module for the course (idempotent)
INSERT INTO course_modules (id, course_id, title, image, short_description, perex, sort_order)
SELECT md5('module-1-zdrave-stravovani'), c.id, 'Úvodní modul', NULL, 'Krátký popis modulu', 'Perex modulu', 1
FROM courses c
WHERE c.slug = 'zdrave-stravovani' AND NOT EXISTS (
  SELECT 1 FROM course_modules m WHERE m.course_id = c.id AND m.title = 'Úvodní modul'
);

-- Grant the product to testmember (so member page shows the course in member library)
-- Grant the product to testmember (so member page shows the course in member library)
-- Use deterministic shorter id (md5) to avoid exceeding the 40-char PK limit and to
-- avoid collisions with existing arbitrary ids.
INSERT INTO user_product (id, user_id, product_id, created)
SELECT md5('userproduct-' || u.id || '|' || p.id), u.id, p.id, now()
FROM users u JOIN products p ON p.slug = 'kurz-zdraveho-stravovani'
WHERE u.login = 'testmember' AND NOT EXISTS (
  SELECT 1 FROM user_product up WHERE up.user_id = u.id AND up.product_id = p.id
);

-- End of seed script
