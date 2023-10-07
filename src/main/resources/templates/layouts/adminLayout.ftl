<#macro page>
<#import "/css/common.ftl" as commonCss>
<#import "/css/mainAdmin.ftl" as mainAdminCss>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="CMS" />
    <meta name="keywords" content=""/>
    <title>CMS Administration</title>
    <link rel="shortcut icon" href="${xlat['favicon.img.src']}" />
    <!-- Bootstrap -->
    <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/4.1.3/css/bootstrap.min.css" />
    <script src="/webjars/jquery/3.0.0/jquery.min.js"></script>

    <#-- Embedding critical CSSs as recommended by Google PageSpeed Insights -->
    <style>
      <@commonCss.common></@commonCss.common>
      <@mainAdminCss.mainAdmin></@mainAdminCss.mainAdmin>
    </style>
  </head>
  <body>
     <#-- Layout based on https://getbootstrap.com/docs/4.2/examples/starter-template/ -->
     <#if !noHeader??>
     <nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
      <a class="navbar-brand" href="/admin">CMS</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarsExampleDefault">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item active">
            <a class="nav-link" href="/admin/users">Users <span class="sr-only">(current)</span></a>
          </li>
          <li class="nav-item active">
            <a class="nav-link" href="/admin/categories">Categories <span class="sr-only">(current)</span></a>
          </li>
          <li class="nav-item active">
            <a class="nav-link" href="/admin/articles">Articles <span class="sr-only">(current)</span></a>
          </li>
          <li class="nav-item active">
            <a class="nav-link" href="/admin/media">Media <span class="sr-only">(current)</span></a>
          </li>
          <li class="nav-item active">
            <a class="nav-link" href="/admin/products">Products <span class="sr-only">(current)</span></a>
          </li>

          <#--
          <li class="nav-item">
            <a class="nav-link" href="#">Link</a>
          </li>
          <li class="nav-item">
            <a class="nav-link disabled" href="#">Disabled</a>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="dropdown01" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Dropdown</a>
            <div class="dropdown-menu" aria-labelledby="dropdown01">
              <a class="dropdown-item" href="#">Action</a>
              <a class="dropdown-item" href="#">Another action</a>
              <a class="dropdown-item" href="#">Something else here</a>
            </div>
          </li>
          -->
        </ul>

        <form class="form-inline my-2 my-lg-0" action="/admin/config/reload" method="POST" style="margin-right:4px">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <button class="btn btn-secondary my-2 my-sm-0" type="submit">Reload config</button>
        </form>

        <form class="form-inline my-2 my-lg-0" action="/admin/localisations/reload" method="POST" style="margin-right:4px">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <button class="btn btn-secondary my-2 my-sm-0" type="submit">Reload localisations</button>
        </form>

        <ul class="navbar-nav mr-4">
          <li class="nav-item">
            <a class="nav-link" href="/">Public web</a>
          </li>
          <#if _loggedUser??>
            <li class="nav-item dropdown logged-user">
              <a class="nav-link active" href="#" id="dropdownUser" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><svg xmlns="http://www.w3.org/2000/svg" height="19px" viewBox="0 0 448 512"><!--! user-solid Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. --><style>svg{fill:#ffffff}</style><path d="M224 256A128 128 0 1 0 224 0a128 128 0 1 0 0 256zm-45.7 48C79.8 304 0 383.8 0 482.3C0 498.7 13.3 512 29.7 512H418.3c16.4 0 29.7-13.3 29.7-29.7C448 383.8 368.2 304 269.7 304H178.3z"/></svg> ${_loggedUser.login}</a>
              <div class="dropdown-menu" aria-labelledby="dropdownUser">
                <a class="dropdown-item" href="/logout">${xlat['logout']}</a>
              </div>
            </li>
          </#if>
        </ul>
        <form class="form-inline my-2 my-lg-0">
          <input class="form-control mr-sm-2" type="text" placeholder="Search" aria-label="Search">
          <button class="btn btn-secondary my-2 my-sm-0" type="submit">Search</button>
        </form>
      </div>
    </nav>
    </#if>

    <main role="main" class="container">
        <#nested/>
    </main><!-- /.container -->
    <#if !noHeader??>
      <footer class="footer mt-auto py-3">
        <div class="container" style="text-align:right">
          <a href="#">${xlat['goto.up']}</a>
        </div>
      </footer>
    </#if>
    <!-- Bootstrap core JavaScript -->
    <script src="/webjars/popper.js/1.14.3/popper.min.js"></script>
    <script src="/webjars/bootstrap/4.1.3/js/bootstrap.min.js"></script>
  </body>
</html>
</#macro>
