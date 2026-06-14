# Courses Feature — Sprint Memory & Implementation Plan

**Goal:** Add private named article sets called Courses (with optional Modules) accessible only to member users who purchased an assigned product, with admin management, member-section navigation, and per-course search.

**Architecture:** Course and Module are new Asset-derived entities; articles gain nullable `course_id`/`module_id` columns. Access is gated by the existing `user_product` → `product_course` join. Course articles are excluded from the public SearchIndexer. Course content is served via `/clenska-sekce/kurzy/` routes with membership checks.

**Tech Stack:** Kotlin/Spring Boot, JOOQ (code-gen from schema), FreeMarker templates, net.formio for form binding, PostgreSQL, Bootstrap 5.3.

---

## Codebase Facts (Grounding Research)

### Key Patterns to Follow

| Pattern | Example to mirror |
|---|---|
| Asset base class | `common/assets/domain/Asset.kt` — `abstract val common: AssetAttributes` |
| Repository | `common/assets/repository/AssetRepository<T, R>` — parameterised by domain + JOOQ record |
| Mapper/Unmapper | `articles/repository/mapper/ArticleMapper.kt` / `ArticleUnmapper.kt` |
| Form DTO | `articles/domain/EditedArticle.kt` + `articles/admin/ArticleForm.kt` (net.formio `FormMapping`) |
| Service interface | `articles/service/ArticleService.kt` — interface + `ArticleServiceImpl.kt` |
| Admin controller | `articles/admin/ArticleAdminController.kt` — `/admin/articles`, list/editForm/save/delete |
| Member controller | `members/controller/MemberSectionController.kt` — uses `loginService.requireLoggedUser` |
| Migration | `sql/migrations/add_faq_entries_table.sql` — plain SQL, idempotent `CREATE TABLE IF NOT EXISTS` |

### Existing Relevant Infrastructure

- **`user_product` table** — join between users and products (accessed via `UserProductRepository`)
- **`UserProductService.findUserProducts(request)`** — finds paid products for logged user (checks `ORDER_ITEMS` + `ORDERS.STATE` for AFTER_PAYMENT_STATES, or free products)
- **`MemberSectionController`** at `/clenska-sekce` — currently lists user products
- **`memberSection.ftl`** — renders product cards with link `/clenska-sekce/{slug}`
- **`SearchIndexer`** — indexes articles (skips drafts), categories, products. Must also skip course articles.
- **`CommonRoles`** — `ADMIN`, `MEMBER`, `REDACTOR`

### Article Changes Needed
`Article.kt` currently has: `id, externalId, slug, title, image, perex, bodyEdited, body, keywords, showOnBlog, draft, editor, validFrom/validTo`. Must add: `courseId: String?`, `moduleId: String?`.

---

## Database Schema (New Tables + Modifications)

### New: `create_tables.sql` additions (and migration file)

```sql
-- courses: private named article sets, always linked to ≥1 product
CREATE TABLE courses (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    created timestamp NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified timestamp NOT NULL,
    modified_by VARCHAR(40) NOT NULL,
    valid_from timestamp DEFAULT NULL,
    valid_to timestamp DEFAULT NULL,
    slug VARCHAR(128) NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    image VARCHAR(128) DEFAULT NULL,
    perex VARCHAR(2000) DEFAULT NULL,
    draft boolean NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX idx_courses_slug ON courses (slug);
CREATE INDEX idx_courses_draft ON courses (draft);

-- modules: ordered sub-sections within a course
CREATE TABLE modules (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    created timestamp NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified timestamp NOT NULL,
    modified_by VARCHAR(40) NOT NULL,
    course_id VARCHAR(40) NOT NULL,
    slug VARCHAR(128) NOT NULL,
    title VARCHAR(256) NOT NULL,
    description VARCHAR(1000) DEFAULT NULL,
    perex TEXT DEFAULT NULL,
    image VARCHAR(128) DEFAULT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    FOREIGN KEY (course_id) REFERENCES courses(id)
);
CREATE INDEX idx_modules_course_id ON modules (course_id);
CREATE INDEX idx_modules_course_sort ON modules (course_id, sort_order);

-- product_course: many-to-many courses ↔ products
CREATE TABLE product_course (
    product_id VARCHAR(40) NOT NULL,
    course_id VARCHAR(40) NOT NULL,
    PRIMARY KEY (product_id, course_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);
CREATE INDEX idx_product_course_product_id ON product_course (product_id);
CREATE INDEX idx_product_course_course_id ON product_course (course_id);
```

