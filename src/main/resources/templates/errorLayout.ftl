<#macro page>
<!DOCTYPE html>
<html lang="cs">
 <head>
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="author" content="${xlat['author.name']}" />
  <#assign description = "${xlat['website.description']}">
  <meta name="description" content="${description?html!}" />
  <link rel="shortcut icon" href="${xlat['favicon.img.src']}" />

  <meta property="og:url" content="${_url}" />
  <meta property="og:locale" content="${xlat['website.locale']}" />
  <meta property="og:description" content="${description?html!}" />

  <title><#if title??>${title} | </#if>${xlat['website.title']}</title>

  <!-- Only one CSS file composed from Bootstrap, common-styles and public-styles: -->
  <!-- Boostrap template is based on https://getbootstrap.com/docs/5.3/examples/navbar-static/ and some regular non-sticky footer -->
  <link href="/static/css/styles.css" type="text/css" rel="stylesheet">

  </head>
  <body class="d-flex flex-column h-100">
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

  </header>

  <#if errorMessage??>
    <div class="alert alert-danger" role="alert">${errorMessage}</div>
  </#if>

  <!-- Page content -->
  <main role="main" class="page-content flex-shrink-0 container">
      <div class="blog-main">
        <#-- Content replaced by the content fragment of the page displayed -->
        <#nested/>
      </div>
  </main>

  <!-- Bootstrap core JavaScript -->
  <script src="/static/js/bootstrap.min.js"></script>
 </body>
</html>
</#macro>
