<#macro page>
<#import "/css/common.ftl" as commonCss>
<#import "/css/main.ftl" as mainCss>
<!DOCTYPE html>
<html lang="cs">
 <head>
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
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

  <!-- Open Graph data -->
  <#if article??>
    <meta property="og:title" content="${article.title!}" />
    <meta property="og:type" content="article" />
    <meta property="og:site_name" content="${xlat['website.title']}" />
    <#if article.image??>
      <meta property="og:image" content="${_urlBase}/media/${article.image}?size=730" />
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

  <script src="/webjars/jquery/3.0.0/jquery.min.js?v210718"></script>
  <#-- colorbox JS must be in head and without async/defer so the popup in product page works! -->
  <script src="/static/js/jquery.colorbox-min.js?v210718"></script>

  <!-- Bootstrap -->
  <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/4.1.3/css/bootstrap.min.css" />

  <#-- lightbox.css must be in head so the popup in product page works! -->
  <link rel="stylesheet" type="text/css" href="/static/css/lightbox.css?v210718" />

  <#if xlat['google-tag.url']??>
    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="${xlat['google-tag.url']}"></script>
    <script>
      window.dataLayer = window.dataLayer || [];
      function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());
      gtag('config', '${xlat['google-tag.config']}');
    </script>
  </#if>

  <#if xlat['mailer-lite.id1']??>
    <!-- MailerLite Universal -->
    <script>
        (function(w,d,e,u,f,l,n){w[f]=w[f]||function(){(w[f].q=w[f].q||[])
        .push(arguments);},l=d.createElement(e),l.async=1,l.src=u,
        n=d.getElementsByTagName(e)[0],n.parentNode.insertBefore(l,n);})
        (window,document,'script','https://assets.mailerlite.com/js/universal.js','ml');
        ml('account', '${xlat['mailer-lite.id1']}');
    </script>
    <!-- End MailerLite Universal -->
  </#if>

  <#-- Embedding critical CSSs as recommended by Google PageSpeed Insights -->
  <style>
    <@commonCss.common></@commonCss.common>
    <@mainCss.main></@mainCss.main>
  </style>

  </head>
  <body class="d-flex flex-column h-100">
    <#-- Layout based on https://getbootstrap.com/docs/4.2/examples/starter-template/ -->
    <div id="fb-root"></div>
    <#-- Facebook SDK for comments; and for FB fanpage appId is added -->
    <#if xlat['fb.sdk.url']??>
    <script async defer crossorigin="anonymous" src="${xlat['fb.sdk.url']}&appId=${xlat['fb.app-id']}"<#if xlat['fb.nonce']??> nonce="${xlat['fb.nonce']}"</#if>></script>
    </#if>
    <header class="header">
      <!-- Fixed navbar -->
      <nav class="navbar navbar-expand-md">
        <a class="navbar-brand" href="/">${xlat['website.title']}</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"><svg xmlns="http://www.w3.org/2000/svg" height="17px" viewBox="0 0 448 512"><!--! bars-solid Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. --><style>svg{fill:#949494}</style><path d="M0 96C0 78.3 14.3 64 32 64H416c17.7 0 32 14.3 32 32s-14.3 32-32 32H32C14.3 128 0 113.7 0 96zM0 256c0-17.7 14.3-32 32-32H416c17.7 0 32 14.3 32 32s-14.3 32-32 32H32c-17.7 0-32-14.3-32-32zM448 416c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32s14.3-32 32-32H416c17.7 0 32 14.3 32 32z"/></svg></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarCollapse">
            <ul class="navbar-nav mr-auto">
              <#if xlat['menu.item1.title']??>
                <li class="nav-item">
                  <a class="nav-link" href="${xlat['menu.item1.url']}">${xlat['menu.item1.title']}</a>
                </li>
              </#if>
              <#--
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="dropdown01" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Submenu 1</a>
                <div class="dropdown-menu" aria-labelledby="dropdown01">
              -->
                  <#-- <a class="dropdown-item" href="/item-url">Item title</a> -->
              <#--
                </div>
              </li>
              -->
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
            <#if _loggedUser??>
            <ul class="navbar-nav mr-4">
              <li class="nav-item dropdown logged-user">
                <a class="nav-link" href="#" id="dropdownUser" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><svg xmlns="http://www.w3.org/2000/svg" height="19px" viewBox="0 0 448 512"><!--! user-solid Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. --><style>svg{fill:#949494}</style><path d="M224 256A128 128 0 1 0 224 0a128 128 0 1 0 0 256zm-45.7 48C79.8 304 0 383.8 0 482.3C0 498.7 13.3 512 29.7 512H418.3c16.4 0 29.7-13.3 29.7-29.7C448 383.8 368.2 304 269.7 304H178.3z"/></svg> ${_loggedUser.login}</a>
                <div class="dropdown-menu" aria-labelledby="dropdownUser">
                  <#if _loggedUser.roleNames?seq_contains("admin")><a class="dropdown-item" href="/admin">${xlat['administration']}</a></#if>
                  <a class="dropdown-item" href="/logout">${xlat['logout']}</a>
                </div>
              </li>
            </ul>
            </#if>
            <form method="get" action="/search" class="form-inline my-2 my-lg-0">
              <input type="text" name="query" class="form-control mr-sm-2" placeholder="Hledat" aria-label="${xlat['search']}">
              <button class="btn my-2 my-sm-0 btn-search" type="submit">${xlat['search']}</button>
            </form>
       </div>
     </nav>
     <#if showHeadline??>
       <div id="headline" style="background-image: url('/media/headline<#if userAccessReport?? && userAccessReport.mobileDevice>-mobile</#if>.webp')">
         <div class="row">
           <div class="col-md-3">
             <#if !userAccessReport?? || !userAccessReport.mobileDevice>
               <div id="headline-portrait">
                 <div id="headline-portrait-holder">
                   <a href="${xlat['headline.portrait.url']}"><img alt="" src="${xlat['headline.portrait.src']}" width="${xlat['headline.portrait.width']}" height="${xlat['headline.portrait.height']}" /></a>
                 </div>
               </div>
             </#if>
           </div>
           <div class="col-md-3" id="headline-offer">
             <p style="font-size: 1.1rem;line-height: 1.6rem;">${xlat['headline.offer.header']}</p>
             <p class="align-center" style="margin-bottom:0.5rem">
                <a href="${xlat['headline.offer.url']}">
                  <img alt="${xlat['headline.offer.img.alt']}" src="${xlat['headline.offer.img.src']}" width="${xlat['headline.offer.img.width']}" height="${xlat['headline.offer.img.height']}"/>
                </a>
             </p>
             <p>${xlat['headline.offer.description']}</p>
             <div id="headline-offer1" style="margin-top:1.3rem;text-align:center">
             <script>
                 jQuery(document).ready(function($) {
                     $("#headline-offer1 .open-lightbox").colorbox({inline:true,href:"#headline-offer1-form",width:"90%",maxWidth:"600px"});
                 });
             </script>

             <#if xlat['mailer-lite.popup-form']??>
               <a class="open-lightbox btn" href="#">${xlat['headline.offer.action']}</a>
               <div style="display: none;">
                 <div id="headline-offer1-form">
                   <div class="ml-embedded" data-form="${xlat['mailer-lite.popup-form']}"></div>
                 </div>
               </div>
             </#if>

             </div>
           </div>
           <div class="col-md-3"></div>
           <div class="col-md-3"></div>
       </div>
     </#if>
  </header>

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
            data-adapt-container-width="true" data-hide-cover="false" data-show-facepile="false">
              <blockquote cite="${xlat['fb.page.url']}" class="fb-xfbml-parse-ignore">
                <a href="${xlat['fb.page.url']}">${xlat['fb.page.title']}</a>
              </blockquote>
            </div>
          </#if>
        </div>

        <#if xlat['mailer-lite.form']??>
        <div class="p-4">
          <#-- MailerLite form -->
          <div class="ml-embedded" data-form="${xlat['mailer-lite.form']}"></div>
          <#-- /MailerLite form -->
        </div>
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

  <footer class="footer mt-auto py-3">
    <div class="container align-center">
        <#if xlat['personal-data.protection.url']??>
            <span class="text-muted"><a href="${xlat['personal-data.protection.url']}">${xlat['personal-data.protection.title']}</a></span>
        </#if>
        <#if xlat['cookies.url']??>
            <#if xlat['personal-data.protection.url']??> | </#if>
            <span class="text-muted"><a href="${xlat['cookies.url']}">${xlat['cookies.title']}</a></span>
        </#if>
    </div>
    <div class="container align-center">
      <span class="text-muted">&copy; ${xlat['website.title']} | ${xlat['website.disclaimer']} | <a href="#">${xlat['goto.up']}</a></span>
    </div>
  </footer>
  <div class="cookie-info-bar">
    ${xlat['cookies.info']} <a class="cookie_info_more" target="_blank" href="${xlat['cookies.url']}">${xlat['cookies.info.more']}</a>.
    <#if xlat['cookies.acceptAll']??><a class="cookie-agreement-set btn btn-primary btn-sm" href="#" role="button">${xlat['cookies.acceptAll']}</a></#if>
    <a class="cookie-info-close btn btn-secondary btn-sm" href="#" role="button">X</a>
  </div>

  <!-- Bootstrap core JavaScript -->
  <script src="/webjars/popper.js/1.14.3/popper.min.js"></script>
  <script src="/webjars/bootstrap/4.1.3/js/bootstrap.min.js"></script>
  <script>
    if (document.cookie.indexOf("cookies_confirmed=") >= 0) {
        jQuery(".cookie-info-bar").remove();
    }
    jQuery(".cookie-agreement-set").click(function () {
        var exdate = new Date();
        exdate.setDate(exdate.getDate() + 36500);
        document.cookie = 'cookies_confirmed=1; path=/; expires=' + exdate.toGMTString();
        jQuery(".cookie-info-bar").remove();
        return false;
    });
    jQuery(".cookie-agreement-unset").click(function () {
        var Cookies = document.cookie.split(';');
        // set 1 Jan, 1970 expiry for every cookies
        for (var i = 0; i < Cookies.length; i++)
        document.cookie = Cookies[i] + "=;expires=" + new Date(0).toUTCString();
        location.reload();
        return false;
    });
    jQuery(".cookie-info-close").click(function () {
        jQuery(".cookie-info-bar").remove();
        return false;
    });
    </script>
 </body>
</html>
</#macro>
