# Sprint
<!-- Machine state (active/paused/complete) lives in sprint-state.json, not here. 
This file is a human-authored brief. -->

## Theme
<!-- One-to-three sentences describing what this sprint covers and why it matters. -->

Implement private named set of articles that will be accessible only to such logged-in users in member section 
who purchased a specific product. Named set of articles can be assigned to one or more products. 

This will allow us to provide exclusive content to certain users, enhancing user engagement and 
providing value to our premium subscribers. 
The private set of articles will be designed to be easily manageable by administrators, 
allowing for seamless content updates and access control.

Articles in article sets can be further organized into modules that are created and named - preprepared in article set administration.
Module can be optionally selected in article editor after selection of article set assigned to the article.

User can search in his private article sets as described bellow.

Member section will have simple top menu before the tiles of products (visible if some article set is available).
Just an illustration of menu with article sets (Courses):

<ul id="menu-kurz" class="menu"><li id="menu-item-1887" class="menu-item menu-item-type-custom menu-item-object-custom menu-item-1887"><a href="/rozcestnik/">Rozcestník</a><span></span></li>
  <li id="menu-item-2449" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-2449"><a href="/rozcestnik/prace-s-mysli/">Prace s mysli</a><span></span></li>
  <li id="menu-item-2447" class="menu-item menu-item-type-post_type menu-item-object-page current-menu-item page_item page-item-2439 current_page_item menu-item-2447"><a href="/rozcestnik/strava/" aria-current="page">Strava</a><span></span></li>
  <li id="menu-item-2448" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-2448"><a href="/rozcestnik/bonusy/">Bonusy</a><span></span></li>
  <li id="menu-item-24964" class="menu-item menu-item-type-post_type menu-item-object-page menu-item-24964"><a href="/rozcestnik/spankove-meditace/">Spankove meditace</a><span></span></li>
</ul>

So each article set (Course) will for one item in this menu.
Each Course detail will be structured as Title, Description paragraph(s),
Search form to search in corresponding article, List of Modules (each with image, title, short description, perex - introduction text),
each Module will then be clickable - with list of Articles accessible within each module (similar list as in homepage https://www.vysnenezdravi.cz/ with article images, titles, perexes) - each clickable 
to display the article detail.
If course does not have any modules, list of articles is displayed directly in detail of course.

## What already exists (context)
<!-- Context for this sprint, including links to relevant codebase, 
product description, analysis, documentation, knowledge base, prototypes, examples and issues. -->

**`sprint-memory.md`** (repo root) — research findings and implementation plan for this sprint. MUST be read before starting any implementation. Keep it up-to-date as tasks complete or decisions change.

Member section (/clenska-sekce, memberSection.ftl, memberLayout.ftl, MemberSectionController.kt) 
already exists and provides access to purchased products.

ADMIN and MEMBER already exist in CommonRoles.kt

Article.kt, ArticleRepository.kt, ArticleService.kt, ArticleAdminController.kt data model/repository/service/administration for articles already exist, 
as well as Product.kt, ProductRepository.kt, ProductService.kt, User.kt, UserRepository.kt etc.

New entities must derive from Asset.kt. Do not name private article sets "ArticleSets", let's name them Courses - so new Course and Module entity 
will be created, one Course can have 0..n Modules, each Module 0..n Articles. Course without Modules can
have directly 0..n Articles assigned on its top level. Name other components accordingly:
CourseRepository, CourseService, CourseController, CourseAdminController, ModuleService, ModuleController, ...

New repositories must derive from AssetRepository.kt

Application can be started with start.sh script.

## Definition of done (success conditions, expectations)
<!-- Bulleted list of concrete, verifiable outcomes. Each bullet should be checkable programmatically 
or via an end-to-end test. -->

- Administration of Courses is implemented, allowing administrators to create and manage Courses (sets of articles) and assign them to products, create list of modules within this course - each module with image, title, short description and perex content (introduction text).
- Article has possibility to be assigned to a course within existing article administration/editor.
- User with member role who purchased a product that has an assigned course can access those article sets and their articles in his member section once logged in. Member section will have simple top menu before the tiles of products (visible if some course is available).
- Access to Course works end-to-end when member user logs into member section, and can see menu with available Courses, visit their details up to nested article details.
- User logged in member section can search per each Course, in all Courses he has access to, and see the articles in search results. Content from the private article set is not pre-indexed. User can search separately within each Course via its search form. Results from publicly indexed articles or from another Courses are not displayed.  

End-to-end test the complete application by running it locally, with idempotent seed script for filling all data needed
to be present in database for full testing. Use Chrome DevTools MCP tools for evaluation
of all the user journeys like the humans do through the typing, clicking, seeing (taking screenshots),
observing application logs and errors, network traffic and console errors in the browser.

## Failure conditions
<!-- Bulleted list of binary-evaluable checks that mean the sprint goal is still failing. 
Each bullet is an observable symptom that must be resolved before the sprint can be declared done. -->

- Articles from course are publicly accessible or accessible to users who have not purchased the associated product - this is NOT acceptable.
- Administrators are unable to create or manage Courses or assign them to products.
- Standard main web /search action allows to search in articles of Courses, or the SearchIndexer indexes the private article sets for public search - this should NOT be possible, private article sets must be excluded!

## Out of scope
<!-- Bulleted list of things the planner must refuse to schedule this sprint. -->

- Do not create unmentioned additional entities within the Courses (like Audio/Video media records are another additional Assets).

## Next up (ready)

- Services for Courses and Modules.
- Script to seed end-to-end testing data for agentic evaluating of this feature, creating new user with ADMIN role and new user with MEMBER role (including their test passwords), new Product assigned with Course with Modules and related Articles.   
- Administration of Courses and Modules.
- Article has possibility to be assigned to a Course within existing article administration/editor.
- Member section enhanced with Courses (and their inner Modules and Articles within them).

## Later / ideas

- Implement idempotent end-to-end browser-driven smoke script that boots the app in 'local' profile, seeds DB and performs Chrome DevTools journeys (justification: automates manual E2E verification and will be used by the MCP-based evaluation).
