-- Usage:
-- psql -U artbeams_user -d artbeams -f scripts/seed_courses_e2e.sql
--
-- Idempotent seed for Courses E2E tests.
-- Safe to re-run: objects are created IF NOT EXISTS and inserts use deterministic IDs
-- so running the script multiple times does not create duplicates.

BEGIN;

-- Create courses related tables if they don't exist yet (idempotent)
CREATE TABLE IF NOT EXISTS courses (
  id VARCHAR(40) NOT NULL PRIMARY KEY,
  created timestamp NOT NULL,
  modified timestamp NOT NULL,
  -- Align with project conventions: creator / modifier user references (nullable)
  created_by VARCHAR(40) DEFAULT NULL,
  modified_by VARCHAR(40) DEFAULT NULL,
  slug VARCHAR(128) NOT NULL,
  title VARCHAR(128) NOT NULL,
  perex VARCHAR(2000) DEFAULT NULL,
  description TEXT DEFAULT NULL,
  draft boolean NOT NULL DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_courses_slug ON courses (slug);

CREATE TABLE IF NOT EXISTS modules (
  id VARCHAR(40) NOT NULL PRIMARY KEY,
  created timestamp NOT NULL,
  modified timestamp NOT NULL,
  -- Align with project conventions: creator / modifier user references (nullable)
  created_by VARCHAR(40) DEFAULT NULL,
  modified_by VARCHAR(40) DEFAULT NULL,
  course_id VARCHAR(40) NOT NULL,
  slug VARCHAR(128) DEFAULT NULL,
  title VARCHAR(128) DEFAULT NULL,
  description TEXT DEFAULT NULL,
  sort_order integer DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_modules_course_id ON modules (course_id);
CREATE INDEX IF NOT EXISTS idx_modules_course_sort ON modules (course_id, sort_order);

CREATE TABLE IF NOT EXISTS product_course (
  product_id VARCHAR(40) NOT NULL,
  course_id VARCHAR(40) NOT NULL,
  PRIMARY KEY (product_id, course_id)
);
CREATE INDEX IF NOT EXISTS idx_product_course_product_id ON product_course (product_id);
CREATE INDEX IF NOT EXISTS idx_product_course_course_id ON product_course (course_id);

-- Add course/module columns to articles if missing (safe to run multiple times)
ALTER TABLE articles ADD COLUMN IF NOT EXISTS course_id VARCHAR(40) DEFAULT NULL;
ALTER TABLE articles ADD COLUMN IF NOT EXISTS module_id VARCHAR(40) DEFAULT NULL;

-- Add indexes for article course/module lookups
CREATE INDEX IF NOT EXISTS idx_articles_course_id ON articles (course_id);
CREATE INDEX IF NOT EXISTS idx_articles_module_id ON articles (module_id);

-- Add foreign key constraints if missing. Use plpgsql blocks to check existence first.
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_modules_course_id') THEN
    ALTER TABLE modules ADD CONSTRAINT fk_modules_course_id FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_course_product') THEN
    ALTER TABLE product_course ADD CONSTRAINT fk_product_course_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_course_course') THEN
    ALTER TABLE product_course ADD CONSTRAINT fk_product_course_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_articles_course') THEN
    ALTER TABLE articles ADD CONSTRAINT fk_articles_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_articles_module') THEN
    ALTER TABLE articles ADD CONSTRAINT fk_articles_module FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE SET NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_courses_created_by') THEN
    ALTER TABLE courses ADD CONSTRAINT fk_courses_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_courses_modified_by') THEN
    ALTER TABLE courses ADD CONSTRAINT fk_courses_modified_by FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE SET NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_modules_created_by') THEN
    ALTER TABLE modules ADD CONSTRAINT fk_modules_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_modules_modified_by') THEN
    ALTER TABLE modules ADD CONSTRAINT fk_modules_modified_by FOREIGN KEY (modified_by) REFERENCES users(id) ON DELETE SET NULL;
  END IF;
END$$;

-- Insert roles (deterministic ids)
INSERT INTO roles (id, created, created_by, modified, modified_by, name)
VALUES ('role-admin', now(), 'seed', now(), 'seed', 'ADMIN')
ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, created, created_by, modified, modified_by, name)
VALUES ('role-member', now(), 'seed', now(), 'seed', 'MEMBER')
ON CONFLICT (id) DO NOTHING;

-- Insert users testadmin and testmember. Use unique login to avoid duplicates.
-- Passwords are dummy values for testing existence only.
INSERT INTO users (id, created, created_by, modified, modified_by, login, password, first_name, last_name, email)
VALUES ('user-testadmin', now(), 'seed', now(), 'seed', 'testadmin', 'password-not-secure', 'Test', 'Admin', 'testadmin@example.com')
ON CONFLICT (login) DO UPDATE SET modified = now(), modified_by = 'seed';

INSERT INTO users (id, created, created_by, modified, modified_by, login, password, first_name, last_name, email)
VALUES ('user-testmember', now(), 'seed', now(), 'seed', 'testmember', 'password-not-secure', 'Test', 'Member', 'testmember@example.com')
ON CONFLICT (login) DO UPDATE SET modified = now(), modified_by = 'seed';

-- Grant roles to users (idempotent using WHERE NOT EXISTS)
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.login = 'testadmin' AND r.id = 'role-admin'
  AND NOT EXISTS (SELECT 1 FROM user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.login = 'testmember' AND r.id = 'role-member'
  AND NOT EXISTS (SELECT 1 FROM user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- Insert a deterministic product that represents the course product
INSERT INTO products (id, created, created_by, modified, modified_by, slug, title, price_regular)
VALUES ('prod-kurz-zdrave', now(), 'seed', now(), 'seed', 'kurz-zdrave-stravovani', 'Kurz zdravého stravování', 1000.00)
ON CONFLICT (id) DO UPDATE SET modified = now(), modified_by = 'seed', title = EXCLUDED.title;

-- Insert the course
INSERT INTO courses (id, created, modified, slug, title, perex, draft)
VALUES ('course-zdrave-stravovani', now(), now(), 'zdrave-stravovani', 'Kurz zdravého stravování', 'Krátký kurs o zdravém stravování pro testy.', false)
ON CONFLICT (id) DO UPDATE SET modified = now();

-- Create two modules for the course
INSERT INTO modules (id, created, modified, course_id, slug, title, sort_order)
VALUES ('module-zs-1', now(), now(), 'course-zdrave-stravovani', 'zdrave-stravovani-modul-1', 'Modul 1 - Úvod', 1)
ON CONFLICT (id) DO UPDATE SET modified = now();

INSERT INTO modules (id, created, modified, course_id, slug, title, sort_order)
VALUES ('module-zs-2', now(), now(), 'course-zdrave-stravovani', 'zdrave-stravovani-modul-2', 'Modul 2 - Pokročilé', 2)
ON CONFLICT (id) DO UPDATE SET modified = now();

-- Assign product to course (many-to-many)
INSERT INTO product_course (product_id, course_id)
VALUES ('prod-kurz-zdrave', 'course-zdrave-stravovani')
ON CONFLICT (product_id, course_id) DO NOTHING;

-- Create three course-scoped articles
INSERT INTO articles (id, created, created_by, modified, modified_by, slug, title, perex, body, draft, course_id)
VALUES
  ('article-zs-1', now(), 'seed', now(), 'seed', 'zdrave-1', 'Zdravé jídlo 1', 'Perex 1', 'Tělo článku 1', false, 'course-zdrave-stravovani'),
  ('article-zs-2', now(), 'seed', now(), 'seed', 'zdrave-2', 'Zdravé jídlo 2', 'Perex 2', 'Tělo článku 2', false, 'course-zdrave-stravovani'),
  ('article-zs-3', now(), 'seed', now(), 'seed', 'zdrave-3', 'Zdravé jídlo 3', 'Perex 3', 'Tělo článku 3', false, 'course-zdrave-stravovani')
ON CONFLICT (id) DO UPDATE SET modified = now();

-- Ensure the testmember has access to the product (user_product) so membership checks pass.
INSERT INTO user_product (id, user_id, product_id, created)
SELECT 'user_product-testmember-prod', u.id, p.id, now()
FROM users u, products p
WHERE u.login = 'testmember' AND p.id = 'prod-kurz-zdrave'
  AND NOT EXISTS (SELECT 1 FROM user_product up WHERE up.user_id = u.id AND up.product_id = p.id);

COMMIT;

-- End of seed script. Safe to re-run.
