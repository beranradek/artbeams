# Custom Icon System

This project uses a custom SVG icon system instead of Font Awesome and Bootstrap Icons to minimize CSS payload and eliminate unused styles.

## Overview

- **Icon Sprite**: `/static/icons/custom-icons.svg` - Contains all 34 icons as SVG symbols (it is just editable XML file)
- **Icon Sprite Template**: `/templates/icons/iconSprite.ftl` - Copy of the SVG sprite included inline in HTML. iconSprite.ftl is a FreeMarker template that contains the SVG sprite and is included inline in the HTML body. This approach is better than loading it as an external file because:
No extra HTTP request - the sprite is embedded directly in the HTML
Immediate availability - icons can be used right away without waiting for a file to load
Better for small sprites - at ~2 KiB, inlining is more efficient than a separate request
It's just a copy of custom-icons.svg that gets included via <#include "/icons/iconSprite.ftl"> in the layout.
- **CSS**: `/static/css/custom-icons.css` - Minimal styling for icons (deferred loading)
- **Size**: ~2.5 KiB total (vs 27.4 KiB for Font Awesome + Bootstrap Icons)
- **Performance**: 90% reduction in icon-related CSS

## Usage

### Basic Icon

```html
<svg class="icon">
  <use href="/static/icons/custom-icons.svg#icon-facebook"></use>
</svg>
```

### Icon with Size

```html
<!-- Small -->
<svg class="icon icon-sm">
  <use href="/static/icons/custom-icons.svg#icon-envelope"></use>
</svg>

<!-- Medium (default is 1em) -->
<svg class="icon icon-md">
  <use href="/static/icons/custom-icons.svg#icon-heart"></use>
</svg>

<!-- Large -->
<svg class="icon icon-lg">
  <use href="/static/icons/custom-icons.svg#icon-robot"></use>
</svg>

<!-- Extra Large -->
<svg class="icon icon-xl">
  <use href="/static/icons/custom-icons.svg#icon-sun"></use>
</svg>
```

### Icon with Color

Icons inherit the current text color by default. Use any text color utility:

```html
<svg class="icon text-primary-custom">
  <use href="/static/icons/custom-icons.svg#icon-check-circle"></use>
</svg>

<svg class="icon text-secondary-custom">
  <use href="/static/icons/custom-icons.svg#icon-geo-alt"></use>
</svg>
```

### Spinning Icon (for loading states)

```html
<svg class="icon icon-spin">
  <use href="/static/icons/custom-icons.svg#icon-circle-notch"></use>
</svg>
```

## Available Icons

### Social Media (5 icons)
- `icon-facebook`
- `icon-instagram`
- `icon-twitter`
- `icon-youtube`
- `icon-whatsapp`

### Contact (3 icons)
- `icon-geo-alt` (location pin)
- `icon-envelope` (email)
- `icon-telephone` (phone)

### UI Elements (13 icons)
- `icon-check-circle`
- `icon-check`
- `icon-chevron-down`
- `icon-clock`
- `icon-sun`
- `icon-moon`
- `icon-heart`
- `icon-arrow-up`
- `icon-info-circle`
- `icon-exclamation-triangle`
- `icon-circle-notch` (spinner)
- `icon-clipboard`
- `icon-code-compare`

### Admin/Tools (5 icons)
- `icon-robot`
- `icon-paperclip`
- `icon-palette`
- `icon-paper-plane`
- `icon-broom`

## Migration from Font Awesome / Bootstrap Icons

### Bootstrap Icons

**Before:**
```html
<i class="bi bi-facebook"></i>
```

**After:**
```html
<svg class="icon">
  <use href="/static/icons/custom-icons.svg#icon-facebook"></use>
</svg>
```

### Font Awesome

**Before:**
```html
<i class="fas fa-robot"></i>
```

**After:**
```html
<svg class="icon">
  <use href="/static/icons/custom-icons.svg#icon-robot"></use>
</svg>
```

### Spinning Icons

**Before:**
```html
<i class="fas fa-circle-notch fa-spin"></i>
```

**After:**
```html
<svg class="icon icon-spin">
  <use href="/static/icons/custom-icons.svg#icon-circle-notch"></use>
</svg>
```

