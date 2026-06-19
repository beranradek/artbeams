#!/usr/bin/env node
/*
 Chrome DevTools / Puppeteer E2E smoke script for Courses

 This script drives a headless (or headed) Chromium instance using Puppeteer
 to perform a simple end-to-end journey:
  - Login as testmember
  - Open member section
  - Verify course menu contains link to /clenska-sekce/zdrave-stravovani
  - Click the course link and verify course detail (module-list or article-list)
  - Save a screenshot artifacts/course-detail.png

 Notes / Docs:
  - Puppeteer (Chromium + DevTools Protocol): https://pptr.dev/
  - Chrome DevTools Protocol overview: https://chromedevtools.github.io/devtools-protocol/

 Usage:
  - npm install puppeteer
  - node scripts/e2e/smoke_courses_mcp.js --baseUrl=http://localhost:8080 --artifacts=scripts/e2e/artifacts
 */

const fs = require('fs');
const path = require('path');
const minimist = require('minimist');
const puppeteer = require('puppeteer');

async function run() {
  const argv = minimist(process.argv.slice(2));
  const baseUrl = argv.baseUrl || process.env.BASE_URL || 'http://localhost:8080';
  const artifacts = argv.artifacts || 'scripts/e2e/artifacts';

  if (!fs.existsSync(artifacts)) fs.mkdirSync(artifacts, { recursive: true });

  let browser = null;
  let page = null;
  try {
    browser = await puppeteer.launch({ headless: true, args: ['--no-sandbox', '--disable-setuid-sandbox'] });
    page = await browser.newPage();
    page.setDefaultTimeout(30000);

    // 1) Go to login
    await page.goto(baseUrl + '/login', { waitUntil: 'networkidle2' });

    // Fill form
    await page.type('input#username', 'testmember');
    await page.type('input#password', 'testmember123');

    // Submit: try pressing Enter on password field
    await page.keyboard.press('Enter');

    // Wait for navigation to finish
    await page.waitForNavigation({ waitUntil: 'networkidle2', timeout: 30000 });

    // Navigate to member section
    await page.goto(baseUrl + '/clenska-sekce', { waitUntil: 'networkidle2' });

    // Assert course menu exists
    const menu = await page.$('nav.course-menu');
    if (!menu) throw new Error('course-menu not found on /clenska-sekce');

    // Check for link to seeded course
    const linkHandle = await page.$x("//nav[contains(@class,'course-menu')]//a[@href='/clenska-sekce/zdrave-stravovani']");
    if (!linkHandle || linkHandle.length === 0) {
      // provide debug screenshot
      const debugPath = path.join(artifacts, 'course-menu-missing.png');
      await page.screenshot({ path: debugPath, fullPage: true });
      throw new Error(`Course link /clenska-sekce/zdrave-stravovani not found. Screenshot: ${debugPath}`);
    }

    // Click the first matching link and wait for navigation (avoid race)
    await Promise.all([
      page.waitForNavigation({ waitUntil: 'networkidle2', timeout: 30000 }),
      linkHandle[0].click(),
    ]);

    // After navigation, assert presence of either .module-list or .article-list
    const hasModuleList = (await page.$('.module-list')) !== null;
    const hasArticleList = (await page.$('.article-list')) !== null;
    if (!hasModuleList && !hasArticleList) {
      const debugPath = path.join(artifacts, 'course-detail-missing.png');
      await page.screenshot({ path: debugPath, fullPage: true });
      throw new Error(`Neither .module-list nor .article-list found on course detail. Screenshot: ${debugPath}`);
    }

    // Save success screenshot
    const outPath = path.join(artifacts, 'course-detail.png');
    await page.screenshot({ path: outPath, fullPage: true });
    console.log('OK: Saved screenshot to', outPath);

    await browser.close();
    process.exit(0);
  } catch (err) {
    try {
      const outPath = path.join(artifacts, `error-${Date.now()}.png`);
      if (page) await page.screenshot({ path: outPath, fullPage: true });
      console.error('Saved error screenshot to', outPath);
    } catch (e) {
      console.error('Failed to save error screenshot', e);
    }
    if (browser) await browser.close();
    console.error('ERROR:', err && err.stack ? err.stack : err);
    process.exit(2);
  }
}

// Entrypoint
if (require.main === module) run();
