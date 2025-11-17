-- Migration: Add simple_shop_product_id column to products table
-- Date: 2025-11-16

ALTER TABLE products
ADD COLUMN IF NOT EXISTS simple_shop_product_id VARCHAR(64) DEFAULT NULL;

-- Verify column was added
SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns
WHERE table_name = 'products' AND column_name = 'simple_shop_product_id';