### Modified: `articles` table
```sql
ALTER TABLE articles ADD COLUMN course_id VARCHAR(40) DEFAULT NULL;
ALTER TABLE articles ADD COLUMN module_id VARCHAR(40) DEFAULT NULL;
ALTER TABLE articles ADD CONSTRAINT fk_article_course FOREIGN KEY (course_id) REFERENCES courses(id);
ALTER TABLE articles ADD CONSTRAINT fk_article_module FOREIGN KEY (module_id) REFERENCES modules(id);
CREATE INDEX idx_articles_course_id ON articles (course_id);
CREATE INDEX idx_articles_module_id ON articles (module_id);
```

---

## File Map

### New files to create
```
src/main/kotlin/org/xbery/artbeams/courses/
├── domain/
│   ├── Course.kt
│   ├── EditedCourse.kt
│   ├── Module.kt
│   └── EditedModule.kt
├── repository/
│   ├── CourseRepository.kt          (extends AssetRepository<Course, CoursesRecord>)
│   ├── ModuleRepository.kt          (extends AssetRepository<Module, ModulesRecord>)
│   └── mapper/
│       ├── CourseMapper.kt
│       ├── CourseUnmapper.kt
│       ├── ModuleMapper.kt
│       └── ModuleUnmapper.kt
├── service/
│   ├── CourseService.kt             (interface)
│   ├── CourseServiceImpl.kt
│   ├── ModuleService.kt             (interface)
│   └── ModuleServiceImpl.kt
├── admin/
│   ├── CourseAdminController.kt     (GET/POST /admin/courses)
│   ├── CourseForm.kt
│   ├── ModuleAdminController.kt     (GET/POST /admin/courses/{courseId}/modules)
│   └── ModuleForm.kt
└── controller/
    └── CourseController.kt          (GET /clenska-sekce/kurzy/*)

src/main/resources/templates/admin/courses/
├── courseList.ftl
├── courseEdit.ftl
├── moduleList.ftl
└── moduleEdit.ftl

src/main/resources/templates/member/
├── courseDetail.ftl
├── moduleDetail.ftl
└── courseSearchResults.ftl

src/main/resources/sql/migrations/
└── add_courses_modules.sql

scripts/
└── seed_courses_e2e.sql             (test data seed)
```

### Files to modify
```
src/main/resources/sql/create_tables.sql            — add 3 new tables
src/main/kotlin/.../articles/domain/Article.kt      — add courseId, moduleId fields
src/main/kotlin/.../articles/domain/EditedArticle.kt — add courseId, moduleId fields
src/main/kotlin/.../articles/repository/mapper/ArticleMapper.kt   — map new fields
src/main/kotlin/.../articles/repository/mapper/ArticleUnmapper.kt — unmap new fields
src/main/kotlin/.../articles/admin/ArticleForm.kt   — add courseId + moduleId drop-downs
src/main/kotlin/.../articles/admin/ArticleAdminController.kt — populate course/module codebooks
src/main/resources/templates/admin/articles/articleEdit.ftl — add course/module selects
src/main/kotlin/.../search/service/SearchIndexer.kt — skip articles with courseId set
src/main/kotlin/.../members/controller/MemberSectionController.kt — add courses to model
src/main/resources/templates/member/memberSection.ftl — add top course menu
src/main/resources/templates/member/memberLayout.ftl — (if menu needs layout-level inclusion)
```

---

## Domain Classes (Key Signatures)

### Course.kt
```kotlin
package org.xbery.artbeams.courses.domain

data class Course(
    override val common: AssetAttributes,
    val validity: Validity,
    val slug: String,
    val title: String,
    val description: String,
    val image: String?,
    val perex: String?,
    val draft: Boolean
) : Asset()
```

### Module.kt
```kotlin
package org.xbery.artbeams.courses.domain

data class Module(
    override val common: AssetAttributes,
    val courseId: String,
    val slug: String,
    val title: String,
    val description: String?,
    val perex: String?,
    val image: String?,
    val sortOrder: Int
) : Asset()
```

### EditedCourse.kt
```kotlin
data class EditedCourse(
    val id: String,
    val slug: String,
    val title: String,
    val description: String,
    val image: String?,
    val file: UploadedFile?,
    val perex: String?,
    val draft: Boolean,
    override val validFrom: Date,
    override val validTo: Date?,
    val products: List<String>     // product IDs assigned to this course
) : EditedTimeValidity
```

### Article.kt additions
```kotlin
// add to Article data class:
val courseId: String?,
val moduleId: String?,
```

