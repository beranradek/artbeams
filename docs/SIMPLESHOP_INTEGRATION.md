# SimpleShop.cz Integration

This document describes the SimpleShop.cz integration for product synchronization.

## Overview

The integration allows you to synchronize product information (name and price) from SimpleShop.cz to your ArtBeams product catalog. This is a **one-way synchronization** from SimpleShop to ArtBeams.

## Features

- Link products to SimpleShop products via SimpleShop Product ID
- Sync individual products from SimpleShop
- Bulk sync all linked products
- Updates product title and price from SimpleShop
- Admin UI for managing synchronization

## Configuration

### 1. Get SimpleShop API Credentials

1. Log in to your SimpleShop.cz account
2. Navigate to Settings → Integrations → API
3. Note your email and API key

### 2. Configure in Database

Add the following configuration entries to the `config` table in your database:

```sql
INSERT INTO config (entry_key, entry_value) VALUES ('simpleshop.api.baseUrl', 'https://api.simpleshop.cz/v2');
INSERT INTO config (entry_key, entry_value) VALUES ('simpleshop.api.email', 'your-email@example.com');
INSERT INTO config (entry_key, entry_value) VALUES ('simpleshop.api.key', 'your-api-key-here');
```

Or update the values if they already exist:

```sql
UPDATE config SET entry_value = 'your-email@example.com' WHERE entry_key = 'simpleshop.api.email';
UPDATE config SET entry_value = 'your-api-key-here' WHERE entry_key = 'simpleshop.api.key';
```

### 3. Database Schema Update

Add the `simple_shop_product_id` column to the products table:

```sql
ALTER TABLE products ADD COLUMN simple_shop_product_id VARCHAR(64) DEFAULT NULL;
```

### 4. Regenerate JOOQ Classes

After updating the database schema, regenerate JOOQ classes:

```bash
./gradlew generateJooq
```

## Usage

### Linking Products

1. Go to the product edit page in the admin interface (`/admin/products/{id}/edit`)
2. Enter the SimpleShop Product ID in the "SimpleShop Product ID" field
3. Save the product

### Syncing a Single Product

1. Edit a product that has a SimpleShop Product ID set
2. Scroll to the "SimpleShop Synchronization" section
3. Click the "Sync from SimpleShop" button

The system will:
- Fetch the product data from SimpleShop API
- Update the product title if it differs and is non-empty in SimpleShop
- Update the regular price if it differs and is set in SimpleShop
- Display a success or error message

### Bulk Sync All Products

1. Go to the product list page (`/admin/products`)
2. Click the "Sync All from SimpleShop" button
3. Confirm the action

The system will sync all products that have a SimpleShop Product ID set.

## API Limitations

According to the SimpleShop.cz API documentation, the API:
- **Does not support** creating, updating, or deleting products
- **Only supports** reading product information via GET /product/{id}/

This is why the integration is one-way (from SimpleShop to ArtBeams) only.

## Architecture

### Components

1. **SimpleShopConfig** (`org.xbery.artbeams.simpleshop.config.SimpleShopConfig`)
   - Configuration service for API credentials

2. **SimpleShopApiClient** (`org.xbery.artbeams.simpleshop.service.SimpleShopApiClient`)
   - HTTP client for SimpleShop API
   - Handles authentication and API requests
   - Parses product responses

3. **SimpleShopSyncService** (`org.xbery.artbeams.simpleshop.service.SimpleShopSyncService`)
   - Business logic for synchronization
   - Compares local and remote products
   - Updates products when necessary

4. **ProductAdminController** (updated)
   - Added endpoints:
     - `POST /admin/products/{id}/sync-from-simpleshop` - Sync single product
     - `POST /admin/products/sync-all-from-simpleshop` - Bulk sync

### Data Model

- **Product** domain model extended with `simpleShopProductId` field
- **SimpleShopProduct** DTO for API responses

## Error Handling

The integration handles various error scenarios:
- Missing API configuration
- Network errors
- Invalid product IDs
- Missing products in SimpleShop
- API errors

All errors are logged and displayed to the user in the admin interface.

## Logging

The integration logs to the application log with the following information:
- Sync operations start and completion
- Individual product updates
- Errors and warnings
- Success/failure counts for bulk operations

## Security

- API credentials stored in database config table
- Basic HTTP authentication used for API requests
- Admin-only access to sync endpoints (protected by Spring Security)
- CSRF protection on all POST endpoints

## Future Enhancements

Possible future improvements:
- Scheduled automatic synchronization
- Sync history/audit log
- Support for syncing additional fields (subtitle, images, etc.)
- Webhook support if SimpleShop adds it
- Two-way sync if SimpleShop API adds write support
