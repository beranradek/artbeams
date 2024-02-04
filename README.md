# ArtBeams

Open source CMS for JVM. Simple, functional.

Main features:

* Simple and rich repository layer for rapid development.
* Markdown and WYSIWYG editor.
* Google Doc integration - syncing article content with Google Doc content.
  Application is authorized to access content editor's Google documents via Google OAuth2.
* Evernote integration.
  * Real applications authenticate with Evernote using OAuth, but for the purpose of exploring the API, you can get a developer token that allows you to access your own Evernote account. To get a developer token, visit https://www.evernote.com/api/DeveloperToken.action
* Built with Spring Boot, Kotlin, Freemarker and Bootstrap. 

## Development

## GIT branches

* One remote "origin" for github and one remote "master" for heroku.
* Pushing from local master to both remotes: "origin" (for github git) and "master" (for deployment).

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