### CourseService.kt interface
```kotlin
interface CourseService {
    fun findCourses(pagination: Pagination): ResultPage<Course>
    fun saveCourse(edited: EditedCourse, ctx: OperationCtx): Course?
    fun findEditedCourse(id: String): EditedCourse
    fun findBySlug(slug: String): Course?
    fun findCoursesForUser(userId: String): List<Course>      // via user_product → product_course
    fun findCoursesForProduct(productId: String): List<Course>
    fun deleteCourse(id: String, ctx: OperationCtx): Boolean
    fun searchArticlesInCourse(courseId: String, query: String, limit: Int): List<Article>
}
```

---

## Implementation Tasks

### Task 1: Database schema + JOOQ regeneration

**Files:**
- Modify: `src/main/resources/sql/create_tables.sql`
- Create: `src/main/resources/sql/migrations/add_courses_modules.sql`

- [ ] Add `courses`, `modules`, `product_course` table definitions at end of `create_tables.sql`
- [ ] Add article columns (`course_id`, `module_id`) to `articles` DDL in `create_tables.sql`
- [ ] Create `src/main/resources/sql/migrations/add_courses_modules.sql` with all `CREATE TABLE IF NOT EXISTS` and `ALTER TABLE ... ADD COLUMN IF NOT EXISTS` statements (idempotent)
- [ ] Run `./gradlew generateJooq` — verify new JOOQ classes generated in `org.xbery.artbeams.jooq.schema` (look for `CoursesRecord`, `ModulesRecord`, `ProductCourseRecord`)
- [ ] Commit: `feat: add courses/modules DB schema and migration`

---

### Task 2: Course + Module domain, repository, service

**Files:**
- Create: `src/main/kotlin/org/xbery/artbeams/courses/domain/Course.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/domain/EditedCourse.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/domain/Module.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/domain/EditedModule.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/repository/mapper/CourseMapper.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/repository/mapper/CourseUnmapper.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/repository/mapper/ModuleMapper.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/repository/mapper/ModuleUnmapper.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/repository/CourseRepository.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/repository/ModuleRepository.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/service/CourseService.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/service/CourseServiceImpl.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/service/ModuleService.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/service/ModuleServiceImpl.kt`

- [ ] Create `Course.kt` and `Module.kt` domain classes (see signatures above)
- [ ] Create `EditedCourse.kt` and `EditedModule.kt` form DTOs
- [ ] Create `CourseMapper` and `CourseUnmapper` following `ArticleMapper` / `ArticleUnmapper` pattern, mapping `COURSES` JOOQ record
- [ ] Create `ModuleMapper` and `ModuleUnmapper` for `MODULES` record
- [ ] Create `CourseRepository extends AssetRepository<Course, CoursesRecord>`:
  - `findCourses(pagination)`, `findBySlug(slug)`, `findCoursesForUser(userId)` (JOIN via `product_course` + `user_product`), `findCoursesForProduct(productId)`, `saveProductCourseAssignments(courseId, productIds)`, `findProductIdsForCourse(courseId)`
- [ ] Create `ModuleRepository extends AssetRepository<Module, ModulesRecord>`:
  - `findModulesByCourseId(courseId)` ordered by `sort_order`, `findBySlugAndCourse(slug, courseId)`
- [ ] Create `CourseService` interface + `CourseServiceImpl` (including `findCoursesForUser`, `searchArticlesInCourse` via direct ILIKE query on articles with `course_id = ?`)
- [ ] Create `ModuleService` interface + `ModuleServiceImpl`
- [ ] Run `./gradlew build` — fix any compile errors
- [ ] Commit: `feat: course/module domain, repository, service`

---

### Task 3: SearchIndexer — exclude course articles

**Files:**
- Modify: `src/main/kotlin/org/xbery/artbeams/search/service/SearchIndexer.kt`

- [ ] In `SearchIndexer.indexArticle()`, add guard after the draft check:
  ```kotlin
  if (article.courseId != null) {
      // Private course articles must not appear in public search
      searchIndexRepository.deleteByEntity(EntityType.ARTICLE, article.id)
      return
  }
  ```
- [ ] In `SearchIndexer.reindexAll()`, the existing `articleRepository.findLatest(10000)` already returns only non-draft articles with valid validity. Add filtering for course articles:
  ```kotlin
  articles.filter { it.courseId == null }.forEach { article -> ... }
  ```
- [ ] Run `./gradlew test` — verify `SearchIndexer` tests pass (or write a test for this logic)
- [ ] Commit: `fix: exclude course articles from public search index`

---

### Task 4: Article entity — course/module fields

