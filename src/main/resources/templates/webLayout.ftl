<#import "/mailing/subscriptionForm.ftl" as subscriptionForm>
<#macro page>
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
      <!-- Navbar, not fixed to the top (fixed-top), but static -->
      <nav class="navbar navbar-expand-md navbar-light bg-light">
        <div class="container-fluid">
          <a class="navbar-brand" href="/">${xlat['website.title']}</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarCollapse">
              <ul class="navbar-nav me-auto mb-2 mb-md-0">
                <#if xlat['menu.item1.title']??>
                  <li class="nav-item">
                    <a class="nav-link" href="${xlat['menu.item1.url']}">${xlat['menu.item1.title']}</a>
                  </li>
                </#if>
                <#if xlat['menu.item2.title']??>
                  <li class="nav-item">
                    <a class="nav-link" href="${xlat['menu.item2.url']}">${xlat['menu.item2.title']}</a>
                  </li>
                </#if>
                <#if xlat['menu.item3.title']??>
                  <li class="nav-item">
                    <a class="nav-link" href="${xlat['menu.item3.url']}">${xlat['menu.item3.title']}</a>
                  </li>
                </#if>
                <#if xlat['menu.item4.title']??>
                  <li class="nav-item">
                    <a class="nav-link" href="${xlat['menu.item4.url']}">${xlat['menu.item4.title']}</a>
                  </li>
                </#if>
                <#if xlat['menu.item5.title']??>
                  <li class="nav-item">
                    <a class="nav-link" href="${xlat['menu.item5.url']}">${xlat['menu.item5.title']}</a>
                  </li>
                </#if>
                <#if xlat['menu.item6.title']??>
                  <li class="nav-item">
                    <a class="nav-link" href="${xlat['menu.item6.url']}">${xlat['menu.item6.title']}</a>
                  </li>
                </#if>
              </ul>
              <form method="get" action="/search" class="d-flex" role="search">
                <input type="search" name="query" class="form-control me-2" placeholder="Hledat" aria-label="${xlat['search']}">
                <button class="btn btn-search btn-search-blue" type="submit">${xlat['search']}</button>
              </form>
         </div>
       </div>
     </nav>
     <#if showHeadline??>
       <div id="headline" class="<#if userAccessReport?? && userAccessReport.mobileDevice>headline-mobile<#else>headline-desktop</#if>">
       <div class="row">
           <div class="col-md-3">
             <#if !userAccessReport?? || !userAccessReport.mobileDevice>
               <div id="headline-portrait">
                 <div id="headline-portrait-holder">
                   <a href="${xlat['headline.portrait.url']}"><img alt="Portrét" src="${xlat['headline.portrait.src']}" width="${xlat['headline.portrait.width']}" height="${xlat['headline.portrait.height']}" /></a>
                 </div>
               </div>
             </#if>
           </div>
           <div class="col-md-3" id="headline-offer">
             <p class="headline-offer-header">${xlat['headline.offer.header']}</p>
             <p class="align-center" class="headline-offer-img-holder">
                <a href="${xlat['headline.offer.url']}">
                  <img alt="${xlat['headline.offer.img.alt']}" src="${xlat['headline.offer.img.src']}" width="${xlat['headline.offer.img.width']}" height="${xlat['headline.offer.img.height']}" />
                </a>
             </p>
             <p>${xlat['headline.offer.description']}</p>

             <div id="headline-offer1">
               <#if xlat['headline.offer.action']??>
                 <button type="button" data-bs-toggle="modal" data-bs-target="#headline-offer1-modal" class="btn headline-offer1-button">${xlat['headline.offer.action']}</button>
               </#if>
             </div>

           </div>
           <div class="col-md-3"></div>
           <div class="col-md-3"></div>
       </div>
       </div>
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
  <#assign showSidebar = !article?? || article.showOnBlog>
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
  
  <footer class="footer mt-auto py-3 bg-body-tertiary">
    <div class="container align-center">
      <#if xlat['personal-data.protection.url']??>
          <span class="text-body-secondary"><a href="${xlat['personal-data.protection.url']}">${xlat['personal-data.protection.title']}</a></span>
      </#if>
      <#if xlat['cookies.url']??>
          <#if xlat['personal-data.protection.url']??> | </#if>
          <span class="text-body-secondary"><a href="${xlat['cookies.url']}">${xlat['cookies.title']}</a></span>
      </#if>
    </div>
    <div class="container align-center">
      <span class="text-muted">&copy; ${xlat['website.title']} | ${xlat['website.disclaimer']} | <a href="#">${xlat['goto.up']}</a></span>
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
