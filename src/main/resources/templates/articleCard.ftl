<#import "/components/badgeComponent.ftl" as badge>

<#macro articleCard article>
<!-- Modern Article Card -->
<div class="card article-card border-0 bg-light">
  <div class="row g-0">
    <div class="col-md-4">
      <#if article.image??>
        <img src="/media/${article.image}?size=${xlat['article.img.small.width']}" 
             class="article-image" 
             alt="${article.title}" 
             loading="lazy" />
      <#else>
        <img src="https://images.unsplash.com/photo-1520206183501-b80df61043c2?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" 
             class="article-image" 
             alt="${article.title}" 
             loading="lazy" />
      </#if>
    </div>
    <div class="col-md-8">
      <div class="card-body">
        <div class="mb-2">
          <@badge.categoryBadge article=article />
          <small class="text-muted ms-2">${article.validFrom?string["d. MMMM yyyy"]}</small>
        </div>
        <h3 class="card-title"><a href="/${article.slug}" class="text-decoration-none">${article.title}</a></h3>
        <p class="card-text">${(article.perex!"")?truncate(150, "...")}</p>
        <a href="/${article.slug}" class="btn btn-sm btn-outline-primary">Přečíst článek</a>
      </div>
    </div>
  </div>
</div>
</#macro>
