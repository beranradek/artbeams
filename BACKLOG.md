<!-- Machine-maintained: the planning agent rewrites this file each run. Do not edit sections other than "Later / ideas" by hand — your changes will be overwritten. -->
<!-- To suggest work, add items to "## Later / ideas" below. The planner will promote them when capacity opens up. -->

## Next up (ready)
- Implement Courses+Modules persistence and admin CRUD (schema, repositories, services, `/admin/courses` UI) touching `src/main/resources/sql/create_tables.sql`, `src/main/resources/sql/migrations/`, and new `src/main/kotlin/org/xbery/artbeams/courses/**` files plus `src/main/resources/templates/admin/courses/**`.
- Add Course/Module assignment into article admin editor by extending `src/main/kotlin/org/xbery/artbeams/articles/domain/{Article,EditedArticle}.kt`, `articles/repository/mapper/*`, `articles/admin/{ArticleForm,ArticleAdminController}.kt`, and `src/main/resources/templates/admin/articles/articleEdit.ftl`.
- Deliver member Courses experience with access checks and private search (course menu and pages in member section, plus private-index guard in `src/main/kotlin/org/xbery/artbeams/search/service/SearchIndexer.kt` and gated article/course routes).

## Later / ideas
- Add idempotent e2e seed SQL script (`scripts/seed_courses_e2e.sql`) to provision admin/member users, purchased product, course, modules, and linked articles for Chrome DevTools journey tests; this is needed for repeatable sprint DoD verification but should follow core feature scaffolding.

## Done (recent)
<!-- Issues closed as completed in recent planning cycles. The planner moves items here automatically. -->

## Skipped (recent)
<!-- Items the planner rejected or deferred this cycle, with a one-line rationale. -->