<#macro heroSection>
<!-- Hero Section with enhanced Author -->
<header class="hero-section">
  <div class="container py-5">
    <div class="row align-items-center">
      <div class="col-lg-7 mb-5 mb-lg-0">
        <h1 class="display-4 fw-bold mb-4">${xlat['website.title']}</h1>
        <p class="lead mb-4">
          ${xlat['website.description']}
        </p>
        <div class="hero-cta-buttons d-grid gap-3 d-md-flex">
          <#if xlat['headline.offer.url']??>
            <a href="${xlat['headline.offer.url']}" class="btn btn-primary-custom btn-lg px-4">${xlat['headline.offer.action']!'Více informací'}</a>
          </#if>
          <#if xlat['contact.url']??>
            <a href="${xlat['contact.url']}" class="btn btn-outline-light btn-lg px-4">Kontaktujte mě</a>
          </#if>
        </div>
      </div>
      <div class="col-lg-5">
        <div class="card author-card shadow-lg border-0">
          <div class="card-body text-center">
            <img src="/static/images/author.webp" 
                 class="author-profile-image mb-3" 
                 alt="Autor ${xlat['author.name']}" 
                 width="200" 
                 height="200" />
            <div class="h4 card-title text-secondary-custom mb-2">${xlat['author.name']}</div>
            <p class="card-text text-light">${xlat['author.description']!'Pomohu Vám zaléčit chronické potíže a dosáhnout kvalitní regenerace skvělými spánkovými návyky.'}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</header>
</#macro>
