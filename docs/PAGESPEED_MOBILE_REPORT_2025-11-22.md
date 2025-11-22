# PageSpeed Insights Mobile Report Analysis
## Report Date: 2025-11-22 08:53:03 UTC
## Report URL: https://pagespeed.web.dev/analysis/https-www-vysnenezdravi-cz/a2aiy9qe9v?form_factor=mobile

### 📊 Current Production State (BEFORE Our Optimizations)

#### ⚠️ Cache Policy Issues - ADDRESSED ✅
**PageSpeed Finding**: Resources served with insufficient cache lifetimes
**Impact**: Estimated savings of **265 KiB** on repeat visits

**Current Production Cache Times:**
1. `/static/images/header.jpg` (241 KB) - **24 hours** ❌
2. `/media/*.webp?size=400` images - **48 hours** ❌  
3. `/static/css/styles.css` (35 KB) - **24 hours** ❌
4. `/static/js/*.js` files - **24 hours** ❌

**✅ OUR IMPLEMENTATION**: All increased to **30 days** (2,592,000 seconds)
- `MvcConfig.kt:22` - Static resources: 24h → 30 days
- `MediaController.kt:132` - Media files: 48h → 30 days

#### JavaScript Execution Time
**Current**: 0.7s total CPU time
- reCAPTCHA script: 617ms (largest contributor)
- Main page scripts: 409ms
- **Impact**: TBT savings of 150ms possible

#### Performance Observations

**The report confirms our optimizations are NEEDED:**
1. ✅ Cache headers are too short (our fix: 30 days)
2. ✅ Static assets could benefit from longer caching
3. ✅ Images are the largest cached assets (up to 241 KB)

### 🎯 Our Implemented Optimizations

#### 1. Extended Cache Headers ✅
- Static resources: 24h → **30 days**
- Media files: 48h → **30 days**  
- Expected impact: **Eliminates 265 KiB of repeat downloads**

#### 2. Preconnect Hints ✅
Added for:
- `https://fonts.googleapis.com`
- `https://cdnjs.cloudflare.com` (Font Awesome)
- `https://cdn.jsdelivr.net` (Bootstrap Icons)
- `https://www.google.com` (reCAPTCHA)

**Expected impact**: Faster DNS resolution and connection setup

#### 3. Deferred CSS Loading ✅
- Font Awesome: Non-blocking with `media="print" onload="this.media='all'"`
- Bootstrap Icons: Non-blocking with same technique
- Fallback with `<noscript>` for non-JS users

**Expected impact**: Reduced render-blocking resources

#### 4. Google Fonts ✅
- Already optimized with `font-display:swap` in build.gradle

### 📈 Expected Performance Improvement

| Metric | Current | Expected After Deploy |
|--------|---------|----------------------|
| Cache efficiency | Low (1-2 days) | **High (30 days)** |
| Render-blocking CSS | 2 external | **0 (deferred)** |
| DNS lookups | Delayed | **Preconnected** |
| Repeat visit speed | Baseline | **+265 KiB saved** |

### ✅ Implementation Verification

**All optimizations are CONFIRMED in code:**

```bash
# Cache headers verified:
$ grep "maxAge.*30.*DAYS" src/main/kotlin/org/xbery/artbeams/config/MvcConfig.kt
  .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())

$ grep "maxAge.*30.*DAYS" src/main/kotlin/org/xbery/artbeams/media/admin/MediaController.kt
  .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())

# Preconnect hints verified:
$ grep "preconnect" src/main/resources/templates/newWebLayout.ftl | wc -l
4  # ✅ All 4 CDNs have preconnect hints

# Deferred CSS verified:
$ grep 'media="print"' src/main/resources/templates/newWebLayout.ftl | wc -l
2  # ✅ Font Awesome and Bootstrap Icons deferred
```

**Git commit**: `fead744` - "Implement PageSpeed mobile optimizations for vysnenezdravi.cz"
**Branch**: `claude/pagespeed-mobile-analysis-01EthvKT2hRcCiSN6DfyVcSM`

### ✅ Verification Checklist

After deployment:
1. Check Network tab: Cache-Control headers show `max-age=2592000`
2. Verify icons load correctly (Font Awesome, Bootstrap)  
3. Run new PageSpeed test to measure actual improvements
4. Monitor for any visual regressions

### 🔧 Additional Recommendations (Future Work)

Based on the report, consider:
1. **Optimize reCAPTCHA loading** - Currently adds 617ms CPU time
2. **Consider image compression** - header.jpg is 241 KB
3. **Evaluate Bootstrap/Font Awesome usage** - Remove unused CSS
4. **Monitor Total Blocking Time** - Current overhead from external scripts

