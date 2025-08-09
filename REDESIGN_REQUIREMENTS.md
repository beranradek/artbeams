 • Automated Build/Test:
    • Run ./gradlew clean build and ./gradlew test after each major step to ensure the app compiles and all tests pass.
 • Visual Regression:
    • Render the site locally (using ./gradlew bootRun --args='--spring.profiles.active=local') and fetch HTML output of key pages (homepage, article, category) to compare
      before/after markup and CSS classes.
 • HTML/CSS Validation:
    • Parse generated HTML for missing/incorrect classes, broken layout, or missing assets.
    • Check for presence of new color variables, utility classes, and correct Bootstrap structure.
 • Functional Testing:
    • Simulate user actions (navigation, search, form submit, cookie banner) by inspecting rendered HTML and JS event handlers.
    • Ensure all links, forms, and interactive elements are present and functional.
 • Accessibility Checks:
    • Scan for alt text, heading structure, color contrast, and keyboard navigation in the HTML.
 • Cross-Device Simulation:
    • Render HTML with different viewport sizes to check responsive classes and layout.
 • Error/Console Log Review:
    • Check for JavaScript errors or warnings in the browser console output.
 • Manual/Visual Review (if possible):
    • If screenshots or rendered HTML previews are available, compare them to the Lovable design reference.
