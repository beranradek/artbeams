<#if subscriptionFormMapping??>
<#import "/mailing/subscriptionForm.ftl" as subscriptionForm>
</#if>
<#macro page pageStyles="" pageStyles2="">
<!DOCTYPE html>
<html lang="cs">
 <head nonce="${_cspNonce}">
 <#-- Facebook appends scripts and styles as child elements of head tag -->
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="author" content="${xlat['author.name']}" />
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
  <meta name="description" content="${description!}" />
  <#if article?? && article.keywords??>
    <meta name="keywords" content="${article.keywords!}"/>
  <#else>
    <meta name="keywords" content="${xlat['website.keywords']}"/>
  </#if>
  <#if xlat['fb.app-id']??>
    <!-- FB-features (comments, fanpage etc.) -->
    <meta property="fb:app_id" content="${xlat['fb.app-id']}" />
  </#if>
  <link rel="shortcut icon" href="${xlat['favicon.img.src']}" />
  <#-- Preload of Largest Contentful Paint (LCP) image with a high fetch priority so it starts loading with the stylesheet. -->
  <link rel="preload" fetchpriority="high" as="image" href="/media/<#if userAccessReport?? && userAccessReport.mobileDevice>headline-mobile<#else>headline</#if>.webp" type="image/webp">

  <!-- Open Graph data -->
  <#if article??>
    <meta property="og:title" content="${article.title!}" />
    <meta property="og:type" content="article" />
    <meta property="og:site_name" content="${xlat['website.title']}" />
    <#if article.image??>
      <meta property="og:image" content="${_urlBase}/media/${article.image}?size=${xlat['article.img.big.width']}" />
    </#if>
    <#-- TODO: Add additional metas:
    <meta property="article:tag" content="Some tag/keyword" />
    <meta property="article:published_time" content="2017-05-09T12:39:34+00:00" />
    article:modified_time, article:author, ...
    -->
  <#elseif category??>
    <meta property="og:title" content="${category.title!}" />
    <meta property="og:type" content="article:section" />
  <#else>
    <meta property="og:title" content="${xlat['website.title']}" />
    <meta property="og:type" content="website" />
  </#if>
  <meta property="og:url" content="${_url}" />
  <meta property="og:locale" content="${xlat['website.locale']}" />
  <meta property="og:description" content="${description!}" />

  <title><#if title??>${title} | </#if>${xlat['website.title']}</title>

  <!-- Only one CSS file composed from Bootstrap, common-styles and public-styles: -->
  <!-- Boostrap template is based on https://getbootstrap.com/docs/5.3/examples/navbar-static/ and some regular non-sticky footer -->
  <link href="/static/css/styles.css" type="text/css" rel="stylesheet">
  
  <style nonce="${_cspNonce}">
      .bd-placeholder-img {
        font-size: 1.125rem;
        text-anchor: middle;
        -webkit-user-select: none;
        -moz-user-select: none;
        user-select: none;
      }

      @media (min-width: 768px) {
        .bd-placeholder-img-lg {
          font-size: 3.5rem;
        }
      }

      .b-example-divider {
        width: 100%;
        height: 3rem;
        background-color: rgba(0, 0, 0, .1);
        border: solid rgba(0, 0, 0, .15);
        border-width: 1px 0;
        box-shadow: inset 0 .5em 1.5em rgba(0, 0, 0, .1), inset 0 .125em .5em rgba(0, 0, 0, .15);
      }

      .b-example-vr {
        flex-shrink: 0;
        width: 1.5rem;
        height: 100vh;
      }

      .bi {
        vertical-align: -.125em;
        fill: currentColor;
      }

      .nav-scroller {
        position: relative;
        z-index: 2;
        height: 2.75rem;
        overflow-y: hidden;
      }

      .nav-scroller .nav {
        display: flex;
        flex-wrap: nowrap;
        padding-bottom: 1rem;
        margin-top: -1px;
        overflow-x: auto;
        text-align: center;
        white-space: nowrap;
        -webkit-overflow-scrolling: touch;
      }

      .btn-bd-primary {
        --bd-violet-bg: #712cf9;
        --bd-violet-rgb: 112.520718, 44.062154, 249.437846;

        --bs-btn-font-weight: 600;
        --bs-btn-color: var(--bs-white);
        --bs-btn-bg: var(--bd-violet-bg);
        --bs-btn-border-color: var(--bd-violet-bg);
        --bs-btn-hover-color: var(--bs-white);
        --bs-btn-hover-bg: #6528e0;
        --bs-btn-hover-border-color: #6528e0;
        --bs-btn-focus-shadow-rgb: var(--bd-violet-rgb);
        --bs-btn-active-color: var(--bs-btn-hover-color);
        --bs-btn-active-bg: #5a23c8;
        --bs-btn-active-border-color: #5a23c8;
      }
  </style>

  <#if pageStyles?has_content>
    <link href="${pageStyles}" type="text/css" rel="stylesheet">
  </#if>

  <#if pageStyles2?has_content>
    <link href="${pageStyles2}" type="text/css" rel="stylesheet">
  </#if>

  <#if xlat['google-tag.url']??>
    <!-- Global site tag (gtag JavaScript) - Google Analytics -->
    <script nonce="${_cspNonce}" async src="${xlat['google-tag.url']}"></script>
    <script nonce="${_cspNonce}" async>
      window.dataLayer = window.dataLayer || [];
          function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());
      gtag('config', '${xlat['google-tag.config']}');
    </script>
  </#if>

  <#include "/commonScripts.ftl">

  </head>
  <body class="d-flex flex-column h-100">
    <div id="fb-root" nonce="${_cspNonce}"></div>
    <#-- Facebook SDK for comments; and for FB fanpage appId is added -->
    <#if xlat['fb.sdk.url']??>
        <script nonce="${_cspNonce}" async defer crossorigin="anonymous" data-type="lazy" data-src="${xlat['fb.sdk.url']}&appId=${xlat['fb.app-id']}"></script>
    </#if>

    <header>
      <!-- Redesigned Navbar (Lovable style, Bootstrap, FreeMarker) -->
      <nav class="navbar navbar-expand-lg nav-custom fixed-top py-3">
        <div class="container-fluid-custom">
          <div class="d-flex align-items-center justify-content-between w-100">
            <!-- Logo -->
            <a class="navbar-brand" href="/">
              <span class="fw-bold fs-4" style="color: var(--primary);">
                Vysněné<span style="color: var(--secondary);">Zdraví</span>
              </span>
            </a>
            <!-- Desktop Navigation -->
            <div class="d-none d-md-flex align-items-center">
              <div class="navbar-nav d-flex flex-row me-3">
                <#if xlat['menu.item1.title']??>
                  <a href="${xlat['menu.item1.url']}" class="nav-link nav-link-custom px-2 px-lg-3">${xlat['menu.item1.title']}</a>
                </#if>
                <#if xlat['menu.item2.title']??>
                  <a href="${xlat['menu.item2.url']}" class="nav-link nav-link-custom px-2 px-lg-3">${xlat['menu.item2.title']}</a>
                </#if>
                <#if xlat['menu.item3.title']??>
                  <a href="${xlat['menu.item3.url']}" class="nav-link nav-link-custom px-2 px-lg-3">${xlat['menu.item3.title']}</a>
                </#if>
                <#if xlat['menu.item4.title']??>
                  <a href="${xlat['menu.item4.url']}" class="nav-link nav-link-custom px-2 px-lg-3">${xlat['menu.item4.title']}</a>
                </#if>
                <#if xlat['menu.item5.title']??>
                  <a href="${xlat['menu.item5.url']}" class="nav-link nav-link-custom px-2 px-lg-3">${xlat['menu.item5.title']}</a>
                </#if>
                <#if xlat['menu.item6.title']??>
                  <a href="${xlat['menu.item6.url']}" class="nav-link nav-link-custom px-2 px-lg-3">${xlat['menu.item6.title']}</a>
                </#if>
              </div>
              <form method="get" action="/search" class="d-flex ms-lg-4" role="search">
                <div class="input-group">
                  <input class="form-control search-box" type="search" name="query" placeholder="Hledat..." aria-label="${xlat['search']}" />
                  <button class="btn btn-outline-secondary" type="submit">
                    <i class="bi bi-search"></i>
                  </button>
                </div>
              </form>
            </div>
            <!-- Mobile menu button -->
            <div class="d-md-none">
              <button class="btn" type="button" data-bs-toggle="collapse" data-bs-target="#mobileMenu" aria-controls="mobileMenu" aria-expanded="false" aria-label="Toggle navigation" style="background: none; border: none; padding: 8px;">
                <span class="navbar-toggler-icon"></span>
              </button>
            </div>
          </div>
        </div>
        <!-- Mobile menu -->
        <div class="collapse" id="mobileMenu">
          <div class="container-fluid-custom py-3">
            <div class="d-flex flex-column text-start">
              <#if xlat['menu.item1.title']??>
                <a href="${xlat['menu.item1.url']}" class="nav-link nav-link-custom py-2 px-2">${xlat['menu.item1.title']}</a>
              </#if>
              <#if xlat['menu.item2.title']??>
                <a href="${xlat['menu.item2.url']}" class="nav-link nav-link-custom py-2 px-2">${xlat['menu.item2.title']}</a>
              </#if>
              <#if xlat['menu.item3.title']??>
                <a href="${xlat['menu.item3.url']}" class="nav-link nav-link-custom py-2 px-2">${xlat['menu.item3.title']}</a>
              </#if>
              <#if xlat['menu.item4.title']??>
                <a href="${xlat['menu.item4.url']}" class="nav-link nav-link-custom py-2 px-2">${xlat['menu.item4.title']}</a>
              </#if>
              <#if xlat['menu.item5.title']??>
                <a href="${xlat['menu.item5.url']}" class="nav-link nav-link-custom py-2 px-2">${xlat['menu.item5.title']}</a>
              </#if>
              <#if xlat['menu.item6.title']??>
                <a href="${xlat['menu.item6.url']}" class="nav-link nav-link-custom py-2 px-2">${xlat['menu.item6.title']}</a>
              </#if>
              <form method="get" action="/search" class="d-flex mt-2 mb-2 px-2" role="search">
                <div class="input-group">
                  <input class="form-control search-box" type="search" name="query" placeholder="Hledat..." aria-label="${xlat['search']}" />
                  <button class="btn btn-outline-secondary" type="submit">
                    <i class="bi bi-search"></i>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </nav>
     <#if showHeadline??>
       <!-- Lovable Hero Section (Bootstrapified) -->
       <section class="hero-section d-flex align-items-center justify-content-center text-center" style="background: linear-gradient(rgba(10,22,34,0.7), rgba(10,22,34,0.7)), url('/static/images/person-beside-bare-tree-at-night-3262249.jpg'); background-size: cover; background-position: center; color: var(--text-light); padding: 8rem 0; position: relative; overflow: hidden;">
         <div class="container position-relative z-2">
           <div class="row align-items-center">
             <div class="col-lg-7 text-lg-start text-center mb-4 mb-lg-0">
               <h1 class="display-3 fw-bold mb-4 gradient-text">Vysněné zdraví začíná kvalitním spánkem</h1>
               <p class="lead mb-5">Objevte cestu k lepšímu zdraví a vitalitě prostřednictvím kvalitního spánku, zdravého životního stylu a osvědčených metod, jak se cítit každý den lépe.</p>
               <div class="d-flex flex-column flex-sm-row gap-3 justify-content-lg-start justify-content-center">
                 <a href="https://www.vysnenezdravi.cz/" target="_blank" rel="noopener noreferrer" class="btn btn-primary-custom btn-lg rounded-pill px-5">Objevit více</a>
                 <a href="#contact" class="btn btn-secondary-custom btn-lg rounded-pill px-5">Kontaktujte nás</a>
               </div>
             </div>
             <div class="col-lg-5 d-flex justify-content-center">
               <div class="featured-image-wrapper">
                 <img src="/static/images/person-beside-bare-tree-at-night-3262249.jpg" alt="Klidný spánek v moderní ložnici" class="featured-image rounded-4 shadow-lg" style="max-width: 400px;" />
               </div>
             </div>
           </div>
         </div>
       </section>
     </#if>

     <#if subscriptionFormMapping??>
         <#if xlat['mailer-lite.form.title']??>
           <div id="headline-offer1-modal" class="modal fade" tabindex="-1" aria-hidden="true">
              <div class="modal-dialog">
                  <div class="modal-content">
                      <div class="modal-header no-title">
                          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Zavřít"></button>
                      </div>
                      <div class="modal-body">
                         <@subscriptionForm.subscriptionForm productSlug=xlat['offer1.productSlug'] subscriptionFormMapping=subscriptionFormMapping formClass='offer1-header-subscription-form'></@subscriptionForm.subscriptionForm>
                      </div>
                  </div>
              </div>
           </div>
         </#if>
     </#if>
  </header>

  <#if errorMessage??>
    <div class="alert alert-danger" role="alert">${errorMessage}</div>
  </#if>

  <#if category??>
    <div class="row category-header">
      <div class="col-md-12">
        <h1>${xlat['category']}: ${category.title}</h1>
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
      <div class="col-md-8 blog-main">
        <#-- Content replaced by the content fragment of the page displayed -->
        <#nested/>
      </div>

      <aside class="col-md-4 blog-sidebar">
        <div class="p-4 mb-3 bg-light rounded">
          <h4>${xlat['website.about.header']}</h4>
          <p class="mb-0"><em>${xlat['website.title']}</em> ${xlat['website.purpose']}</p>
        </div>

        <#if latestArticles??>
        <div class="p-4">
          <h4>${xlat['articles.newest']}</h4>
          <ol class="list-unstyled mb-0">
            <#list latestArticles as article>
              <li><a href="/${article.slug}">${article.title}</a></li>
            </#list>
          </ol>
        </div>
        </#if>

        <#if articleCategories??>
        <div class="p-4">
          <h4>${xlat['articles.rubrics']}</h4>
          <ol class="list-unstyled mb-0">
            <#list articleCategories as category>
              <li><a href="${xlat['categories.url.base']}/${category.slug}">${category.title}</a></li>
            </#list>
          </ol>
        </div>
        </#if>

        <div class="p-4">
          <h4>${xlat['social.networks']}</h4>
          <#if xlat['fb.page.url']??>
            <div class="fb-page" data-href="${xlat['fb.page.url']}"
            data-tabs="timeline" data-width="292" data-height="500" data-small-header="true"
            data-adapt-container-width="true" data-hide-cover="false" data-show-facepile="false"
            data-lazy="true">
              <blockquote cite="${xlat['fb.page.url']}" class="fb-xfbml-parse-ignore">
                <a href="${xlat['fb.page.url']}">${xlat['fb.page.title']}</a>
              </blockquote>
            </div>
          </#if>
        </div>

        <#if subscriptionFormMapping??>
            <#if xlat['mailer-lite.form.title']??>
            <div class="p-4 sidebar-offer-form-holder">
                <img src="${xlat['sidebar.offer.img.src']}" loading="lazy" alt="${xlat['sidebar.offer.img.alt']}" width="${xlat['sidebar.offer.img.width']}" height="${xlat['sidebar.offer.img.height']}" border="0">
                <@subscriptionForm.subscriptionForm productSlug=xlat['offer1.productSlug'] subscriptionFormMapping=subscriptionFormMapping formClass='offer1-sidebar-subscription-form'></@subscriptionForm.subscriptionForm>
            </div>
            </#if>
        </#if>
      </aside><!-- /.blog-sidebar -->

    </div>
    <#else>
      <#-- No sidebar -->
      <div class="blog-main">
        <#-- Content replaced by the content fragment of the page displayed -->
        <#nested/>
      </div>
    </#if>
  </main>
  
  <!-- Lovable-style Footer -->
  <footer class="bg-dark text-white pt-5 pb-4 mt-auto">
    <div class="container">
      <div class="row g-4">
        <div class="col-lg-3 col-md-6 mb-4 mb-lg-0">
          <h3 class="footer-heading mb-3">Vysněné Zdraví</h3>
          <p class="mb-4">Průvodce k lepšímu spánku a zdravějšímu životnímu stylu.</p>
          <div class="d-flex">
            <a href="#" class="footer-social-icon me-2"><i class="bi bi-facebook"></i></a>
            <a href="#" class="footer-social-icon me-2"><i class="bi bi-instagram"></i></a>
            <a href="#" class="footer-social-icon"><i class="bi bi-twitter"></i></a>
          </div>
        </div>
        <div class="col-lg-3 col-md-6 mb-4 mb-lg-0">
          <h3 class="footer-heading mb-3">Užitečné odkazy</h3>
          <ul class="list-unstyled">
            <li><a href="/" class="footer-link">O nás</a></li>
            <li><a href="#" class="footer-link">Náš tým</a></li>
            <li><a href="#" class="footer-link">Často kladené otázky</a></li>
            <#if xlat['terms-and-conditions.url']??>
              <li><a href="${xlat['terms-and-conditions.url']}" class="footer-link">${xlat['terms-and-conditions.title']}</a></li>
            </#if>
            <#if xlat['personal-data.protection.url']??>
              <li><a href="${xlat['personal-data.protection.url']}" class="footer-link">${xlat['personal-data.protection.title']}</a></li>
            </#if>
            <#if xlat['cookies.url']??>
              <li><a href="${xlat['cookies.url']}" class="footer-link">${xlat['cookies.title']}</a></li>
            </#if>
          </ul>
        </div>
        <div class="col-lg-3 col-md-6 mb-4 mb-lg-0">
          <h3 class="footer-heading mb-3">Kategorie</h3>
          <ul class="list-unstyled">
            <li><a href="#" class="footer-link">Spánek</a></li>
            <li><a href="#" class="footer-link">Zdravé stravování</a></li>
            <li><a href="#" class="footer-link">Relaxace</a></li>
            <li><a href="#" class="footer-link">Pohyb</a></li>
            <li><a href="#" class="footer-link">Duševní zdraví</a></li>
          </ul>
        </div>
        <div class="col-lg-3 col-md-6">
          <h3 class="footer-heading mb-3">Kontaktujte nás</h3>
          <ul class="list-unstyled">
            <li class="mb-2"><i class="bi bi-geo-alt me-2"></i> Spánková 42, Praha 2</li>
            <li class="mb-2"><i class="bi bi-telephone me-2"></i> +420 123 456 789</li>
            <li><i class="bi bi-envelope me-2"></i> info@vysnenezdravi.cz</li>
          </ul>
        </div>
      </div>
      <div class="row mt-4 pt-3 border-top border-secondary">
        <div class="col-md-6 text-start text-secondary small">
          &copy; ${xlat['website.title']} ${.now?string['yyyy']} | ${xlat['website.disclaimer']}
        </div>
        <div class="col-md-6 text-end">
          <a href="#" class="footer-link">${xlat['goto.up']}</a>
        </div>
      </div>
    </div>
    <div id="cookie-disclaimer" class="cookie-disclaimer">
      <div class="cookie-close"><i class="fa fa-times"></i></div>
      <div class="container">
        <p>${xlat['cookies.info']} <a class="cookie_info_more" target="_blank" href="${xlat['cookies.url']}">${xlat['cookies.info.more']}</a>.
        <#if xlat['cookies.acceptAll']??>&nbsp;<button type="button" id="accept-cookie" class="btn btn-success">${xlat['cookies.acceptAll']}</button></#if>
        <button type="button" id="close-cookie" class="btn btn-secondary">X</button></p>
      </div>
    </div>
  </footer>

  <!-- Bootstrap core JavaScript -->
  <script nonce="${_cspNonce}" src="/static/js/bootstrap.min.js"></script>
  <!-- reCaptcha -->
  <script  nonce="${_cspNonce}" src="https://www.google.com/recaptcha/api.js?render=${xlat['recaptcha.siteKey']}"></script>

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

      <!-- Function registered on document ready -->
      ready(function() {
            <!-- Cookies confirmation -->
            var cookie = false;
            var cookieContent = document.getElementById('cookie-disclaimer');
            var cookieName = 'cookies_confirmed';

            checkCookie();

            if (cookie === true) {
                cookieContent.style.visibility = "hidden";
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
                  return cookie = false; //setCookie(cookieName, "accepted", 365);
              }
            }

            function acceptCookieHandler() {
                setCookie(cookieName, "accepted", 365);
                cookieContent.style.visibility = "hidden";
            }

            function unsetCookiesHandler() {
                var Cookies = document.cookie.split(';');
                // set 1 Jan, 1970 expiry for every cookies
                for (var i = 0; i < Cookies.length; i++) {
                    document.cookie = Cookies[i] + "=;expires=" + new Date(0).toUTCString();
                }
                location.reload();
                return false;
            }

            function closeCookieHandler() {
                cookieContent.style.visibility = "hidden";
            }

            registerOnClickHandler('accept-cookie', acceptCookieHandler);
            registerOnClickHandler('close-cookie', closeCookieHandler);
            registerOnClickHandler('unset-cookies', unsetCookiesHandler);
      });
  </script>
 </body>
</html>
</#macro>
