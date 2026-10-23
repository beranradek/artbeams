# ArtBeams

**Open source CMS** for blogs with admin interface. Simple, functional.

Built with Spring Boot framework, Kotlin language, JOOQ (and PostgreSQL) for persistence layer, 
Spring Security, Spring MVC for handling requests, Apache FreeMarker and Bootstrap templates, Formio form definition, binding and validation library (https://github.com/beranradek/formio), 
Google APIs for integration with Google Docs, Browscap for browser detection,
HttpClient and Jackson for integration with JSON APIs, 
zxcvbn library for password strength estimation, 
mailgun-java for sending transactional Mailgun emails, 
Apache PDFBox for manipulation of PDF files, 
Scrimage image library for manipulation of images, 
Kotest and MockK for unit tests, Flexmark for markdown editor, CKEditor WYSIWYG editor, 
Evernote API for Evernote integration, Google reCaptcha and more.

## Implemented features

* SEO and GEO optimized: supports SEO-friendly URLs, meta tags, sitemap generation and basic geo-targeting considerations.
* Public blog with articles, categories, comments, search, products to purchase, integration of social networks, terms and conditions, data privacy and cookies policy, contact form.
* Administration of article categories, articles, roles, users, comments, products and orders. Markdown editor and WYSIWYG editor.
* Simple client zone for customers to view their purchased products.
* Google Doc integration - syncing article content with Google Doc content.
  Application is authorized to access content editor's Google documents via Google OAuth2.
* Evernote integration using simple developer token 
  (real applications authenticate with Evernote using OAuth, but for the purpose of exploring the API, you can get a developer token that allows you to access your own Evernote account. To get a developer token, visit https://www.evernote.com/api/DeveloperToken.action).
* Mailer for Mailgun transactional emails.
* MailerLite webhook integration for real-time subscriber status synchronization.

## MailerLite Webhook Configuration

To enable automatic synchronization of subscriber unsubscribe events with your ArtBeams database:

1. **Configure webhook secret**: Add `mailerlite.webhook.secret` to your application configuration
2. **Register webhook in MailerLite**:
   - Go to MailerLite dashboard → Integrations → Webhooks
   - Create new webhook with URL: `https://yourdomain.com/api/webhook/mailerlite/events`
   - Select events: `subscriber.unsubscribed`, `subscriber.removed_from_group`
   - Use your configured secret for authentication
3. The webhook will automatically update user consent status in your database when users unsubscribe

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

* **Default Admin User**: A default admin user is created on the first application run. Change the password of admin user!
  * **Login**: `admin`
  * **Password**: `adminadmin`

### Troubleshooting

* **Localization Encoding**: If you encounter missing localization keys (e.g., `InvalidReferenceException: ... xlat['news.form.intro']`), it might be due to encoding issues when running `insert_localisation.sql`. On Windows, ensure you run the script with UTF-8 encoding:
  ```powershell
  $env:PGCLIENTENCODING='UTF8'; psql -U postgres -d artbeams -f src/main/resources/sql/insert_localisation.sql
  ```

### Running on Heroku

* Add Heroku Procfile (provided)
* In Heroku CLI run command: `heroku ps:scale web=1` (so the application has associated one dyno required for application run and application can start)
* See Heroku documentation for more details

#### JVM memory settings on Heroku

Basic dyno has 512 MB RAM and 1 shared vCPU. Without tuning, the process used about 670 MB and Heroku logged R14
"Memory quota exceeded". The buildpack sets only `-Xmx300m`, but the JVM uses much more memory than the heap.
`Procfile` passes `$JAVA_OPTS` to the JVM, so the limits are set as a Heroku config variable (no code change):

`heroku config:set -a <app-name> JAVA_OPTS="-XX:+UseSerialGC -Xmx256m -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=64m -XX:NativeMemoryTracking=summary"`

Memory budget (approximate, the sum must stay under 512 MB):

| Area | Limit | Why |
| --- | --- | --- |
| Java heap | `-Xmx256m` (buildpack default 300m) | Objects of the app. Lower than 300m to leave room for the areas below. |
| Metaspace | `-XX:MaxMetaspaceSize=128m` | Loaded classes (Spring, JOOQ, Kotlin, libraries). Unlimited by default. 128m is enough, OutOfMemoryError means raise it. |
| JIT code cache | `-XX:ReservedCodeCacheSize=64m` | Compiled native code. Default is 240m. 64m is enough for this app size. |
| Thread stacks | `-Xss512k` (buildpack) | About 0.5 MB per thread (Tomcat, DB pool, schedulers). |
| GC structures | `-XX:+UseSerialGC` | See below. |

Details:

* `-XX:+UseSerialGC` - single-threaded GC with the smallest memory footprint. Default G1 needs extra memory (tens of MB)
  and parallel threads, which give no speedup on a 256 MB heap and one shared vCPU. Pauses are short on a small heap.
* `-Xmx256m` - heap is only part of the process memory, so it must be clearly below the dyno quota.
* `-XX:MaxMetaspaceSize`, `-XX:ReservedCodeCacheSize` - hard limits against uncontrolled growth of non-heap areas.
* `-XX:NativeMemoryTracking=summary` - small overhead, allows a breakdown: `jcmd <pid> VM.native_memory summary`.

Check memory with `heroku logs -a <app-name> | grep -e R14 -e "Process running mem"`. Heroku logs these lines only when
the dyno is over quota, so no lines means the settings work. If R14 stays, lower `-Xmx` or use a bigger dyno.

#### GitHub Actions deploy

This repo contains a GitHub Actions workflow that builds the application and deploys it to an existing Heroku app via Heroku Git.

Required GitHub configuration for repository `beranradek/artbeams`:

1) Add repository secret:
   - `HEROKU_API_KEY`
2) Add repository variable:
   - `HEROKU_APP_NAME`
3) Ensure the Heroku app already exists and the API key has access to it.

How to run the workflow:

- Automatically on push to `master` when the commit message contains `[deploy]`
- Manually from GitHub Actions via the `Run workflow` button
- Manually from CLI:
  - `gh workflow run deploy-heroku.yml --ref master`

What the workflow does:

- checks out the full Git history (`fetch-depth: 0`) because Heroku rejects pushes from shallow clones
- runs `./gradlew build`
- verifies access to the target Heroku app with `heroku apps:info`
- deploys with `git push heroku HEAD:main`