## Adding New Icons

### Quick Method (Recommended)

1. **Find the icon**:
   - Bootstrap Icons: https://icons.getbootstrap.com/
   - Font Awesome: https://fontawesome.com/icons (free icons only)

2. **Get the SVG path**:
   - Click on the icon
   - Copy the `<path>` element(s) from the SVG code

3. **Edit the sprite file**:
   ```bash
   # Open in your editor (VS Code, IntelliJ, or any text editor)
   nano src/main/resources/static/icons/custom-icons.svg
   # or
   code src/main/resources/static/icons/custom-icons.svg
   ```

4. **Add the icon** before the closing `</svg>` tag:
   ```xml
   <symbol id="icon-your-icon-name" viewBox="0 0 16 16">
     <path d="...paste the path data here..."/>
   </symbol>
   ```

5. **Update the template sprite**:
   ```bash
   cp src/main/resources/static/icons/custom-icons.svg \
      src/main/resources/templates/icons/iconSprite.ftl
   ```

6. **Use the new icon**:
   ```html
   <svg class="icon">
     <use href="#icon-your-icon-name"></use>
   </svg>
   ```

### Important Notes

For Bootstrap Icons:
- Visit https://icons.getbootstrap.com/
- Search for your icon
- Click on it and copy the SVG `<path>` element(s)

For Font Awesome:
- Visit https://fontawesome.com/icons
- Search for your icon (use free icons only)
- View the SVG code and copy the `<path>` element

### 2. Add to Sprite

Edit `/static/icons/custom-icons.svg` and add a new `<symbol>`:

```xml
<symbol id="icon-your-icon-name" viewBox="0 0 16 16">
  <path d="...your SVG path data..."/>
</symbol>
```

**Important Notes:**
- Bootstrap Icons use `viewBox="0 0 16 16"`
- Font Awesome icons vary (common: `viewBox="0 0 512 512"` or `viewBox="0 0 384 512"`)
- Use the correct viewBox from the source icon
- Icon ID should start with `icon-` prefix
- Use kebab-case for icon names

### 3. Use the New Icon

```html
<svg class="icon">
  <use href="/static/icons/custom-icons.svg#icon-your-icon-name"></use>
</svg>
```

### 4. Document It

Add the new icon to the "Available Icons" section above.

## FreeMarker Macro (Optional)

For easier usage in templates, you can create a macro:

```ftl
<#macro icon name size="" class="">
  <svg class="icon ${size} ${class}">
    <use href="/static/icons/custom-icons.svg#icon-${name}"></use>
  </svg>
</#macro>
```

Usage:
```ftl
<@icon name="facebook" size="icon-lg" class="text-primary-custom" />
```

## Performance Benefits

| Metric | Before | After | Savings |
|--------|--------|-------|---------|
| Font Awesome CSS | 14.8 KiB | 0 KiB | 14.8 KiB |
| Bootstrap Icons CSS | 12.6 KiB | 0 KiB | 12.6 KiB |
| Custom Icons | 0 KiB | ~2.5 KiB | - |
| **Total** | **27.4 KiB** | **~2.5 KiB** | **~90%** |

Additional benefits:
- No unused CSS (100% of icons are used)
- No external CDN dependencies
- Better caching control
- Easier to customize colors and sizes
- No font loading delays

## Troubleshooting

### Icon not displaying

1. Check the icon ID is correct (must start with `icon-`)
2. Verify the SVG sprite path is correct
3. Ensure the icon exists in `custom-icons.svg`
4. Check browser console for errors

### Icon too small/large

Use size classes:
- `.icon-sm` for small icons
- `.icon-md` for medium icons
- `.icon-lg` for large icons
- `.icon-xl` for extra large icons

Or set custom size with inline styles:
```html
<svg class="icon" style="width: 24px; height: 24px;">
  <use href="/static/icons/custom-icons.svg#icon-heart"></use>
</svg>
```

### Icon wrong color

Icons inherit `currentColor`. Set the text color on the parent or the SVG itself:

```html
<svg class="icon text-danger">
  <use href="/static/icons/custom-icons.svg#icon-exclamation-triangle"></use>
</svg>
```
