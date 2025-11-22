<#if subscriptionFormMapping??>
<#import "/mailing/subscriptionForm.ftl" as subscriptionForm>
</#if>
<#import "/components/modernNavbar.ftl" as navbar>
<#import "/components/heroSection.ftl" as hero>
<#import "/components/modernSidebar.ftl" as sidebar>
<#import "/components/modernFooter.ftl" as footer>

<#macro page pageStyles="" pageStyles2="">
<!DOCTYPE html>
<html lang="cs">
 <head nonce="${_cspNonce}">
 <#-- Facebook appends scripts and styles as child elements of head tag -->
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="author" content="${xlat['author.name']}" />

  <#-- Preconnect to external domains for faster resource loading -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="preconnect" href="https://cdnjs.cloudflare.com" crossorigin>
  <link rel="preconnect" href="https://cdn.jsdelivr.net" crossorigin>
  <link rel="preconnect" href="https://www.google.com" crossorigin>
  <link id="google-fonts-css" href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Playfair+Display:ital,wght@0,400;0,600;0,700;1,400&display=swap" rel="stylesheet" media="print">
  <noscript>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Playfair+Display:ital,wght@0,400;0,600;0,700;1,400&display=swap" rel="stylesheet">
  </noscript>
  <#assign description = "${xlat['website.description']}">
  <#if article??>
    <#assign title = "${(article.title)[0..*200]}">
    <#if article.perex??>
      <#assign description = "${(article.perex)[0..*350]}">
    </#if>
  <#elseif category??>
    <#assign title = "${(category.title)[0..*200]}">
    <#if category.description??>
      <#assign description = "${(category.description)[0..*350]}">
    </#if>
  </#if>
  <meta name="description" content="${description?html!}" />
  <#-- Note: keywords meta tag is outdated for SEO, but kept for legacy compatibility -->
  <#if article?? && article.keywords??>
    <meta name="keywords" content="${article.keywords!}"/>
  <#else>
    <meta name="keywords" content="${xlat['website.keywords']}"/>
  </#if>

  <#-- Canonical URL to prevent duplicate content issues -->
  <#if article??>
    <link rel="canonical" href="${_urlBase}/${article.slug}" />
  <#elseif category??>
    <link rel="canonical" href="${_urlBase}/kategorie/${category.slug}" />
  <#else>
    <link rel="canonical" href="${_urlBase}" />
  </#if>

  <#-- Author link for articles -->
  <#if article??>
    <link rel="author" href="${_urlBase}/muj-pribeh" />
  </#if>

  <link rel="shortcut icon" href="${xlat['favicon.img.src']}" />
  <#-- Preload of Largest Contentful Paint (LCP) image with a high fetch priority so it starts loading with the stylesheet. -->
  <#if showHeadline??>
  <link rel="preload" fetchpriority="high" as="image" href="/static/images/header.jpg" type="image/jpeg">
  </#if>

  <!-- Open Graph data (Facebook, LinkedIn) -->
  <#if article??>
    <meta property="og:title" content="${article.title!}" />
    <meta property="og:type" content="article" />
    <meta property="og:site_name" content="${xlat['website.title']}" />
    <#if article.image??>
      <meta property="og:image" content="${_urlBase}/media/${article.image}?size=${xlat['article.img.tablet.width']}" />
      <meta property="og:image:width" content="${xlat['article.img.tablet.width']}" />
      <meta property="og:image:height" content="${xlat['article.img.tablet.height']!xlat['article.img.tablet.width']}" />
    </#if>
    <#-- Complete Article OpenGraph metadata -->
    <meta property="article:published_time" content="${article.validFrom?datetime?iso_utc}" />
    <meta property="article:modified_time" content="${article.modified?datetime?iso_utc}" />
    <meta property="article:author" content="${_urlBase}/muj-pribeh" />
    <#if articleCategories?? && articleCategories?size gt 0>
      <meta property="article:section" content="${articleCategories[0].title}" />
    </#if>
    <#if article.keywords??>
      <#list article.keywords?split(",") as keyword>
        <#if keyword?trim != "">
    <meta property="article:tag" content="${keyword?trim}" />
        </#if>
      </#list>
    </#if>
  <#elseif category??>
    <meta property="og:title" content="${category.title!}" />
    <meta property="og:type" content="article:section" />
    <meta property="og:image" content="${_urlBase}/static/images/header.jpg" />
  <#else>
    <meta property="og:title" content="${xlat['website.title']}" />
    <meta property="og:type" content="website" />
    <meta property="og:image" content="${_urlBase}/static/images/header.jpg" />
  </#if>
  <meta property="og:url" content="${_url}" />
  <meta property="og:locale" content="${xlat['website.locale']}" />
  <meta property="og:description" content="${description?html!}" />

  <!-- Twitter Card data -->
  <meta name="twitter:card" content="summary_large_image" />
  <meta name="twitter:title" content="<#if title??>${title}<#else>${xlat['website.title']}</#if>" />
  <meta name="twitter:description" content="${description?truncate(200)?html!}" />
  <#if article?? && article.image??>
    <meta name="twitter:image" content="${_urlBase}/media/${article.image}?size=${xlat['article.img.tablet.width']}" />
    <meta name="twitter:image:alt" content="${article.title!}" />
  <#else>
    <#-- Use default social image for non-article pages -->
    <meta name="twitter:image" content="${_urlBase}/static/images/header.jpg" />
  </#if>
  <#-- Add your Twitter handle if you have one
  <meta name="twitter:site" content="@YourTwitterHandle" />
  <meta name="twitter:creator" content="@YourTwitterHandle" />
  -->

  <title><#if title??>${title} | </#if>${xlat['website.title']}</title>

  <#-- JSON-LD Structured Data for GEO/SEO -->
  <#if articleJsonLd??>
  <script type="application/ld+json" nonce="${_cspNonce}">
