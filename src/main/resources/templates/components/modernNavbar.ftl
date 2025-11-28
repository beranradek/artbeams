<#macro modernNavbar>
<!-- Modern Navbar -->
<nav class="navbar navbar-expand-md navbar-light bg-white nav-custom">
  <div class="container-fluid container-fluid-custom">
    <a class="navbar-brand fw-bold" href="/">
      <span class="text-primary-custom">Vysněné</span><span class="text-secondary-custom">Zdraví</span>
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarCollapse">
        <ul class="navbar-nav me-auto mb-2 mb-md-0">
          <#if xlat['menu.item1.title']??>
            <li class="nav-item">
              <a class="nav-link nav-link-custom" href="${xlat['menu.item1.url']}"><span data-i18n-key="menu.item1.title">${xlat['menu.item1.title']}</span></a>
            </li>
          </#if>
          <#if xlat['menu.item2.title']??>
            <li class="nav-item">
              <a class="nav-link nav-link-custom" href="${xlat['menu.item2.url']}"><span data-i18n-key="menu.item2.title">${xlat['menu.item2.title']}</span></a>
            </li>
          </#if>
          <#if xlat['menu.item3.title']??>
            <li class="nav-item">
              <a class="nav-link nav-link-custom" href="${xlat['menu.item3.url']}"><span data-i18n-key="menu.item3.title">${xlat['menu.item3.title']}</span></a>
            </li>
          </#if>
          <#if xlat['menu.item4.title']??>
            <li class="nav-item">
              <a class="nav-link nav-link-custom" href="${xlat['menu.item4.url']}"><span data-i18n-key="menu.item4.title">${xlat['menu.item4.title']}</span></a>
            </li>
          </#if>
          <#if xlat['menu.item5.title']??>
            <li class="nav-item">
              <a class="nav-link nav-link-custom" href="${xlat['menu.item5.url']}"><span data-i18n-key="menu.item5.title">${xlat['menu.item5.title']}</span></a>
            </li>
          </#if>
          <#if xlat['menu.item6.title']??>
            <li class="nav-item">
              <a class="nav-link nav-link-custom" href="${xlat['menu.item6.url']}"><span data-i18n-key="menu.item6.title">${xlat['menu.item6.title']}</span></a>
            </li>
          </#if>
        </ul>
        <form method="get" action="/search" class="d-flex" role="search">
          <input type="search" name="query" class="form-control search-box me-2" placeholder="Hledat" aria-label="${xlat['search']}">
          <button class="btn btn-primary-custom" type="submit"><span data-i18n-key="search">${xlat['search']}</span></button>
        </form>
   </div>
 </div>
</nav>
</#macro>