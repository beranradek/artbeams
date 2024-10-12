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

      .card-holder {
        container-type: inline-size;
        display: block;
        margin: 20px;
      }
      .card-content {
        cursor: pointer;
        position: relative;
        background-color: rgb(248, 248, 248);
        background-repeat: no-repeat;
        background-size: cover;
        min-height: 300px;
        border-width: 1px;
        border-style: solid;
        border-color: rgb(230, 230, 230);
        border-image: initial;
        border-radius: 20px;
        outline: transparent solid 4px;
        transition: outline-color 300ms cubic-bezier(0.4, 0, 0.2, 1);
      }
      .card-content:hover {
        border-color: rgb(26, 26, 26);
        outline-color: rgb(230, 230, 230);
      }
      .card-image {
          min-height: 180px; /* Keep in sync with .card-title-desc top and .card-content min-height */
          border-radius: 20px 20px 0 0;
       }
      .card-title-desc {
        top: 200px;
        font-family: "Montserrat", sans-serif;
        display: flex;
        flex-direction: column;
        position: absolute;
        bottom: 20px;
        left: 20px;
        right: 20px;
      }
      .card-title {
        font-weight: 600;
        font-size: 1.25rem;
        line-height: 1.3;
        letter-spacing: -1px;
        color: rgb(0, 0, 0);
      }
      .card-hline-holder {
        margin-top: 8px;
      }
      .card-hline {
        margin: 0px 0px 10px -20px;
        flex-shrink: 0;
        position: relative;
        height: 4px;
        width: 40px;
        background-color: rgb(233, 4, 30);
        border: 0px solid;
      }
      .card-desc-holder {
          display: flex;
          flex-direction: row;
          -webkit-box-align: center;
          align-items: center;
      }
      .card-desc {
        font-weight: 400;
        font-size: 1rem;
        line-height: 1.5;
        letter-spacing: 0.00938em;
        color: #111111c2;
        /* text-transform: uppercase; */
      }
    </style>
    
    <#-- NOTE: Improvement: Embedding critical CSSs as recommended by Google PageSpeed Insights -->
    <!-- Custom styles for this template -->
    <link rel="stylesheet" type="text/css" href="/static/css/common-styles.css" />
    <link rel="stylesheet" type="text/css" href="/static/css/admin-styles.css" />
    
  </head>
  <body class="d-flex flex-column h-100">
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
              <a class="nav-link" href="/clenska-sekce/muj-profil">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 15.5H7.5C6.10444 15.5 5.40665 15.5 4.83886 15.6722C3.56045 16.06 2.56004 17.0605 2.17224 18.3389C2 18.9067 2 19.6044 2 21M14.5 7.5C14.5 9.98528 12.4853 12 10 12C7.51472 12 5.5 9.98528 5.5 7.5C5.5 5.01472 7.51472 3 10 3C12.4853 3 14.5 5.01472 14.5 7.5ZM11 21L14.1014 20.1139C14.2499 20.0715 14.3241 20.0502 14.3934 20.0184C14.4549 19.9902 14.5134 19.9558 14.5679 19.9158C14.6293 19.8707 14.6839 19.8161 14.7932 19.7068L21.25 13.25C21.9404 12.5597 21.9404 11.4403 21.25 10.75C20.5597 10.0596 19.4404 10.0596 18.75 10.75L12.2932 17.2068C12.1839 17.3161 12.1293 17.3707 12.0842 17.4321C12.0442 17.4866 12.0098 17.5451 11.9816 17.6066C11.9497 17.6759 11.9285 17.7501 11.8861 17.8987L11 21Z"
                  stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span title="${_loggedUser.login}">Můj profil</span>
                </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/logout">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16 17L21 12M21 12L16 7M21 12H9M12 17C12 17.93 12 18.395 11.8978 18.7765C11.6204 19.8117 10.8117 20.6204 9.77646 20.8978C9.39496 21 8.92997 21 8 21H7.5C6.10218 21 5.40326 21 4.85195 20.7716C4.11687 20.4672 3.53284 19.8831 3.22836 19.1481C3 18.5967 3 17.8978 3 16.5V7.5C3 6.10217 3 5.40326 3.22836 4.85195C3.53284 4.11687 4.11687 3.53284 4.85195 3.22836C5.40326 3 6.10218 3 7.5 3H8C8.92997 3 9.39496 3 9.77646 3.10222C10.8117 3.37962 11.6204 4.18827 11.8978 5.22354C12 5.60504 12 6.07003 12 7"
                  stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                ${xlat['logout']}
              </a>
            </li>
            </#if>
          </ul>
        </div>
      </div>
    </nav>

    <main role="main" class="container page-content">
        <#nested/>
    </main><!-- /.container -->
    
    <!-- Bootstrap core JavaScript -->
    <script nonce="${_cspNonce}" src="/static/js/bootstrap.min.js"></script>
  </body>
</html>
</#macro>
