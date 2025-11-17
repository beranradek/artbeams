-- Migration script to add 'id' column to media table and change primary key
-- This script updates the production database to match the local schema

BEGIN;

-- Step 1: Drop the existing primary key constraint
ALTER TABLE media DROP CONSTRAINT media_pkey;

-- Step 2: Add the 'id' column (not null with default UUID for existing rows)
ALTER TABLE media ADD COLUMN id VARCHAR(128);

-- Step 3: Generate UUIDs for existing rows
UPDATE media SET id = gen_random_uuid()::text WHERE id IS NULL;

-- Step 4: Make id column NOT NULL
ALTER TABLE media ALTER COLUMN id SET NOT NULL;

-- Step 5: Add new primary key constraint on 'id'
ALTER TABLE media ADD CONSTRAINT media_pkey PRIMARY KEY (id);

-- Step 6: Verify the change
-- \d media

COMMIT;
