# ArtBeams

**Open source CMS** for blogs with admin interface. Simple, functional.

Built with Spring Boot framework, Kotlin language, JOOQ (and PostgreSQL) for persistence layer, 
Spring Security, Spring MVC for handling requests, Apache FreeMarker and Bootstrap templates, Formio form definition, binding and validation library (http://www.formio.net/documentation/), 
Google APIs for integration with Google Docs, Browscap for browser detection,
HttpClient and Jackson for integration with JSON APIs, 
zxcvbn library for password strength estimation, 
mailgun-java for sending transactional Mailgun emails, 
Apache PDFBox for manipulation of PDF files, 
Scrimage image library for manipulation of images, 
Kotest and MockK for unit tests, Flexmark for markdown editor, CKEditor WYSIWYG editor, 
Evernote API for Evernote integration, Google reCaptcha and more.

## Implemented features

* Public blog with articles, categories, comments, search, products to purchase, integration of social networks, terms and conditions, data privacy and cookies policy, contact form.
* Administration of article categories, articles, roles, users, comments, products and orders. Markdown editor and WYSIWYG editor.
* Simple client zone for customers to view their purchased products.
* Google Doc integration - syncing article content with Google Doc content.
  Application is authorized to access content editor's Google documents via Google OAuth2.
* Evernote integration using simple developer token 
  (real applications authenticate with Evernote using OAuth, but for the purpose of exploring the API, you can get a developer token that allows you to access your own Evernote account. To get a developer token, visit https://www.evernote.com/api/DeveloperToken.action).
* Mailer for Mailgun transactional emails.

## Development

### Conventions

Packages are organized by feature, not by layer.
A feature usually consists of an `admin` subpackage for admin controllers, services and forms; 
of `controller` subpackage for public web controllers, `service` subpackage for services,
`repository` subpackage for repositories and `domain` subpackage for domain data classes. 

### Common package

Package `common` contains common cross-cutting functionalities, patterns and utilities.

For example: `OperationCtx` - operation context; counting of user accesses; `AbstractJsonApi`; 
`Asset` data model with common `AssetAttributes` and `AssetRepository`; 
`AuthorizationCodeGenerator` and `AuthorizationCodeValidator`; 
`BaseController` as base class for public controllers; email validator for advanced email format and domain validation;
`OperationException` as base class of application-specific exception hierarchy and related `Preconditions`,
`ErrorCode` and `StatusCode`, file management utils; 
Formio form library validators and library extensions for Kotlin and Spring file uploads; 
FreeMarker configuration; JSON ObjectMapper; `MailgunMailSender`;
`MarkdownConverter` for conversion of markup to HTML; 
data overview pagination utilities; JOOQ converters;
`AbstractRecordFetcher`, `AbstractRecordStorage` and `AbstractMappingRepository` as abstract repository classes
and interfaces; queue for asynchronous processing of entries with re-tries and exponential backoff; 
encryption, encoding and hashing security utilities; date and text utilities.

### GIT branches

* Remote "origin" branch for GitHub and remote "master" branch for Heroku can be used.
* Pushing from local master to both remotes: "origin" (for GitHub git) and "master" (for Heroku deployment).

### Gradle build

Build using `./gradlew clean build`

Updating gradle wrapper: Update gradle version in build.gradle or springBootVersion of spring-boot-gradle-plugin 
and run: `gradle wrapper`

### Automatic application restarts using Spring Developer Tools

In IDE is usually not compiling immediately the changed sources, manual rebuild is required so the application
is automatically restarted (like Ctrl + F9 in IntelliJ IDEA). 
This involves also changes in templates (but change in a template does not invoke application restart because it is not necessary - templates are not cached in development mode).
(see https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html).

## Quick start guide

* Configure database in application.properties.
* Create DB tables using sql/create*.sql scripts.
* Fill in DB tables using sql/insert*.sql scripts, fill in your own configuration and translations.
* Run application with local config: `./gradlew :bootRun --args='--spring.profiles.active=local'`, or:
* Run Application main class with VM options: `-DJDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/<db-name> -DJDBC_DATABASE_USERNAME=... -DJDBC_DATABASE_PASSWORD=...`

### Running on Heroku

* Add Heroku Procfile (provided)
* In Heroku CLI run command: `heroku ps:scale web=1` (so the application has associated one dyno required for application run and application can start)
* See Heroku documentation for more details
