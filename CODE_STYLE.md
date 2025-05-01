## Follow these coding best practices:

* Application is written in Kotlin language using Spring Boot with Spring MVC controllers, 
  Freemarker template engine, Bootstrap for design (CSS and JavaScript), custom pure JavaScript, JOOQ for persistence (PostgreSQL DB), 
  Formio form definition, binding and validation library (http://www.formio.net/documentation/),
  Google APIs for integration with Google Docs, 
  Browscap for browser detection, Jackson for integration with JSON APIs, 
  zxcvbn library for password strength estimation, mailgun-java for sending transactional Mailgun emails, 
  Apache PDFBox for manipulation of PDF files, Scrimage image library for manipulation of images, 
  Flexmark for markdown editor, CKEditor for WYSIWYG editor, Evernote API for Evernote integration, Google reCaptcha,
  Kotest and MockK for unit tests.
* Application is built using Gradle (gradlew) and deployed to Heroku.  
* Packages are organized by feature. Each encapsulated feature has its own package with subpackages: domain, repository, service, controller (for public controllers), admin (for administration controllers) (if applicable).
* Create reusable methods and classes, especially avoid the code duplication.
* Use meaningful names for variables, methods, classes, etc.
* Never create a file longer than 500 lines of code. If a file approaches this limit, refactor by splitting it into more classes or helper files. 
* Check correctness of your code, also for the edge cases.
* Use proper error handling and logging. Do not use System.err or System.out, but SLF4J logger.
* Close all resources properly.
* Check that you have implemented all requested changes.
* Check if the code is time and memory efficient.
* Use constructions idiomatic for programming language.
* Adhere to SOLID object oriented design principles, especially create well defined classes or interfaces with single responsibility.
* Make changes in places most local to the parts of code where they will be used. Do not duplicate the code.
* Respect and use existing conventions, libraries, etc that are already present in the code base.
* Create tests for every more complex service (business) logic.
* Do not forget to always add the necessary imports if not present yet.
* Do NOT use wildcard imports, name the imported symbols explicitly.
* Maintain exact indentation.
* Copy unchanged lines exactly. Include all details word-for-word.
* Do not forget to remove the code that becomes unused after implementation.
* Never assume missing context.
* Never hallucinate libraries or functions – only use known, verified libraries.
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
- **Comment non-obvious code** and ensure everything is understandable to a mid-level developer.
- When writing complex logic, **add an inline `// Reason:` comment** explaining the why, not just the what.
