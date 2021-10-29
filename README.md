# ArtBeams

Open source CMS for JVM. Simple, functional.

Main features:

* Simple and rich repository layer for rapid development.
* Markdown and WYSIWYG editor.
* Evernote integration.
* Built with Spring Boot, Kotlin, Freemarker and Bootstrap. 

## Development

### Gradle build

Build using `./gradlew build`

Run using `./gradlew bootRun` or `java -jar build/libs/*.jar`

Updating gradle wrapper: Update gradle version in build.gradle or springBootVersion of spring-boot-gradle-plugin 
and run: gradle wrapper

### Automatic application restarts using Spring Developer Tools

In IDE which is not compiling immediatelly the changed sources, manual rebuild is required so the application
is automatically restarted (like Ctrl + F9 in IntelliJ Idea). Also for changes in templates (but change in a template
does not invoke application restart because it is not necessary - templates are not cached in development mode).
(see https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html).

## Project setup

* Configure database in application.properties.
* Create DB tables using sql/create*.sql scripts.
* Fill in DB tables using sql/insert*.sql scripts, fill in your own configuration and translations.
* Run Application main class with VM options: -DJDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/<db-name> -DJDBC_DATABASE_USERNAME=... -DJDBC_DATABASE_PASSWORD=... -DMAILGUN_API_KEY=... -DMAILGUN_DOMAIN=...

### Running on Heroku

* Add Heroku Procfile (provided)
* In Heroku CLI run command: heroku ps:scale web=1 (so the application has associated one dyno required for application run and application can start)

