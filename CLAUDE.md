# Global project instructions

Guidance for working with code in this repository.

## Project Overview

ArtBeams is an open-source CMS for blogs with administration interface, built with Kotlin/Spring Boot. 
It features content management, user authentication, e-commerce capabilities, and integrations 
with Google Docs, Evernote, and email services.

## Development Commands

### Build & Run
```bash
# Clean build
./gradlew clean build

# Run locally
./gradlew bootRun --args='--spring.profiles.active=local'
Application is then available on: http://localhost:8080/

# Generate JOOQ classes (required after database schema changes)
./gradlew jooqCodegen

# Run tests
./gradlew test

# Run specific test
./gradlew test --tests "org.xbery.artbeams.comments.service.SpamDetectorTest"
```

### Linting
```bash
# Check Kotlin code style (ktlint)
./gradlew ktlintCheck

# Auto-fix code style issues
./gradlew ktlintFormat

# Generate baseline for existing violations (to suppress existing issues)
./gradlew ktlintGenerateBaseline
```

**Notes:**
- Run `ktlintCheck` after coding sessions and before commits/releases
- Use `ktlintFormat` to auto-fix most style issues

## Architecture

### Technology Stack
- **Language**: Kotlin 2.4 with Java 25
- **Framework**: Spring Boot 4.1, Spring MVC, Spring Security
- **Database**: PostgreSQL with JOOQ 3.21
- **Templates**: FreeMarker with Bootstrap CSS
- **Testing**: Kotest, MockK
- **Forms**: Formio 1.9.0, form definition, binding and validation library (https://github.com/beranradek/formio)
- SLF4J logger
- Google APIs for integration with Google Docs
- Browscap for browser detection
- Jackson for integration with JSON APIs
- zxcvbn library for password strength estimation
- mailgun-java for sending transactional Mailgun emails
- Apache PDFBox for manipulation of PDF files
- Scrimage image library for manipulation of images
- Flexmark for markdown editor
- CKEditor for WYSIWYG editor
- Evernote API for Evernote integration (not actively used now)
- Google reCaptcha
- Deployed to Heroku

#### Database
- Uses PostgreSQL with JOOQ for type-safe SQL queries
- Database schema in `src/main/resources/sql/create_tables.sql`
- Run `./gradlew jooqCodegen` after schema changes to regenerate classes in `org.xbery.artbeams.jooq.schema` package
- JOOQ generated files (`src/main/jooqGenerated/**`) are excluded from linting

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
- **Repository Pattern**: `AbstractMappingRepository` with mapper/unmapper classes (extends `AbstractRecordStorage` for create, update, delete operations, `AbstractRecordFetcher` for find operations). Every DB operation must go through this repository layer.
- **Service Layer**: Business logic in `*Service` classes. All business logic must be in service layer, not in controllers or repositories.
- **Controller Layer**: Spring MVC controllers - subclasses of `BaseController`. `*AdminController` classes for admin endpoints. 
  `AdminHomeController` handles main administration page. `WebController` handles public website homepage. 
  `MemberSectionController` handles main member section pages. 
  All using org.springframework.web mapping annotations and Freemarker templates.
- **Context Handling**: `OperationCtx` for request context tracking
- **Error Handling**: `OperationException` hierarchy with status codes and 
  `kotlin/org/xbery/artbeams/common/error/Preconditions.kt` for precondition checks throwing `OperationException` subclasses.
  `org.xbery.artbeams.common.controller.BaseController.statusCodeToHttpStatus` maps `OperationException`s `StatusCode` to springframework HttpStatus.
- **Security**: PBKDF2 password hashing, role-based access control. Spring Security configured in `org.xbery.artbeams.config.SecurityConfig`.

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
- Source files: `bootstrap.min.css` and other minified common styles
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
2. Run `./gradlew jooqCodegen` to regenerate classes
3. Update mappers/unmappers if needed
4. Add migration logic if required

### Security Considerations
- Never log sensitive information
- Use PBKDF2 for password hashing (existing utilities available)
- Validate all user inputs (through form validation typically)
- Check authorization at service layer

## Coding best practices

* Packages are organized by feature. Each encapsulated feature has its own package with subpackages: domain, repository, service, controller (for public controllers), admin (for administration controllers) (if applicable).
* DRY principle.
* Use meaningful names for variables, methods, classes.
* Use require checks from Preconditions.kt for handling of missing data or access rights.
* Never create a file longer than 500 lines of code. If a file approaches this limit, refactor by splitting it into more classes or helper files.
* Check correctness of your code, also for the edge cases.
* Use proper error handling and logging. Do NOT use System.err or System.out. Use logger.
* Close all resources properly.
* Check that you have implemented all requested changes.
* Check if the code is time and memory efficient, DB queries optimized.
* Adhere to SOLID OOP principles, especially create well-defined classes or interfaces with single responsibility.
* Make changes in places most local to the parts of code where they will be used.
* Respect and use existing conventions, libraries, patterns that are already present in codebase.
* Create tests for every more complex service (business) logic.
* Do not forget to always add the necessary imports if not present yet.
* Do NOT use wildcard imports, name the imported symbols explicitly.
* Do not forget to remove the code that becomes unused after implementation.
* Never assume missing context.
* Never hallucinate libraries or functions – only use verified facts.
* Always confirm file paths and module names exist before referencing them in code or tests.
* **Never delete or overwrite existing behavior** unless explicitly instructed to do it as part of a task.
* Always communicate security considerations, improvements and issues. Review the code for security issues.

### Testing & Reliability

- **Create unit tests for new features (with mocks)** (functions, classes, routes, etc). Test libraries are already set up.
- **After updating any logic**, check whether existing unit tests need to be updated. If so, do it.
- **Tests should live in a `test` folder** mirroring the main app structure.
- Example of running concrete test: `./gradlew :ai-tools-commons:test --tests "cz.etn.ai.tools.tool.provider.McpToolProvidersTest"`

- Include at least:
    - 1 test for expected use
    - 1 edge case
    - 1 failure case

### Documentation & Explainability

- **Update `README.md`** when new features are added, dependencies change, or setup steps are modified.
- When writing complex logic, **add an inline `// Reason:` comment** explaining the why, not just the what.
