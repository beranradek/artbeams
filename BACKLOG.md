<!-- Machine-maintained: the planning agent rewrites this file each run. Do not edit sections other than "Later / ideas" by hand — your changes will be overwritten. -->
<!-- To suggest work, add items to "## Later / ideas" below. The planner will promote them when capacity opens up. -->

## Next up (ready)
- Deliver member Courses experience with strict access checks and private search (course menu and member pages in `src/main/kotlin/org/xbery/artbeams/members/controller/MemberSectionController.kt`, new `src/main/kotlin/org/xbery/artbeams/courses/controller/**`, member templates under `src/main/resources/templates/member/**`, private-index guard in `src/main/kotlin/org/xbery/artbeams/search/service/SearchIndexer.kt`, and gated article/course routes in `src/main/kotlin/org/xbery/artbeams/web/WebController.kt`).
- Add Course/Module assignment into article admin editor by extending `src/main/kotlin/org/xbery/artbeams/articles/domain/{Article,EditedArticle}.kt`, `src/main/kotlin/org/xbery/artbeams/articles/repository/mapper/*`, `src/main/kotlin/org/xbery/artbeams/articles/admin/{ArticleForm,ArticleAdminController}.kt`, and `src/main/resources/templates/admin/articles/articleEdit.ftl`.
- Add idempotent e2e seed SQL script (`scripts/seed_courses_e2e.sql`) to provision admin/member users, purchased product, course, modules, and linked articles for Chrome DevTools journey tests; include deterministic IDs and safe re-run semantics aligned with `src/main/resources/sql/create_tables.sql` and `src/main/resources/sql/migrations/*.sql`.

## Later / ideas
<!-- To suggest work, add items to "## Later / ideas" below. The planner will promote them when capacity opens up. -->

## Done (recent)
<!-- Issues closed as completed in recent planning cycles. The planner moves items here automatically. -->

## Skipped (recent)
<!-- Items the planner rejected or deferred this cycle, with a one-line rationale. -->