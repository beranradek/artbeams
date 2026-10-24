<#import "/adminComponents.ftl" as c>
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

    
    <#-- NOTE: Improvement: Embedding critical CSSs as recommended by Google PageSpeed Insights -->
    <!-- Custom styles for this template -->
    <link rel="stylesheet" type="text/css" href="/static/css/common-styles.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/admin-styles.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/sales-styles.css" />
    <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <#-- Admin-only theme, must stay last to override the common styles -->
    <link rel="stylesheet" type="text/css" href="/static/css/admin-ui.css?v15" />

    <#include "/commonScripts.ftl">
  </head>
  <body class="admin-ui d-flex flex-column h-100">
     <!-- Skip to main content link for keyboard navigation -->
     <a href="#main-content" class="skip-to-main">Skip to main content</a>
     
     <!-- Navbar, not fixed to the top (fixed-top), but static with additional mb-4 (bottom padding) -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark" role="navigation" aria-label="Main navigation">
      <div class="container-fluid">
        <a class="navbar-brand" href="/admin" aria-label="CMS Administration Home">CMS</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarCollapse">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0" role="menubar">
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/articles" role="menuitem" aria-label="Manage articles">Articles</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/categories" role="menuitem" aria-label="Manage categories">Categories</a>
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
              <a class="nav-link" href="/admin/courses" role="menuitem" aria-label="Manage courses">Courses</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/orders" role="menuitem" aria-label="Manage orders">Orders</a>
            </li>
            <li class="nav-item" role="none">
              <a class="nav-link" href="/admin/users" role="menuitem" aria-label="Manage users">Users</a>
            </li>
            <li class="nav-item nav-secondary-inline" role="none">
              <a class="nav-link" href="/admin/faqs?entityType=HOMEPAGE&entityId=homepage" role="menuitem">Homepage FAQs</a>
            </li>
            <li class="nav-item nav-secondary-inline" role="none">
              <a class="nav-link" href="/admin/consents" role="menuitem">Consents</a>
            </li>
            <li class="nav-item nav-secondary-inline" role="none">
              <a class="nav-link" href="/admin/localisations" role="menuitem">Localisations</a>
            </li>
            <li class="nav-item nav-secondary-inline" role="none">
              <a class="nav-link" href="/admin/config" role="menuitem">Config</a>
            </li>
            <li class="nav-item nav-secondary-inline" role="none">
              <a class="nav-link" href="/admin/activity-logs" role="menuitem">Activity Logs</a>
            </li>
            <li class="nav-item nav-secondary-inline" role="none">
              <a class="nav-link" href="/admin/system-events" role="menuitem">System Events</a>
            </li>
            <li class="nav-item nav-secondary-inline" role="none">
              <a class="nav-link" href="/admin/search-console" role="menuitem">Search Console</a>
            </li>
            <li class="nav-item dropdown nav-more" role="none">
              <a class="nav-link dropdown-toggle" href="#" id="moreDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">More</a>
              <ul class="dropdown-menu" aria-labelledby="moreDropdown">
                <li><a class="dropdown-item" href="/admin/faqs?entityType=HOMEPAGE&entityId=homepage">Homepage FAQs</a></li>
                <li><a class="dropdown-item" href="/admin/consents">Consents</a></li>
                <li><a class="dropdown-item" href="/admin/localisations">Localisations</a></li>
                <li><a class="dropdown-item" href="/admin/config">Config</a></li>
                <li><a class="dropdown-item" href="/admin/activity-logs">Activity Logs</a></li>
                <li><a class="dropdown-item" href="/admin/system-events">System Events</a></li>
                <li><a class="dropdown-item" href="/admin/search-console">Search Console</a></li>
              </ul>
            </li>
          </ul>
  
          <#-- Actions dropdown menu -->
          <div class="dropdown me-lg-2 mb-2 mb-lg-0">
            <button class="btn btn-sm btn-outline-light dropdown-toggle" type="button" id="actionsDropdown" data-bs-toggle="dropdown" aria-expanded="false" aria-label="Actions menu" title="Actions">
              <@c.icon "ellipsis"/><span class="d-lg-none d-wide-inline"> Actions</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="actionsDropdown">
              <#if _isRemoteDbConfigured?? && _isRemoteDbConfigured>
              <li>
                <a class="dropdown-item" href="/admin/sync/confirm" aria-label="Synchronize remote database">
                  <@c.icon "refresh"/> Sync remote DB
                </a>
              </li>
              <li><hr class="dropdown-divider"></li>
              </#if>
              <li>
                <a class="dropdown-item action-reload-config" href="#" aria-label="Reload configuration from database">
                  <@c.icon "refresh"/> Reload config
                </a>
              </li>
              <li>
                <a class="dropdown-item action-reload-localisations" href="#" aria-label="Reload localisations from database">
                  <@c.icon "globe"/> Reload localisations
                </a>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <a class="dropdown-item action-reindex-all" href="#" aria-label="Rebuild search index in background">
                  <@c.icon "search"/> Reindex all
                </a>
              </li>
            </ul>
          </div>

          <#-- Hidden forms for POST actions triggered via JavaScript -->
          <form id="form-config-reload" class="d-none" action="/admin/config/reload" method="POST">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          </form>
          <form id="form-localisations-reload" class="d-none" action="/admin/localisations/reload" method="POST">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          </form>
          <form id="form-reindex-all" class="d-none" action="/admin/actions/reindex" method="POST">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          </form>

          <ul class="navbar-nav ms-lg-2" role="menubar">
            <li class="nav-item" role="none">
              <a class="nav-link" href="/" role="menuitem" aria-label="View public website" title="Public web"><@c.icon "external"/><span class="d-lg-none d-wide-inline"> Public web</span></a>
            </li>
            <#if _loggedUser??>
            <li class="nav-item dropdown logged-user" role="none">
              <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false" aria-label="User menu: ${_loggedUser.login}" title="${_loggedUser.login}">
                <@c.icon "user"/> <span class="d-lg-none d-wide-inline">${_loggedUser.login}</span>
              </a>
              <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                <li><span class="dropdown-item-text text-muted small">Signed in as <strong>${_loggedUser.login}</strong></span></li>
                <li><a class="dropdown-item" href="/admin/users/${_loggedUser.id}/edit"><@c.icon "pencil"/> My profile</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="/logout"><@c.icon "log-out"/> ${xlat['logout']}</a></li>
              </ul>
            </li>
            <li class="nav-item d-none d-lg-block" role="none">
              <a class="nav-link" href="/logout" role="menuitem" aria-label="Log out from CMS" title="${xlat['logout']}">
                <@c.icon "log-out"/>
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
    <#if !noUp>
    <button type="button" id="admin-to-top" class="admin-to-top" title="${xlat['goto.up']}" aria-label="${xlat['goto.up']}"><@c.icon "arrow-up"/></button>
    </#if>

    <!-- Bootstrap core JavaScript (bundle includes Popper for dropdowns) -->
    <script nonce="${_cspNonce}" src="/static/js/bootstrap.bundle.min.js"></script>

    <!-- Admin Keyboard Shortcuts -->
    <script nonce="${_cspNonce}" src="/static/js/admin-keyboard-shortcuts.js"></script>
    <script nonce="${_cspNonce}" src="/static/js/admin-ui.js?v7"></script>

    <!-- Actions dropdown handlers -->
    <script nonce="${_cspNonce}">
      document.addEventListener('DOMContentLoaded', function() {
        // Handle Reload config action
        const reloadConfigLink = document.querySelector('.action-reload-config');
        if (reloadConfigLink) {
          reloadConfigLink.addEventListener('click', function(e) {
            e.preventDefault();
            document.getElementById('form-config-reload').submit();
          });
        }

        // Handle Reload localisations action
        const reloadLocalisationsLink = document.querySelector('.action-reload-localisations');
        if (reloadLocalisationsLink) {
          reloadLocalisationsLink.addEventListener('click', function(e) {
            e.preventDefault();
            document.getElementById('form-localisations-reload').submit();
          });
        }

        // Handle Reindex all action
        const reindexAllLink = document.querySelector('.action-reindex-all');
        if (reindexAllLink) {
          reindexAllLink.addEventListener('click', function(e) {
            e.preventDefault();
            document.getElementById('form-reindex-all').submit();
          });
        }
      });
    </script>
  </body>
</html>
</#macro>
