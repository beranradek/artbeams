# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ArtBeams is an open-source CMS for blogs with administration interface, built with Kotlin/Spring Boot. It features content management, user authentication, e-commerce capabilities, and integrations with Google Docs, Evernote, and email services.

## Development Commands

### Build & Run
```bash
# Clean build
./gradlew clean build

# Run locally
./gradlew bootRun --args='--spring.profiles.active=local'
Application is then available on: http://localhost:8080/

# Generate JOOQ classes (required after database schema changes)
./gradlew generateJooq

# Run tests
./gradlew test

# Run specific test
./gradlew test --tests "org.xbery.artbeams.comments.service.SpamDetectorTest"
```

### Database
- Uses PostgreSQL with JOOQ for type-safe SQL queries
- Database schema in `src/main/resources/sql/create_tables.sql`
- Run `./gradlew generateJooq` after schema changes to regenerate classes
- JOOQ generated classes in `org.xbery.artbeams.jooq.schema` package

## Architecture

### Technology Stack
- **Language**: Kotlin 1.9.22 with Java 21
- **Framework**: Spring Boot 3.1.5, Spring MVC, Spring Security
- **Database**: PostgreSQL with JOOQ 3.18.7
- **Templates**: FreeMarker with Bootstrap CSS
- **Testing**: Kotest, MockK
- **Forms**: Formio 1.7.0 for validation and binding

### Package Structure
Uses **feature-based packaging** (not layer-based):
```
org.xbery.artbeams/
├── articles/ (article management)
├── categories/ (category system) 
├── comments/ (comments with spam detection)
├── users/ (authentication, roles, password management)
├── orders/ (e-commerce orders)
├── products/ (product catalog)
├── common/ (shared infrastructure)
└── web/ (public controllers)
```

### Key Patterns
- **Repository Pattern**: `AbstractMappingRepository` with mapper/unmapper classes
- **Context Handling**: `OperationCtx` for request context tracking
- **Error Handling**: `OperationException` hierarchy with status codes
- **Security**: PBKDF2 password hashing, role-based access control

## Database Access

### JOOQ Integration
- All database access through JOOQ generated classes
- Each feature has Repository/Mapper/Unmapper pattern:
  - Repository: Data access logic
  - Mapper: Database record → Domain object  
  - Unmapper: Domain object → Database record
- Custom converters in `common.persistence.jooq.converter`

### Core Entities
- Users & Roles: `users`, `roles`, `user_role`
- Content: `articles`, `categories`, `article_category`
- Comments: `comments` with spam detection
- E-commerce: `products`, `orders`, `order_items`
- Media: `media_files` with image processing

## Configuration

### Local Development
- Copy `application-local-template.yml` to `application-local.yml`
- Configure database connection and API keys
- Use `--spring.profiles.active=local` when running

### Environment Variables (Production)
- `JDBC_DATABASE_URL`, `JDBC_DATABASE_USERNAME`, `JDBC_DATABASE_PASSWORD`
- Google API credentials for OAuth2 and Docs integration
- Mailgun configuration for email delivery
- reCAPTCHA keys for anti-spam protection

## Key Features

### Content Management
- Articles with Markdown/WYSIWYG editing
- Category-based organization  
- Comment system with spam filtering
- Media file management with image transformation

### User System
- Role-based access (ADMIN, MEMBER)
- Password recovery/setup with email
- Login-as functionality for admins
- User profile management

### E-commerce
- Product catalog with pricing
- Order management and admin creation
- User product library access

### Integrations
- Google Docs: OAuth2 content sync
- Evernote: Note import
- Mailgun: Email delivery
- reCAPTCHA: Anti-spam

## Testing

- Tests mirror main package structure in `src/test/kotlin`
- Kotest for assertions, MockK for mocking
- Test clock configuration available in `TestClockConfiguration`
- Focus on business logic testing (services, utilities, validators)

## CSS Build Process

- Automatic CSS minification during build
- Combines Bootstrap + custom styles into `styles.css`
- Source files: `bootstrap.min.css`, `common-styles.css`, `public-styles.css`
- CSS minification is handled by the `minifyCss` Gradle task.
- The `minifyCss` task is automatically run before `processResources` (not directly by `build`).

## Common Development Tasks

### Adding New Feature
1. Create feature package with `domain`, `repository`, `service`, `admin` sub-packages
2. Create domain classes with proper validation
3. Implement Repository with Mapper/Unmapper
4. Add Service layer with business logic
5. Create Controllers for admin and/or public interfaces
6. Add corresponding templates in `src/main/resources/templates`

### Database Changes
1. Update SQL schema in `src/main/resources/sql/create_tables.sql`
2. Run `./gradlew generateJooq` to regenerate classes
3. Update mappers/unmappers if needed
4. Add migration logic if required

### Security Considerations
- Never log sensitive information
- Use PBKDF2 for password hashing (existing utilities available)
- Validate all user inputs (through form validation typically)
- Check authorization at service layer
