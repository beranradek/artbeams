<#import "/components/badgeComponent.ftl" as badge>

<#macro articleCardModern article featured=false>
<#if featured>
  <!-- Featured Article -->
  <div class="card featured-article-card border-0 bg-accent-custom">
    <div class="row g-0">
      <div class="col-md-6">
        <div class="featured-image-wrapper">
          <#if article.image??>
            <img src="/media/${article.image}?size=${xlat['article.img.big.width']}" 
                 class="featured-image" 
                 alt="${article.title}" 
                 loading="lazy" />
          <#else>
            <img src="https://images.unsplash.com/photo-1520206183501-b80df61043c2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" 
                 class="featured-image" 
                 alt="${article.title}" 
                 loading="lazy" />
          </#if>
        </div>
      </div>
      <div class="col-md-6">
        <div class="card-body p-4">
          <div class="mb-2">
            <@badge.categoryBadge article=article featured=true />
            <small class="text-muted ms-2">${article.validFrom?string["d. MMMM yyyy"]}</small>
          </div>
          <h2 class="card-title h3 mb-3">${article.title}</h2>
          <p class="card-text">${(article.perex!"")?truncate(180, "...")}</p>
          <a href="/${article.slug}" class="btn btn-primary-custom">Přečíst článek</a>
        </div>
      </div>
    </div>
  </div>
<#else>
  <!-- Regular Article -->
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
          <h3 class="card-title">${article.title}</h3>
          <p class="card-text">${(article.perex!"")?truncate(150, "...")}</p>
          <a href="/${article.slug}" class="btn btn-sm btn-outline-primary">Přečíst článek</a>
        </div>
      </div>
    </div>
  </div>
</#if>
</#macro>