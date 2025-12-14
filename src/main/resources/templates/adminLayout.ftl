<#macro page noUp=false>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="" />
    <meta name="keywords" content=""/>
    <title><#if pageTitle??>${pageTitle}<#else><#if title??>${title}<#else>CMS Administration | </#if></#if>${xlat['website.title']}</title>
    <link rel="shortcut icon" href="${xlat['favicon.img.src']}" />
    
    <!-- Bootstrap -->
    <!-- Based on https://getbootstrap.com/docs/5.3/examples/navbar-static/ and some regular non-sticky footer -->
    <link href="/static/css/bootstrap.min.css" type="text/css" rel="stylesheet">

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

      /* Accessibility: Enhanced focus indicators for keyboard navigation */
      a:focus-visible,
      button:focus-visible,
      input:focus-visible,
      textarea:focus-visible,
      select:focus-visible,
      [tabindex]:focus-visible {
        outline: 3px solid #0d6efd;
        outline-offset: 2px;
        box-shadow: 0 0 0 4px rgba(13, 110, 253, 0.25);
      }

      .nav-link:focus-visible {
        outline: 2px solid #ffffff;
        outline-offset: 2px;
        background-color: rgba(255, 255, 255, 0.1);
      }

      .btn:focus-visible {
        outline: 3px solid #0d6efd;
        outline-offset: 2px;
        box-shadow: 0 0 0 4px rgba(13, 110, 253, 0.3);
      }

      /* Skip to main content link for keyboard users */
      .skip-to-main {
        position: absolute;
        left: -9999px;
        z-index: 999;
        padding: 10px 20px;
        background: #0d6efd;
        color: white;
        text-decoration: none;
        font-weight: bold;
      }

      .skip-to-main:focus {
        left: 10px;
        top: 10px;
      }
    </style>
    
    <#-- NOTE: Improvement: Embedding critical CSSs as recommended by Google PageSpeed Insights -->
    <!-- Custom styles for this template -->
    <link rel="stylesheet" type="text/css" href="/static/css/common-styles.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/admin-styles.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/sales-styles.css" />
    <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />

    <#include "/commonScripts.ftl">
  </head>
  <body class="d-flex flex-column h-100">
     <!-- Skip to main content link for keyboard navigation -->
     <a href="#main-content" class="skip-to-main">Skip to main content</a>
     
     <!-- Navbar, not fixed to the top (fixed-top), but static with additional mb-4 (bottom padding) -->
    <nav class="navbar navbar-expand-md navbar-dark bg-dark mb-4" role="navigation" aria-label="Main navigation">
      <div class="container-fluid">
        <a class="navbar-brand" href="/admin" aria-label="CMS Administration Home">CMS</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarCollapse">
          <ul class="navbar-nav me-auto mb-2 mb-md-0" role="menubar">
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/users" role="menuitem" aria-label="Manage users">Users</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/activity-logs" role="menuitem" aria-label="View activity logs">Activity Logs</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/categories" role="menuitem" aria-label="Manage categories">Categories</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/articles" role="menuitem" aria-label="Manage articles">Articles</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/comments" role="menuitem" aria-label="Manage comments">Comments</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/media" role="menuitem" aria-label="Manage media files">Media</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/products" role="menuitem" aria-label="Manage products">Products</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/orders" role="menuitem" aria-label="Manage orders">Orders</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/consents" role="menuitem" aria-label="Manage user consents">Consents</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/localisations" role="menuitem" aria-label="Manage localisations">Localisations</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/config" role="menuitem" aria-label="Manage configuration">Config</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/search-console" role="menuitem" aria-label="View search console metrics">Search Console</a>
            </li>
          </ul>
  
          <#if _isRemoteDbConfigured?? && _isRemoteDbConfigured>
          <a href="/admin/sync/confirm" class="btn btn-primary my-2 my-sm-0 mx-2" aria-label="Synchronize remote database">
            <i class="fas fa-sync-alt" aria-hidden="true"></i> <span>Sync remote DB</span>
          </a>
          </#if>

          <form class="form-inline my-2 my-lg-0 form-config-reload" action="/admin/config/reload" method="POST" aria-label="Reload configuration">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button class="btn btn-secondary my-2 my-sm-0" type="submit" aria-label="Reload configuration from database">Reload config</button>
          </form>

          <form class="form-inline my-2 my-lg-0 form-localisations-reload" action="/admin/localisations/reload" method="POST" aria-label="Reload localisations">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button class="btn btn-secondary my-2 my-sm-0" type="submit" aria-label="Reload localisations from database">Reload localisations</button>
          </form>
  
          <ul class="navbar-nav mr-4" role="menubar">
            <li class="nav-item" role="none">
              <a class="nav-link" href="/" role="menuitem" aria-label="View public website">Public web</a>
            </li>
            <#if _loggedUser??>
            <li class="nav-item logged-user" role="none">
              <a class="nav-link" href="#" role="menuitem" aria-label="Current user: ${_loggedUser.login}">
                <svg xmlns="http://www.w3.org/2000/svg" height="19px" viewBox="0 0 448 512" aria-hidden="true" focusable="false"><!--! user-solid Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. --><style nonce="${_cspNonce}">svg{fill:#ffffff}</style><path d="M224 256A128 128 0 1 0 224 0a128 128 0 1 0 0 256zm-45.7 48C79.8 304 0 383.8 0 482.3C0 498.7 13.3 512 29.7 512H418.3c16.4 0 29.7-13.3 29.7-29.7C448 383.8 368.2 304 269.7 304H178.3z"/></svg>
                <span>${_loggedUser.login}</span>
              </a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/logout" role="menuitem" aria-label="Log out from CMS">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true" focusable="false">
                  <path d="M16 17L21 12M21 12L16 7M21 12H9M12 17C12 17.93 12 18.395 11.8978 18.7765C11.6204 19.8117 10.8117 20.6204 9.77646 20.8978C9.39496 21 8.92997 21 8 21H7.5C6.10218 21 5.40326 21 4.85195 20.7716C4.11687 20.4672 3.53284 19.8831 3.22836 19.1481C3 18.5967 3 17.8978 3 16.5V7.5C3 6.10217 3 5.40326 3.22836 4.85195C3.53284 4.11687 4.11687 3.53284 4.85195 3.22836C5.40326 3 6.10218 3 7.5 3H8C8.92997 3 9.39496 3 9.77646 3.10222C10.8117 3.37962 11.6204 4.18827 11.8978 5.22354C12 5.60504 12 6.07003 12 7"
                  stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>${xlat['logout']}</span>
              </a>
            </li>
            </#if>
          </ul>
        </div>
      </div>
    </nav>

    <main role="main" class="container page-content" id="main-content" aria-label="Main content">
        <#nested/>
    </main><!-- /.container -->
      <footer class="footer mt-auto py-3 bg-body-tertiary" role="contentinfo" aria-label="Footer">
        <#if !noUp>
        <div class="container align-right">
          <a href="#" aria-label="Go to top of page">${xlat['goto.up']}</a>
        </div>
        </#if>
      </footer>

    <!-- Bootstrap core JavaScript -->
    <script nonce="${_cspNonce}" src="/static/js/bootstrap.min.js"></script>

    <!-- Admin Keyboard Shortcuts -->
    <script nonce="${_cspNonce}" src="/static/js/admin-keyboard-shortcuts.js"></script>
  </body>
</html>
</#macro>
