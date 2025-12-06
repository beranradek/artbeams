-- Migration: Enhancement Phase 1 - Orders table updates and User Activity Log
-- Purpose: Add e-commerce tracking and user activity logging capabilities
-- Date: 2024-12-06
-- Related features: Order paid time tracking, payment method, admin notes, user activity logging

-- =====================================================
-- ORDERS TABLE ENHANCEMENTS
-- =====================================================

-- Add paid_time column to track when payment was confirmed
ALTER TABLE orders ADD COLUMN IF NOT EXISTS paid_time TIMESTAMP DEFAULT NULL;

-- Add payment_method to track how the order was paid (card, transfer, paypal, etc.)
ALTER TABLE orders ADD COLUMN IF NOT EXISTS payment_method VARCHAR(32) DEFAULT NULL;

-- Add notes column for admin notes about the order
ALTER TABLE orders ADD COLUMN IF NOT EXISTS notes TEXT DEFAULT NULL;

-- Add index for paid_time for efficient querying of paid orders
CREATE INDEX IF NOT EXISTS idx_orders_paid_time ON orders (paid_time);

-- Add index for payment_method for filtering by payment type
CREATE INDEX IF NOT EXISTS idx_orders_payment_method ON orders (payment_method);

-- =====================================================
-- USER ACTIVITY LOG TABLE
-- =====================================================

-- Create user_activity_log table for tracking user actions
CREATE TABLE IF NOT EXISTS user_activity_log (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    user_id VARCHAR(40) NOT NULL,
    action_type VARCHAR(32) NOT NULL,
    action_time TIMESTAMP NOT NULL,
    entity_type VARCHAR(20) DEFAULT NULL,
    entity_id VARCHAR(40) DEFAULT NULL,
    ip_address VARCHAR(60) DEFAULT NULL,
    user_agent VARCHAR(200) DEFAULT NULL,
    details TEXT DEFAULT NULL
);

-- Add foreign key constraint to users table
ALTER TABLE user_activity_log ADD CONSTRAINT IF NOT EXISTS user_activity_log_user_fk
    FOREIGN KEY (user_id) REFERENCES users (id);

-- Index on user_id for efficient user activity lookups
CREATE INDEX IF NOT EXISTS idx_user_activity_log_user_id ON user_activity_log (user_id);

-- Index on action_time for temporal queries and sorting
CREATE INDEX IF NOT EXISTS idx_user_activity_log_action_time ON user_activity_log (action_time);

-- Index on action_type for filtering by action
CREATE INDEX IF NOT EXISTS idx_user_activity_log_action_type ON user_activity_log (action_type);

-- Composite index for entity lookups
CREATE INDEX IF NOT EXISTS idx_user_activity_log_entity ON user_activity_log (entity_type, entity_id);

-- Composite index for user + time range queries (most common query pattern)
CREATE INDEX IF NOT EXISTS idx_user_activity_log_user_time ON user_activity_log (user_id, action_time DESC);

-- =====================================================
-- COMMENTS
-- =====================================================

-- Migration script successfully created!
-- After running this script, remember to:
-- 1. Run: ./gradlew generateJooq
-- 2. Update Order domain class with new fields
-- 3. Update OrderMapper and OrderUnmapper
-- 4. Create UserActivityLog domain class
-- 5. Create UserActivityLog repository and mappers
