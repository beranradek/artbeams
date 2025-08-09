# ArtBeams Public Web Redesign Plan (Lovable Components)

## 1. Goals & Principles
- **Adopt Lovable's new visual style and component structure** for the public-facing web (not admin)
- **Preserve Bootstrap, plain CSS, and simple JavaScript** (no React, no build tools)
- **Maximize maintainability and clarity** for a non-React, template-driven site
- **Migrate only public web (not admin)**
- **Preserve existing JavaScript functionality** where possible

## 2. Scope
- Homepage, article list, article detail, category pages, sidebar, footer, navigation, contact
- All public FreeMarker templates in `src/main/resources/templates/` (except `admin/`)
- Public CSS in `static/css/public-styles.css`, `static/css/common-styles.css`
- Public images in `static/images/` or `/media/`
- JavaScript in `static/js/` (for search, cookies, etc.)

## 3. Component Mapping
| Lovable Component         | Target ArtBeams Template(s)        | Notes/Actions |
|--------------------------|-------------------------------------|---------------|
| Navbar                   | `webLayout.ftl`                     | Replace navbar markup, update search form |
| Hero Section             | `webLayout.ftl` (headline), homepage | Replace/merge with current headline/offer |
| BenefitsSection          | `homepage.ftl` or new fragment      | Add as homepage block |
| FeaturedContent          | `homepage.ftl`                      | Map to article cards/list |
| TestimonialsSection      | `homepage.ftl` or new fragment      | Add as homepage block |
| ContactSection           | `contact.ftl` (if exists)           | Replace contact form markup |
| Footer                   | `webLayout.ftl`                     | Replace footer markup |

## 4. Implementation Steps

### 4.1. Prepare & Backup
- Backup all templates and CSS in `src/main/resources/templates/` and `static/css/`

### 4.2. CSS Integration
- Copy Lovable's color palette and utility classes from `vysnenezdravi-lovable/src/index.css` into `public-styles.css`
- Merge/replace global styles (fonts, backgrounds, buttons, nav, footer)
- Remove/replace old color variables and redundant rules
- Add new utility classes as needed
- Test for regressions on all public pages

### 4.3. Navbar Redesign
- In `webLayout.ftl`, replace `<nav>` block with Lovable's navbar HTML (convert JSX to HTML, remove React logic)
- Use Bootstrap classes for collapse/mobile menu (no React state)
- Update menu items to use `${xlat['menu.itemX.title']}` and URLs
- Update search form markup to match new design
- Remove unused nav CSS, add new nav styles from Lovable

### 4.4. Hero Section
- In `webLayout.ftl` (headline block), replace current headline/offer with Hero section markup from Lovable
- Adapt background images to use `/media/` or existing images
- Convert any Tailwind/JSX classes to Bootstrap+custom CSS
- Remove unused headline CSS, add new hero styles

### 4.5. Homepage Blocks
- In `homepage.ftl`, add BenefitsSection, FeaturedContent, TestimonialsSection blocks in order
- For each:
  - Convert Lovable JSX to HTML
  - Replace icons with Bootstrap Icons or SVGs
  - Use Bootstrap grid for layout
  - Map article data to FeaturedContent cards
  - Add testimonials as static or from config
- Add new CSS for cards, badges, etc.

### 4.6. Article & Category Templates
- Update article and category templates to use new card and typography styles
- Update sidebar to match new design (see Lovable sidebar/card patterns)
- Update author, metadata, and social share blocks for new style

### 4.7. Contact Section
- If `contact.ftl` exists, replace form markup with Lovable's ContactSection HTML
- Use Bootstrap form controls, preserve backend logic
- Add/merge new CSS for contact form

### 4.8. Footer Redesign
- In `webLayout.ftl`, replace `<footer>` block with Lovable's footer HTML (convert JSX to HTML)
- Update links and contact info from `${xlat[...]}`
- Add new footer CSS, remove old redundant styles

### 4.9. Images & Media
- Copy any new images from `vysnenezdravi-lovable/public/lovable-uploads/` to `static/images/` or `/media/`
- Update image references in templates and CSS
- Optimize images for web (WebP preferred)

### 4.10. JavaScript
- Preserve all current JS functionality (search, cookies, modals)
- Remove any JS tied to old markup/components
- Test all interactive elements (search, modals, cookies)

### 4.11. Testing & Polish
- Test all public pages on desktop and mobile
- Check for visual bugs, layout issues, missing content
- Validate accessibility (contrast, alt text, keyboard nav)
- Optimize CSS (remove unused, minify)

### 4.12. Documentation
- Document new component structure and CSS conventions in `REDESIGN_PLAN.md`
- Add migration notes for future maintainers

## 5. File-by-File Migration Checklist
- [ ] `webLayout.ftl` (navbar, hero, footer)
- [ ] `homepage.ftl` (benefits, featured, testimonials)
- [ ] `article.ftl`, `category.ftl`, `articleCard.ftl` (cards, typography)
- [ ] `contact.ftl` (contact form)
- [ ] `public-styles.css`, `common-styles.css` (merge/replace styles)
- [ ] `static/images/` (new images)
- [ ] `static/js/` (preserve JS)

## 6. Migration Tips
- Convert JSX to plain HTML (remove curly braces, use `class` not `className`)
- Replace Tailwind classes with Bootstrap + custom CSS
- Use Bootstrap Icons for icons, or inline SVGs
- Use FreeMarker variables for dynamic content
- Keep admin templates and styles unchanged

## 7. Out of Scope
- No changes to admin UI/templates/styles
- No React, Vite, or build tools
- No TypeScript or JSX in production

---

**This plan is designed for step-by-step implementation by an AI programming agent or developer. Each step should be checked off after completion.**
