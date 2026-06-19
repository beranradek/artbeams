E2E smoke tests for Courses feature

This directory contains a wrapper script and a browser-driven MCP/puppeteer script
that performs an end-to-end smoke test for the "Courses" member feature.

Files
- run_smoke_courses.sh — wrapper script that seeds the DB, starts the app, runs the browser script, performs idempotence checks and stops the app.
- smoke_courses_mcp.js — Node.js script using Puppeteer to drive Chromium and take screenshots.
- artifacts/ — output screenshots (created by the wrapper script).

Quickstart

1) Ensure local DB is available and accessible as described in sprint-memory.md.
   Example: psql -U artbeams_user -d artbeams

2) Install Node.js dependencies for the browser script (recommended):
   cd scripts/e2e && npm ci
   This will install puppeteer and minimist as declared in package.json.

3) Run the wrapper script:
   ./scripts/e2e/run_smoke_courses.sh

The script will:
- execute scripts/seed_courses_e2e.sql
- start the application (via start.sh or ./gradlew bootRun)
- wait for http://localhost:8080/ to respond
- run the browser-driven script (saves screenshots in scripts/e2e/artifacts)
- run SQL idempotence checks
- stop the application

If the script succeeds it exits 0 and at least scripts/e2e/artifacts/course-detail.png
will be present. On failure a screenshot prefixed with "error-" will be saved and
the script will exit non-zero.
