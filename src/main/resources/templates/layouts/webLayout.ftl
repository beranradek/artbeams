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
                   <a href="${xlat['headline.portrait.url']}"><img alt="Portrét" src="${xlat['headline.portrait.src']}" width="${xlat['headline.portrait.width']}" height="${xlat['headline.portrait.height']}" /></a>
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
               <a class="open-lightbox btn" href="#" style="font-weight:500;font-size:18px">${xlat['headline.offer.action']}</a>
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
          <#-- Evidently not needed/spared: <style type="text/css">@import url("https://assets.mlcdn.com/fonts.css?version=1696321");</style> -->
          <style type="text/css">
             /* LOADER */
             .ml-form-embedSubmitLoad {
             display: inline-block;
             width: 20px;
             height: 20px;
             }
             .g-recaptcha {
             transform: scale(0.85);
             -webkit-transform: scale(0.85);
             transform-origin: 0 0;
             -webkit-transform-origin: 0 0;
             height: 66px;
             }
             .sr-only {
             position: absolute;
             width: 1px;
             height: 1px;
             padding: 0;
             margin: -1px;
             overflow: hidden;
             clip: rect(0,0,0,0);
             border: 0;
             }
             .ml-form-embedSubmitLoad:after {
             content: " ";
             display: block;
             width: 11px;
             height: 11px;
             margin: 1px;
             border-radius: 50%;
             border: 4px solid #fff;
             border-color: #ffffff #ffffff #ffffff transparent;
             animation: ml-form-embedSubmitLoad 1.2s linear infinite;
             }
             @keyframes ml-form-embedSubmitLoad {
             0% {
             transform: rotate(0deg);
             }
             100% {
             transform: rotate(360deg);
             }
             }
             #mlb2-8308481.ml-form-embedContainer {
             box-sizing: border-box;
             display: table;
             margin: 0 auto;
             position: static;
             width: 100% !important;
             }
             #mlb2-8308481.ml-form-embedContainer h4,
             #mlb2-8308481.ml-form-embedContainer p,
             #mlb2-8308481.ml-form-embedContainer span,
             #mlb2-8308481.ml-form-embedContainer button {
             text-transform: none !important;
             letter-spacing: normal !important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper {
             background-color: #ffffff;
             border-width: 0px;
             border-color: transparent;
             border-radius: 4px;
             border-style: solid;
             box-sizing: border-box;
             display: inline-block !important;
             margin: 0;
             padding: 0;
             position: relative;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper.embedPopup,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper.embedDefault { width: 300px; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper.embedForm { max-width: 300px; width: 100%; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-align-left { text-align: left; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-align-center { text-align: center; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-align-default { display: table-cell !important; vertical-align: middle !important; text-align: center !important; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-align-right { text-align: right; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedHeader img {
             border-top-left-radius: 4px;
             border-top-right-radius: 4px;
             height: auto;
             margin: 0 auto !important;
             max-width: 100%;
             width: 300px;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody {
             padding: 20px 20px 0 20px;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody.ml-form-embedBodyHorizontal {
             padding-bottom: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent {
             text-align: left;
             margin: 0 0 20px 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent h4,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent h4 {
             color: #d2691e;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 20px;
             font-weight: 700;
             margin: 0 0 10px 0;
             text-align: left;
             word-break: break-word;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent p,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent p {
             color: #000000;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 14px;
             font-weight: 400;
             line-height: 20px;
             margin: 0 0 10px 0;
             text-align: left;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent ul,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent ol,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent ul,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent ol {
             color: #000000;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 14px;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent ol ol,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent ol ol {
             list-style-type: lower-alpha;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent ol ol ol,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent ol ol ol {
             list-style-type: lower-roman;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent p a,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent p a {
             color: #000000;
             text-decoration: underline;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-block-form .ml-field-group {
             text-align: left!important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-block-form .ml-field-group label {
             margin-bottom: 5px;
             color: #333333;
             font-size: 14px;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-weight: bold; font-style: normal; text-decoration: none;;
             display: inline-block;
             line-height: 20px;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent p:last-child,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent p:last-child {
             margin: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody form {
             margin: 0;
             width: 100%;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-formContent,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow {
             margin: 0 0 20px 0;
             width: 100%;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow {
             float: left;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-formContent.horozintalForm {
             margin: 0;
             padding: 0 0 20px 0;
             width: 100%;
             height: auto;
             float: left;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow {
             margin: 0 0 10px 0;
             width: 100%;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow.ml-last-item {
             margin: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow.ml-formfieldHorizintal {
             margin: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input {
             background-color: #ffffff !important;
             color: #333333 !important;
             border-color: #cccccc;
             border-radius: 4px !important;
             border-style: solid !important;
             border-width: 1px !important;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 14px !important;
             height: auto;
             line-height: 21px !important;
             margin-bottom: 0;
             margin-top: 0;
             margin-left: 0;
             margin-right: 0;
             padding: 10px 10px !important;
             width: 100% !important;
             box-sizing: border-box !important;
             max-width: 100% !important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input::-webkit-input-placeholder,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input::-webkit-input-placeholder { color: #333333; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input::-moz-placeholder,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input::-moz-placeholder { color: #333333; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input:-ms-input-placeholder,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input:-ms-input-placeholder { color: #333333; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input:-moz-placeholder,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input:-moz-placeholder { color: #333333; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow textarea, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow textarea {
             background-color: #ffffff !important;
             color: #333333 !important;
             border-color: #cccccc;
             border-radius: 4px !important;
             border-style: solid !important;
             border-width: 1px !important;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 14px !important;
             height: auto;
             line-height: 21px !important;
             margin-bottom: 0;
             margin-top: 0;
             padding: 10px 10px !important;
             width: 100% !important;
             box-sizing: border-box !important;
             max-width: 100% !important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before {
             border-color: #cccccc!important;
             background-color: #ffffff!important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input.custom-control-input[type="checkbox"]{
             box-sizing: border-box;
             padding: 0;
             position: absolute;
             z-index: -1;
             opacity: 0;
             margin-top: 5px;
             margin-left: -1.5rem;
             overflow: visible;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before {
             border-radius: 4px!important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow input[type=checkbox]:checked~.label-description::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox input[type=checkbox]:checked~.label-description::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-input:checked~.custom-control-label::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-input:checked~.custom-control-label::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox input[type=checkbox]:checked~.label-description::after {
             background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 8 8'%3e%3cpath fill='%23fff' d='M6.564.75l-3.59 3.612-1.538-1.55L0 4.26 2.974 7.25 8 2.193z'/%3e%3c/svg%3e");
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-input:checked~.custom-control-label::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-input:checked~.custom-control-label::after {
             background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='-4 -4 8 8'%3e%3ccircle r='3' fill='%23fff'/%3e%3c/svg%3e");
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-input:checked~.custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-input:checked~.custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-input:checked~.custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-input:checked~.custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox input[type=checkbox]:checked~.label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox input[type=checkbox]:checked~.label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow input[type=checkbox]:checked~.label-description::before  {
             border-color: #000000!important;
             background-color: #000000!important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-label::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-label::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label::after {
             top: 2px;
             box-sizing: border-box;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::after {
             top: 0px!important;
             box-sizing: border-box!important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::after {
             top: 0px!important;
             box-sizing: border-box!important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::after {
             top: 0px!important;
             box-sizing: border-box!important;
             position: absolute;
             left: -1.5rem;
             display: block;
             width: 1rem;
             height: 1rem;
             content: "";
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::before {
             top: 0px!important;
             box-sizing: border-box!important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .custom-control-label::before {
             position: absolute;
             top: 4px;
             left: -1.5rem;
             display: block;
             width: 16px;
             height: 16px;
             pointer-events: none;
             content: "";
             background-color: #ffffff;
             border: #adb5bd solid 1px;
             border-radius: 50%;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .custom-control-label::after {
             position: absolute;
             top: 2px!important;
             left: -1.5rem;
             display: block;
             width: 1rem;
             height: 1rem;
             content: "";
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::before, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before {
             position: absolute;
             top: 4px;
             left: -1.5rem;
             display: block;
             width: 16px;
             height: 16px;
             pointer-events: none;
             content: "";
             background-color: #ffffff;
             border: #adb5bd solid 1px;
             border-radius: 50%;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::after {
             position: absolute;
             top: 0px!important;
             left: -1.5rem;
             display: block;
             width: 1rem;
             height: 1rem;
             content: "";
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::after {
             position: absolute;
             top: 0px!important;
             left: -1.5rem;
             display: block;
             width: 1rem;
             height: 1rem;
             content: "";
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .custom-radio .custom-control-label::after {
             background: no-repeat 50%/50% 50%;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .custom-checkbox .custom-control-label::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::after, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::after {
             background: no-repeat 50%/50% 50%;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-control, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-control {
             position: relative;
             display: block;
             min-height: 1.5rem;
             padding-left: 1.5rem;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-input, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-input, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-input, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-input {
             position: absolute;
             z-index: -1;
             opacity: 0;
             box-sizing: border-box;
             padding: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-label, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-label, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label {
             color: #000000;
             font-size: 12px!important;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             line-height: 22px;
             margin-bottom: 0;
             position: relative;
             vertical-align: top;
             font-style: normal;
             font-weight: 700;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-select, #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-select {
             background-color: #ffffff !important;
             color: #333333 !important;
             border-color: #cccccc;
             border-radius: 4px !important;
             border-style: solid !important;
             border-width: 1px !important;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 14px !important;
             line-height: 20px !important;
             margin-bottom: 0;
             margin-top: 0;
             padding: 10px 28px 10px 12px !important;
             width: 100% !important;
             box-sizing: border-box !important;
             max-width: 100% !important;
             height: auto;
             display: inline-block;
             vertical-align: middle;
             -webkit-appearance: none;
             -moz-appearance: none;
             appearance: none;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow {
             height: auto;
             width: 100%;
             float: left;
             }
             .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-input-horizontal { width: 70%; float: left; }
             .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-button-horizontal { width: 30%; float: left; }
             .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-button-horizontal.labelsOn { padding-top: 25px;  }
             .ml-form-formContent.horozintalForm .ml-form-horizontalRow .horizontal-fields { box-sizing: border-box; float: left; padding-right: 10px;  }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input {
             background-color: #ffffff;
             color: #333333;
             border-color: #cccccc;
             border-radius: 4px;
             border-style: solid;
             border-width: 1px;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 14px;
             line-height: 20px;
             margin-bottom: 0;
             margin-top: 0;
             padding: 10px 10px;
             width: 100%;
             box-sizing: border-box;
             overflow-y: initial;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow button {
             background-color: #d2691e !important;
             border-color: #d2691e;
             border-style: solid;
             border-width: 1px;
             border-radius: 4px;
             box-shadow: none;
             color: #ffffff !important;
             cursor: pointer;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 14px !important;
             font-weight: 700;
             line-height: 20px;
             margin: 0 !important;
             padding: 10px !important;
             width: 100%;
             height: auto;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow button:hover {
             background-color: #ff6a00 !important;
             border-color: #ff6a00 !important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow input[type="checkbox"] {
             box-sizing: border-box;
             padding: 0;
             position: absolute;
             z-index: -1;
             opacity: 0;
             margin-top: 5px;
             margin-left: -1.5rem;
             overflow: visible;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description {
             color: #000000;
             display: block;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 12px;
             text-align: left;
             margin-bottom: 0;
             position: relative;
             vertical-align: top;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow label {
             font-weight: normal;
             margin: 0;
             padding: 0;
             position: relative;
             display: block;
             min-height: 24px;
             padding-left: 24px;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow label a {
             color: #000000;
             text-decoration: underline;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow label p {
             color: #000000 !important;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif !important;
             font-size: 12px !important;
             font-weight: normal !important;
             line-height: 18px !important;
             padding: 0 !important;
             margin: 0 5px 0 0 !important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow label p:last-child {
             margin: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedSubmit {
             margin: 0 0 20px 0;
             float: left;
             width: 100%;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedSubmit button {
             background-color: #d2691e !important;
             border: none !important;
             border-radius: 4px !important;
             box-shadow: none !important;
             color: #ffffff !important;
             cursor: pointer;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif !important;
             font-size: 14px !important;
             font-weight: 700 !important;
             line-height: 21px !important;
             height: auto;
             padding: 10px !important;
             width: 100% !important;
             box-sizing: border-box !important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedSubmit button.loading {
             display: none;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedSubmit button:hover {
             background-color: #ff6a00 !important;
             }
             .ml-subscribe-close {
             width: 30px;
             height: 30px;
             background-size: 30px;
             cursor: pointer;
             margin-top: -10px;
             margin-right: -10px;
             position: absolute;
             top: 0;
             right: 0;
             }
             .ml-error input, .ml-error textarea, .ml-error select {
             border-color: red!important;
             }
             .ml-error .custom-checkbox-radio-list {
             border: 1px solid red !important;
             border-radius: 4px;
             padding: 10px;
             }
             .ml-error .label-description,
             .ml-error .label-description p,
             .ml-error .label-description p a,
             .ml-error label:first-child {
             color: #ff0000 !important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow.ml-error .label-description p,
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow.ml-error .label-description p:first-letter {
             color: #ff0000 !important;
             }
             @media only screen and (max-width: 300px){
             .ml-form-embedWrapper.embedDefault, .ml-form-embedWrapper.embedPopup { width: 100%!important; }
             .ml-form-formContent.horozintalForm { float: left!important; }
             .ml-form-formContent.horozintalForm .ml-form-horizontalRow { height: auto!important; width: 100%!important; float: left!important; }
             .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-input-horizontal { width: 100%!important; }
             .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-input-horizontal > div { padding-right: 0px!important; padding-bottom: 10px; }
             .ml-form-formContent.horozintalForm .ml-button-horizontal { width: 100%!important; }
             .ml-form-formContent.horozintalForm .ml-button-horizontal.labelsOn { padding-top: 0px!important; }
             }
          </style>
          <style type="text/css">
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions { text-align: left; float: left; width: 100%; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent {
             margin: 0 0 15px 0;
             text-align: left;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent.horizontal {
             margin: 0 0 15px 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent h4 {
             color: #000000;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 12px;
             font-weight: 700;
             line-height: 18px;
             margin: 0 0 10px 0;
             word-break: break-word;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent p {
             color: #000000;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 12px;
             line-height: 18px;
             margin: 0 0 10px 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent.privacy-policy p {
             color: #000000;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 12px;
             line-height: 22px;
             margin: 0 0 10px 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent.privacy-policy p a {
             color: #000000;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent.privacy-policy p:last-child {
             margin: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent p a {
             color: #000000;
             text-decoration: underline;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent p:last-child { margin: 0 0 15px 0; }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptions {
             margin: 0;
             padding: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox {
             margin: 0 0 10px 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox:last-child {
             margin: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox label {
             font-weight: normal;
             margin: 0;
             padding: 0;
             position: relative;
             display: block;
             min-height: 24px;
             padding-left: 24px;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description {
             color: #000000;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 12px;
             line-height: 18px;
             text-align: left;
             margin-bottom: 0;
             position: relative;
             vertical-align: top;
             font-style: normal;
             font-weight: 700;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .description {
             color: #000000;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 12px;
             font-style: italic;
             font-weight: 400;
             line-height: 18px;
             margin: 5px 0 0 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox input[type="checkbox"] {
             box-sizing: border-box;
             padding: 0;
             position: absolute;
             z-index: -1;
             opacity: 0;
             margin-top: 5px;
             margin-left: -1.5rem;
             overflow: visible;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedMailerLite-GDPR {
             padding-bottom: 20px;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedMailerLite-GDPR p {
             color: #000000;
             font-family: 'Open Sans', Arial, Helvetica, sans-serif;
             font-size: 10px;
             line-height: 14px;
             margin: 0;
             padding: 0;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedMailerLite-GDPR p a {
             color: #000000;
             text-decoration: underline;
             }
             @media (max-width: 768px) {
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent p {
             font-size: 12px !important;
             line-height: 18px !important;
             }
             #mlb2-8308481.ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedMailerLite-GDPR p {
             font-size: 10px !important;
             line-height: 14px !important;
             }
             }
          </style>
          <div id="mlb2-8308481" class="ml-form-embedContainer ml-subscribe-form ml-subscribe-form-8308481">
             <div class="ml-form-align-center ">
                <div class="ml-form-embedWrapper embedForm">
                   <div class="ml-form-embedHeader">
                      <img src="https://bucket.mlcdn.com/a/1442/1442900/images/624b89a78edc8f47fd1326963494846b06d05c80.jpeg" alt="Západ slunce" width="292" height="219" border="0" style="display: block;">
                      <style>
                         @media only screen and (max-width: 300px){
                         .ml-form-embedHeader { display: none !important; }
                         }
                      </style>
                   </div>
                   <div class="ml-form-embedBody ml-form-embedBodyDefault row-form">
                      <div class="ml-form-embedContent" style=" ">
                         <#if xlat['mailer-lite.form']??><h4>${xlat['mailer-lite.form.title']}</h4></#if>
                         <#if xlat['mailer-lite.form.text']??>${xlat['mailer-lite.form.text']}</#if>
                      </div>
                      <form class="ml-block-form" action="https://assets.mailerlite.com/jsonp/631542/forms/101272572769863491/subscribe" data-code="" method="post" target="_blank">
                         <div class="ml-form-formContent">
                            <div class="ml-form-fieldRow ">
                               <div class="ml-field-group ml-field-name">
                                  <!-- input -->
                                  <input aria-label="name" type="text" class="form-control" data-inputmask="" name="fields[name]" placeholder="Jméno" autocomplete="given-name">
                                  <!-- /input -->
                               </div>
                            </div>
                            <div class="ml-form-fieldRow ml-last-item">
                               <div class="ml-field-group ml-field-email ml-validate-email ml-validate-required">
                                  <!-- input -->
                                  <input aria-label="email" aria-required="true" type="email" class="form-control" data-inputmask="" name="fields[email]" placeholder="Email" autocomplete="email">
                                  <!-- /input -->
                               </div>
                            </div>
                         </div>
                         <!-- Privacy policy -->
                         <!-- /Privacy policy -->
                         <div class="ml-form-embedPermissions" style="margin-bottom: 0px; padding-bottom: 5px;">
                            <div class="ml-form-embedPermissionsContent default">
                               <p><em>Vaše osobní údaje (jméno, e-mailová adresa) jsou u mě v bezpečí a budu je na základě vašeho souhlasu zpracovávat podle&nbsp;<a href="${xlat['personal-data.protection.url']}" target="_blank">zásad ochrany osobních údajů</a>, které vycházejí z české a evropské legislativy.</em></p>
                               <p><em>Stisknutím tlačítka vyjadřujete svůj souhlas s tímto zpracováním potřebným pro zaslání e-booku a dalších newsletterů ode mě, které se budou týkat souvisejícího tématu.</em></p>
                               <p><em>Svůj souhlas můžete kdykoli odvolat kliknutím na tlačítko ODHLÁSIT v každém zaslaném e-mailu.</em></p>
                               <div class="ml-form-embedPermissionsOptions">
                               </div>
                            </div>
                         </div>
                         <input type="hidden" name="ml-submit" value="1">
                         <div class="ml-form-embedSubmit">
                            <button type="submit" class="primary">ODESLAT</button>
                            <button disabled="disabled" style="display: none;" type="button" class="loading">
                               <div class="ml-form-embedSubmitLoad"></div>
                               <span class="sr-only">Loading...</span>
                            </button>
                         </div>
                         <input type="hidden" name="anticsrf" value="true">
                      </form>
                   </div>
                   <div class="ml-form-successBody row-success" style="display: none">
                      <div class="ml-form-successContent">
                         <h4>Děkujeme!</h4>
                         <p>Úspěšně jste se zaregistroval(a) k odběru novinek.</p>
                      </div>
                   </div>
                </div>
             </div>
          </div>
          <script>
             function ml_webform_success_8308481() {
             try {
                 window.top.location.href = '${xlat['mailer-lite.form.success.url']}';
               } catch (e) {
                 window.location.href = '${xlat['mailer-lite.form.success.url']}';
               }
             }
          </script>
          <script src="https://groot.mailerlite.com/js/w/webforms.min.js?v1f25ee4b05f240a833e02c19975434a4" type="text/javascript"></script>
          <script>
             fetch("https://assets.mailerlite.com/jsonp/631542/forms/101272572769863491/takel")
          </script>
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
