<#import "/newWebLayout.ftl" as layout>
<#import "/components/articleCardModern.ftl" as articleCard>
<@layout.page>
  <div class="row">
    <div class="col-12">
      <h2><span data-i18n-key="search.results.label">Výsledky vyhledávání</span>: <strong>'${query}'</strong></h2>
      <#if query?length < 2>
        <p class="text-muted"><span data-i18n-key="search.enter.more.chars">Zadejte prosím více znaků.</span></p>
      <#else>
        <p class="text-muted">Nalezeno <strong>${totalResults!0}</strong> výsledků</p>
      </#if>
    </div>
  </div>

  <#-- Categories Section -->
  <#if categories?? && (categories?size > 0)>
    <div class="row mt-4">
      <div class="col-12">
        <h3>Rubriky (${categories?size})</h3>
      </div>
    </div>
    <div class="row">
      <#list categories as category>
        <div class="col-md-6 col-lg-4 mb-3">
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">
                <a href="/kategorie/${category.slug}">${category.title}</a>
              </h5>
              <#if category.description??>
                <p class="card-text text-muted">${category.description?truncate(150, '...')}</p>
              </#if>
            </div>
          </div>
        </div>
      </#list>
    </div>
  </#if>

  <#-- Products Section -->
  <#if products?? && (products?size > 0)>
    <div class="row mt-4">
      <div class="col-12">
        <h3>Produkty (${products?size})</h3>
      </div>
    </div>
    <div class="row">
      <#list products as product>
        <div class="col-md-6 col-lg-4 mb-3">
          <div class="card h-100">
            <#if product.metadata?? && product.metadata.listingImage??>
              <img src="/media/${product.metadata.listingImage}?size=300" class="card-img-top" alt="${product.title}" loading="lazy">
            </#if>
            <div class="card-body">
              <h5 class="card-title">
                <a href="/produkt/${product.slug}">${product.title}</a>
              </h5>
              <#if product.description??>
                <p class="card-text">${product.description?truncate(100, '...')}</p>
              </#if>
            </div>
          </div>
        </div>
      </#list>
    </div>
  </#if>

  <#-- Articles Section -->
  <#if articles?? && (articles?size > 0)>
    <div class="row mt-4">
      <div class="col-12">
        <h3>Články (${articles?size})</h3>
      </div>
    </div>
    <#list articles as result>
      <#-- Create article-like object from search result for articleCard -->
      <#assign article = {
        "id": result.entityId,
        "slug": result.slug,
        "title": result.title,
        "perex": result.description!"",
        "image": result.metadata.image!"",
        "validFrom": .now
      }>
      <@articleCard.articleCardModern article=article />
    </#list>
  </#if>

  <#-- No results message -->
  <#if (!categories?? || categories?size == 0) && (!products?? || products?size == 0) && (!articles?? || articles?size == 0) && query?? && (query?length >= 2)>
    <div class="row mt-4">
      <div class="col-12">
        <div class="alert alert-info" role="alert">
          <h4 class="alert-heading">Žádné výsledky</h4>
          <p>Pro váš dotaz <strong>'${query}'</strong> nebyly nalezeny žádné výsledky.</p>
          <hr>
          <p class="mb-0">Zkuste změnit vyhledávací výraz nebo použijte jiná klíčová slova.</p>
        </div>
      </div>
    </div>
  </#if>

</@layout.page>