${articleJsonLd}
  </script>
  </#if>
  <#if breadcrumbJsonLd??>
  <script type="application/ld+json" nonce="${_cspNonce}">
${breadcrumbJsonLd}
  </script>
  </#if>
  <#if websiteJsonLd??>
  <script type="application/ld+json" nonce="${_cspNonce}">
${websiteJsonLd}
  </script>
  </#if>

  <#-- CSS files merged and minified into styles.css
  <link href="/static/css/bootstrap.min.css" type="text/css" rel="stylesheet">
  <link href="/static/css/new-design-styles.css" type="text/css" rel="stylesheet">
  <link href="/static/css/new-hero-styles.css" type="text/css" rel="stylesheet">
  <link href="/static/css/new-components.css" type="text/css" rel="stylesheet">
  <link href="/static/css/responsive-images.css" type="text/css" rel="stylesheet">
  -->
  <#-- Critical CSS loaded synchronously -->
  <link href="/static/css/styles.css" type="text/css" rel="stylesheet">

  <#-- Custom icon system CSS (deferred for better performance) -->
  <link id="custom-icons-css" href="/static/css/custom-icons.css" type="text/css" rel="stylesheet" media="print" />

  <#-- Fallback for browsers without JavaScript -->
  <noscript>
    <link href="/static/css/custom-icons.css" type="text/css" rel="stylesheet" />
  </noscript>

  <#if pageStyles?has_content>
    <link href="${pageStyles}" type="text/css" rel="stylesheet">
  </#if>

  <#if pageStyles2?has_content>
    <link href="${pageStyles2}" type="text/css" rel="stylesheet">
  </#if>

  <#include "/commonScripts.ftl">

  </head>
  <body class="d-flex flex-column h-100" id="body-element">
    <#-- Custom icon sprite (inline for immediate availability, hidden via CSS class) -->
    <#include "/icons/iconSprite.ftl">
    
    <@navbar.modernNavbar />

    <#-- Show hero section only on homepage -->
    <#if showHeadline??>
      <@hero.heroSection />
    </#if>

    <#-- Modal for subscription form from header -->
    <#if subscriptionFormMapping??>
        <#if xlat['mailer-lite.form.title']??>
          <div id="headline-offer1-modal" class="modal fade" tabindex="-1" aria-hidden="true">
             <div class="modal-dialog">
                 <div class="modal-content">
                     <div class="modal-header no-title">
                         <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Zavřít"></button>
                     </div>
                     <div class="modal-body">
                        <@subscriptionForm.subscriptionForm productSlug=xlat['offer1.productSlug'] subscriptionFormMapping=subscriptionFormMapping formClass='offer1-header-subscription-form' textColor='black'></@subscriptionForm.subscriptionForm>
                     </div>
                 </div>
             </div>
          </div>
        </#if>
    </#if>

  <#if errorMessage??>
    <div class="alert alert-danger" role="alert">${errorMessage}</div>
  </#if>

  <#if category??>
    <div class="row category-header">
      <div class="col-md-12">
        <h1>${category.title}</h1>
      </div>
    </div>
  </#if>

  <!-- Page content -->
  <#if !showSidebar??>
  <#assign showSidebar = !article?? || article.showOnBlog>
  </#if>
  <main role="main" class="page-content flex-shrink-0 container">

    <#if showSidebar>
    <div class="row">
      <div class="col-lg-8 blog-main">
        <#-- Content replaced by the content fragment of the page displayed -->
        <#nested/>
      </div>

      <@sidebar.modernSidebar />

    </div>
    <#else>
      <#-- No sidebar -->
      <div class="blog-main">
        <#-- Content replaced by the content fragment of the page displayed -->
        <#nested/>
      </div>
    </#if>
  </main>
  
  <@footer.modernFooter />

  <!-- Cookie disclaimer -->
  <div id="cookie-disclaimer" class="cookie-disclaimer">
    <div class="container">
      <p>${xlat['cookies.info']} <a class="cookie_info_more" target="_blank" href="${xlat['cookies.url']}">${xlat['cookies.info.more']}</a>.

      <#if xlat['cookies.acceptAll']??>&nbsp;<button type="button" id="accept-cookie" class="btn btn-success">${xlat['cookies.acceptAll']}</button></#if>
      <button type="button" id="close-cookie" class="btn btn-secondary">X</button></p>
    </div>
  </div>

  <!-- Bootstrap core JavaScript -->
  <script nonce="${_cspNonce}" src="/static/js/bootstrap.min.js"></script>
  <!-- Responsive Images JavaScript -->
  <script nonce="${_cspNonce}" src="/static/js/responsive-images.js"></script>
  <!-- reCaptcha -->
  <script async defer nonce="${_cspNonce}" data-type="lazy" data-src="https://www.google.com/recaptcha/api.js?render=${xlat['recaptcha.siteKey']}"></script>

  <script nonce="${_cspNonce}">
      <#-- Lazy loading of data-type='lazy' scripts and iframes -->
      const loadScriptsTimer = setTimeout(loadScripts, 5000);
      const userInteractionEvents = ["mouseover","keydown","touchmove","touchstart","wheel"];
      userInteractionEvents.forEach(function (event) {
          window.addEventListener(event, triggerScriptLoader, {
              passive: true
          });
      });
      function triggerScriptLoader() {
          loadScripts();
          clearTimeout(loadScriptsTimer);
          userInteractionEvents.forEach(function (event) {
              window.removeEventListener(event, triggerScriptLoader, {
                  passive: true
              });
          });
      }
      function loadScripts() {
          document.querySelectorAll("script[data-type='lazy']").forEach(function (elem) {
              elem.setAttribute("src", elem.getAttribute("data-src"));
          });
          document.querySelectorAll("iframe[data-type='lazy']").forEach(function (elem) {
              elem.setAttribute("src", elem.getAttribute("data-src"));
          });
      }
      <#-- /Lazy loading of data-type='lazy' scripts and iframes -->

      <#-- Create stars/light particles for the hero section -->
      <#if showHeadline??>
      const heroSection = document.querySelector(".hero-section");
      if (heroSection) {
        for (let i = 0; i < 50; i++) {
          const star = document.createElement("div");
          star.classList.add("star");
          star.style.left = Math.random() * 100 + "%";
          star.style.top = Math.random() * 100 + "%";
          star.style.animationDelay = Math.random() * 3 + "s";
          heroSection.appendChild(star);
        }
      }
      </#if>

      <!-- Function registered on document ready -->
      ready(function() {
            <#-- Enable deferred CSS -->
            var deferredCssIds = ['custom-icons-css', 'google-fonts-css'];
            deferredCssIds.forEach(function(id) {
                var link = document.getElementById(id);
                if (link) link.media = 'all';
            });

            <!-- Initialize Bootstrap tooltips -->
            const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
            tooltipTriggerList.forEach(tooltipTriggerEl => {
              new bootstrap.Tooltip(tooltipTriggerEl);
            });

            <!-- Smooth scroll for anchor links -->
            document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
              anchor.addEventListener("click", function (e) {
                e.preventDefault();
                const targetId = this.getAttribute("href");
                if (targetId !== "#") {
                  const targetElement = document.querySelector(targetId);
                  if (targetElement) {
                    window.scrollTo({
                      top: targetElement.offsetTop - 80, // Offset for fixed header
                      behavior: "smooth",
                    });
                  }
                }
              });
            });

            <!-- Cookies confirmation -->
            var cookie = false;
            var cookieContent = document.getElementById('cookie-disclaimer');
            var cookieName = 'cookies_confirmed';

            checkCookie();

            if (cookie === true && cookieContent) {
                cookieContent.style.display = "none";
            }

            function setCookie(cname, cvalue, exdays) {
              var d = new Date();
              d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
              var expires = "expires=" + d.toGMTString();
              document.cookie = cname + "=" + cvalue + "; " + expires;
            }

            function getCookie(cname) {
              var name = cname + "=";
              var ca = document.cookie.split(';');
              for (var i = 0; i < ca.length; i++) {
                var c = ca[i].trim();
                if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
              }
              return "";
            }

            function checkCookie() {
              var check = getCookie(cookieName);
              if (check !== "") {
                return cookie = true;
              } else {
                  return cookie = false;
              }
            }

            function acceptCookieHandler() {
                setCookie(cookieName, "accepted", 365);
                if (cookieContent) cookieContent.style.display = "none";
            }

            function unsetCookiesHandler() {
                var Cookies = document.cookie.split(';');
                for (var i = 0; i < Cookies.length; i++) {
                    document.cookie = Cookies[i] + "=;expires=" + new Date(0).toUTCString();
                }
                location.reload();
                return false;
            }

            function closeCookieHandler() {
                if (cookieContent) cookieContent.style.display = "none";
            }

            registerOnClickHandler('accept-cookie', acceptCookieHandler);
            registerOnClickHandler('close-cookie', closeCookieHandler);
            registerOnClickHandler('unset-cookies', unsetCookiesHandler);
            
            <!-- Initialize news subscription form AJAX handling -->
            ajaxHandleFormWithClass("news-subscription-form", true);
      });
  </script>
 </body>
</html>
</#macro>
