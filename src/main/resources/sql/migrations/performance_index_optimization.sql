-- Performance optimization: Add missing indexes for common query patterns and foreign keys
-- Run this migration to improve database query performance

-- Article-Category junction table indexes (for joins)
CREATE INDEX IF NOT EXISTS idx_article_category_article_id ON article_category (article_id);
CREATE INDEX IF NOT EXISTS idx_article_category_category_id ON article_category (category_id);

-- User-Role junction table indexes (for joins)
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role (user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON user_role (role_id);

-- Order items indexes (for joins and queries)
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items (order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items (product_id);

-- Users table indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_created_by ON users (created_by);

-- Articles table indexes
CREATE INDEX IF NOT EXISTS idx_articles_slug ON articles (slug);
CREATE INDEX IF NOT EXISTS idx_articles_created_by ON articles (created_by);
CREATE INDEX IF NOT EXISTS idx_articles_show_on_blog ON articles (show_on_blog);
CREATE INDEX IF NOT EXISTS idx_articles_created ON articles (created DESC);

-- Categories table indexes
CREATE INDEX IF NOT EXISTS idx_categories_slug ON categories (slug);

-- Products table indexes
CREATE INDEX IF NOT EXISTS idx_products_slug ON products (slug);

-- Orders table additional indexes
CREATE INDEX IF NOT EXISTS idx_orders_created_by ON orders (created_by);
CREATE INDEX IF NOT EXISTS idx_orders_state ON orders (state);
CREATE INDEX IF NOT EXISTS idx_orders_created ON orders (created DESC);

-- Comments table additional indexes
CREATE INDEX IF NOT EXISTS idx_comments_created ON comments (created DESC);
CREATE INDEX IF NOT EXISTS idx_comments_created_by ON comments (created_by);
CREATE INDEX IF NOT EXISTS idx_comments_email ON comments (email);

-- User product table indexes
CREATE INDEX IF NOT EXISTS idx_user_product_user_id ON user_product (user_id);
CREATE INDEX IF NOT EXISTS idx_user_product_product_id ON user_product (product_id);
CREATE INDEX IF NOT EXISTS idx_user_product_composite ON user_product (user_id, product_id);

-- Performance notes:
-- These indexes improve:
-- 1. JOIN performance on foreign keys
-- 2. WHERE clause filtering on slug, state, created_by fields
-- 3. ORDER BY performance on created timestamps
-- 4. User lookups by email
-- 5. Article and product URL resolution (slug lookups)