**Files:**
- Modify: `src/main/kotlin/org/xbery/artbeams/articles/domain/Article.kt`
- Modify: `src/main/kotlin/org/xbery/artbeams/articles/domain/EditedArticle.kt`
- Modify: `src/main/kotlin/org/xbery/artbeams/articles/repository/mapper/ArticleMapper.kt`
- Modify: `src/main/kotlin/org/xbery/artbeams/articles/repository/mapper/ArticleUnmapper.kt`
- Modify: `src/main/kotlin/org/xbery/artbeams/articles/admin/ArticleForm.kt`
- Modify: `src/main/kotlin/org/xbery/artbeams/articles/admin/ArticleAdminController.kt`
- Modify: `src/main/resources/templates/admin/articles/articleEdit.ftl`

- [ ] Add `val courseId: String?` and `val moduleId: String?` to `Article.kt`; update `updatedWith(...)` builder accordingly
- [ ] Add `val courseId: String?` and `val moduleId: String?` to `EditedArticle.kt`
- [ ] Update `ArticleMapper.map()` — add `courseId = record.courseId`, `moduleId = record.moduleId`
- [ ] Update `ArticleUnmapper.unmap()` — set `ARTICLES.COURSE_ID`, `ARTICLES.MODULE_ID`
- [ ] Add `.field<String?>("courseId", Field.DROP_DOWN_CHOICE)` and `.field<String?>("moduleId", Field.DROP_DOWN_CHOICE)` to `ArticleForm.definition`
- [ ] In `ArticleAdminController.editForm()`, inject `CourseService` and populate model with:
  - `courses` — `courseService.findCourses(Pagination.ALL).items` (list of Course for select options)
  - `modules` — if `editedArticle.courseId != null`, `moduleService.findModulesByCourseId(courseId)` else empty list
- [ ] In `articleEdit.ftl`, add course select and conditional module select (below categories section):
  ```html
  <div class="mb-3">
    <label>Kurz (volitelně)</label>
    <@forms.inputSelect name="article.courseId" codebook=courses emptyLabel="-- bez kurzu --" />
  </div>
  <div class="mb-3" id="moduleGroup">
    <label>Modul kurzu (volitelně)</label>
    <@forms.inputSelect name="article.moduleId" codebook=modules emptyLabel="-- bez modulu --" />
  </div>
  ```
- [ ] Run `./gradlew build` — verify compilation
- [ ] Commit: `feat: article course/module assignment in editor`

---

### Task 5: Course + Module admin controllers and templates

