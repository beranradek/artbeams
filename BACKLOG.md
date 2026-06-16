## Next up (ready)


-

## Later / ideas

- Implement Course detail pages and member-side course navigation, search and per-course article listing by creating src/main/kotlin/org/xbery/artbeams/courses/controller/CourseController.kt, templates under src/main/resources/templates/member/{courseDetail.ftl,moduleDetail.ftl,courseSearchResults.ftl}, updating src/main/kotlin/org/xbery/artbeams/members/controller/MemberSectionController.kt and src/main/resources/templates/member/memberSection.ftl, and guarding public article access in the public article controller — REASON: this item is already in-flight as issue #41 (in-progress); removed from Next up to avoid duplicate proposals.

## Done (recent)

- Add Course/Module assignment into article admin editor by extending src/main/kotlin/org/xbery/artbeams/articles/domain/{Article,EditedArticle}.kt, src/main/kotlin/org/xbery/artbeams/articles/repository/mapper/*, src/main/kotlin/org/xbery/artbeams/articles/admin/{ArticleForm,ArticleAdminController}.kt, and src/main/resources/templates/admin/articles/articleEdit.ftl — enable admins to assign an Article to a Course (and optionally to a Module).
- Add Course and Module domain classes and Asset-backed repository (src/main/kotlin/org/xbery/artbeams/courses/domain/Course.kt, Module.kt and src/main/kotlin/org/xbery/artbeams/courses/repository/CourseRepository.kt)

## Skipped (recent)

-
