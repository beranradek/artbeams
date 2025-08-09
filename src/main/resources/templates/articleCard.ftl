<#macro articleCard article>

  <div class="card content-card h-100 shadow-sm border-0">
    <#if article.image??>
      <a href="/${article.slug}" class="featured-image-wrapper"><img alt="${article.image}" src="/media/${article.image}?size=${xlat['article.img.small.width']}" loading="lazy" class="featured-image card-img-top" /></a>
    </#if>
    <div class="card-body d-flex flex-column">
      <h3 class="card-title item-title mb-2"><a href="/${article.slug}" class="stretched-link text-primary-custom">${article.title}</a></h3>
      <span class="item-date mb-1 text-muted">${article.validFrom?string["d.M.yyyy, HH:mm"]}</span>
      <p class="card-text item-perex mb-auto">${(article.perex)[0..*350]}</p>
    </div>
  </div>
</#macro>
