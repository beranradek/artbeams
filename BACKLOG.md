## Next up (ready)

- Add Course/Module assignment into article admin editor by extending src/main/kotlin/org/xbery/artbeams/articles/domain/{Article,EditedArticle}.kt, src/main/kotlin/org/xbery/artbeams/articles/repository/mapper/*, src/main/kotlin/org/xbery/artbeams/articles/admin/{ArticleForm,ArticleAdminController}.kt, and src/main/resources/templates/admin/articles/articleEdit.ftl — enable admins to assign an Article to a Course (and optionally to a Module).


## Later / ideas
<!-- To suggest work, add items to "## Later / ideas" below. The planner will promote them when capacity opens up. -->
- (seed) Implement Course detail pages and member-side course navigation/menu, search and per-course article listing (requires Course/Module admin + article assignment). Moved to Later until admin assignment and access control are complete.

## Done (recent)
<!-- Issues closed as completed in recent planning cycles. The planner moves items here automatically. -->
- Add Course and Module domain classes and Asset-backed repository (src/main/kotlin/org/xbery/artbeams/courses/domain/Course.kt, Module.kt and src/main/kotlin/org/xbery/artbeams/courses/repository/CourseRepository.kt)

## Skipped (recent)
<!-- Items the planner rejected or deferred this cycle, with a one-line rationale. -->