**Files:**
- Create: `src/main/kotlin/org/xbery/artbeams/courses/admin/CourseAdminController.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/admin/CourseForm.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/admin/ModuleAdminController.kt`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/admin/ModuleForm.kt`
- Create: `src/main/resources/templates/admin/courses/courseList.ftl`
- Create: `src/main/resources/templates/admin/courses/courseEdit.ftl`
- Create: `src/main/resources/templates/admin/courses/moduleList.ftl`
- Create: `src/main/resources/templates/admin/courses/moduleEdit.ftl`

- [ ] Create `CourseForm.kt` — `FormMapping<EditedCourse>` for slug, title, description, image, file, perex, draft, validFrom, validTo, products (multi-select of product IDs)
- [ ] Create `CourseAdminController.kt` at `/admin/courses` (mirror `ArticleAdminController` pattern):
  - `list()` — paginated list
  - `editForm(id)` — GET, populates model with `editedCourse`, `products` codebook (all products for assignment)
  - `save(request)` — POST, validates form, calls `courseService.saveCourse(edited, ctx)`
  - `delete(id, request)` — POST
  - Require `hasAuthority(request, "admin")`
- [ ] Create `courseList.ftl` and `courseEdit.ftl` templates (mirror `articleList.ftl` / `articleEdit.ftl`)
- [ ] Create `ModuleForm.kt` — `FormMapping<EditedModule>` for courseId, slug, title, description, perex, image, file, sortOrder
- [ ] Create `ModuleAdminController.kt` at `/admin/courses/{courseId}/modules`
- [ ] Create `moduleList.ftl` and `moduleEdit.ftl` templates
- [ ] Add link to Courses in the admin navigation (find the admin nav template — likely `adminLayout.ftl`)
- [ ] Run `./gradlew build` and test via browser at `/admin/courses`
- [ ] Commit: `feat: course/module admin CRUD`

---

### Task 6: Member section — course menu, detail, module detail, article access

**Files:**
- Modify: `src/main/kotlin/org/xbery/artbeams/members/controller/MemberSectionController.kt`
- Modify: `src/main/resources/templates/member/memberSection.ftl`
- Create: `src/main/kotlin/org/xbery/artbeams/courses/controller/CourseController.kt`
- Create: `src/main/resources/templates/member/courseDetail.ftl`
- Create: `src/main/resources/templates/member/moduleDetail.ftl`
- Modify: existing public article controller (to gate access for course articles)

- [ ] In `MemberSectionController.memberSectionHome()`, add courses to the model:
  ```kotlin
  val loggedUser = model["_loggedUser"] as? User ?: return unauthorized(request)
  val courses = courseService.findCoursesForUser(loggedUser.id)
  model["courses"] = courses
  ```
- [ ] In `memberSection.ftl`, add top menu before the products grid (visible if `courses?has_content`):
  ```html
  <#if courses?has_content>
  <nav class="course-menu mb-4">
    <ul class="nav nav-pills">
      <#list courses as course>
      <li class="nav-item">
        <a class="nav-link" href="/clenska-sekce/kurzy/${course.slug}">${course.title}</a>
      </li>
      </#list>
    </ul>
  </nav>
  </#if>
  ```
- [ ] Create `CourseController` at `/clenska-sekce/kurzy`:
  - `GET /{courseSlug}` — verify user access (`courseService.findCoursesForUser(userId)` contains this course), show course detail with modules list or direct article list
  - `GET /{courseSlug}/moduly/{moduleSlug}` — verify access, show module detail with article list
  - `GET /{courseSlug}/search?q=...` — verify access, call `courseService.searchArticlesInCourse(courseId, q, 50)`, show results
  - All handlers call `loginService.requireLoggedUser(request)` then check course membership; return 403 redirect if unauthorized
- [ ] Create `courseDetail.ftl` — shows course title, description, search form, list of modules (image, title, description, perex) OR list of articles if no modules
- [ ] Create `moduleDetail.ftl` — shows module title, perex, list of articles (image, title, perex, link to article)
- [ ] Create `courseSearchResults.ftl` — shows search results within a course
- [ ] Guard article access: in the public article controller, find the handler for article detail. If `article.courseId != null`, verify that the logged-in user has access to that course (via `courseService.findCoursesForUser(userId).any { it.id == article.courseId }`). If not, redirect to `/clenska-sekce`.
- [ ] Run `./gradlew build`; manually test the full member flow
- [ ] Commit: `feat: member section course navigation and article access guard`

---

### Task 7: End-to-end seed data script

**Files:**
- Create: `scripts/seed_courses_e2e.sql`

- [ ] Write idempotent SQL seed script that creates:
  - Admin user: login `testadmin`, password hash for `testadmin123` (use existing PBKDF2 pattern — look at existing seed or user creation code for the hash format), role ADMIN
  - Member user: login `testmember`, hashed password `testmember123`, role MEMBER
  - One Product: `Kurz zdravého stravování`
  - One Course: `zdravé-stravování`, assigned to the product, with description and perex
  - Two Modules: `Základy výživy` (sort_order 1), `Praktické recepty` (sort_order 2)
  - Three Articles assigned to the course — two in Module 1, one in Module 2 (course articles, not public)
  - user_product entry linking testmember to the product
  - order + order_item for testmember so `UserProductRepository.findUserProducts` returns the product

- [ ] Document usage at top of script: `-- psql -U artbeams_user -d artbeams -f scripts/seed_courses_e2e.sql`
- [ ] Run the script against local DB and verify member login shows the course menu
- [ ] Commit: `chore: add e2e seed data for courses feature`

---

## Security Checklist

- [ ] Course articles (`article.courseId != null`) are never returned by `articleRepository.findLatest()` / public endpoints without an access check
- [ ] `SearchIndexer.indexArticle()` skips articles with `courseId != null`
- [ ] `SearchIndexer.reindexAll()` filters out course articles
- [ ] `CourseController` always calls `loginService.requireLoggedUser()` — returns 401/redirect if not logged in
- [ ] Access check in `CourseController`: user must own (via user_product + paid order) a product assigned to the requested course
- [ ] Public article detail handler: if `article.courseId != null`, enforce course membership check

## Run & Test Commands

```bash
# After schema changes:
./gradlew generateJooq

# Build and lint:
./gradlew clean build
./gradlew ktlintCheck

# Apply migration to local DB:
psql -U artbeams_user -d artbeams -f src/main/resources/sql/migrations/add_courses_modules.sql

# Run locally:
./gradlew bootRun --args='--spring.profiles.active=local'
# Open: http://localhost:8080/clenska-sekce (login as testmember)
# Open: http://localhost:8080/admin/courses (login as testadmin)

# Run seed data:
psql -U artbeams_user -d artbeams -f scripts/seed_courses_e2e.sql
```
