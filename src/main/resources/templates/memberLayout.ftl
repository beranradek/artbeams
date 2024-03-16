<#macro page>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="CMS" />
    <meta name="keywords" content=""/>
    <title>${xlat['member-section.title']}</title>
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
    </style>
    
    <#-- NOTE: Improvement: Embedding critical CSSs as recommended by Google PageSpeed Insights -->
    <!-- Custom styles for this template -->
    <link rel="stylesheet" type="text/css" href="/static/css/common-styles.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/admin-styles.css" />
    
  </head>
  <body class="d-flex flex-column h-100">
     <#if !noHeader??>
     <!-- Navbar, not fixed to the top (fixed-top), but static with additional mb-4 (bottom padding) -->
    <nav class="navbar navbar-expand-md navbar-dark bg-dark mb-4">
      <div class="container-fluid">
        <a class="navbar-brand" href="/clenska-sekce">${xlat['member-section.title']}</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarCollapse">
          <ul class="navbar-nav mr-4">
            <#if _loggedUser??>
            <li class="nav-item logged-user">
              <a class="nav-link href="#">
                <svg xmlns="http://www.w3.org/2000/svg" height="19px" viewBox="0 0 448 512"><!--! user-solid Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. --><style nonce="${_cspNonce}">svg{fill:#ffffff}</style><path d="M224 256A128 128 0 1 0 224 0a128 128 0 1 0 0 256zm-45.7 48C79.8 304 0 383.8 0 482.3C0 498.7 13.3 512 29.7 512H418.3c16.4 0 29.7-13.3 29.7-29.7C448 383.8 368.2 304 269.7 304H178.3z"/></svg>
                ${_loggedUser.login}
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/logout">${xlat['logout']}</a>
            </li>
            </#if>
          </ul>
        </div>
      </div>
    </nav>
    </#if>

    <main role="main" class="container page-content">
        <#nested/>
    </main><!-- /.container -->
    <#if !noHeader??>
      <footer class="footer mt-auto py-3 bg-body-tertiary">
        <div class="container align-right">
          <a href="#">${xlat['goto.up']}</a>
        </div>
      </footer>
    </#if>
    
    <!-- Bootstrap core JavaScript -->
    <script nonce="${_cspNonce}" src="/static/js/bootstrap.min.js"></script>
  </body>
</html>
</#macro>
