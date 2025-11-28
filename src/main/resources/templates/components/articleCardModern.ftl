<#import "/components/badgeComponent.ftl" as badge>
<#import "/components/responsiveImage.ftl" as img>

<#macro articleCardModern article featured=false>
<#if featured>
  <!-- Featured Article -->
  <div class="card featured-article-card border-0 bg-accent-custom">
    <div class="row g-0">
      <div class="col-md-6">
        <div class="featured-image-wrapper">
          <@img.articleCardImage imageName=article.image alt=article.title cssClass="featured-image" />
        </div>
      </div>
      <div class="col-md-6">
        <div class="card-body p-4">
          <div class="mb-2">
            <@badge.categoryBadge article=article featured=true />
            <small class="text-muted ms-2">${article.validFrom?string["d. MMMM yyyy"]}</small>
          </div>
          <h2 class="card-title h3 mb-3"><a href="/${article.slug}" class="text-decoration-none"><span data-article-title data-article-id="${article.id}">${article.title}</span></a></h2>
          <p class="card-text"><span data-article-perex data-article-id="${article.id}">${(article.perex!"")?truncate(350, "...")}</span></p>
          <a href="/${article.slug}" class="btn btn-primary-custom"><span data-i18n-key="article.read.button">Přečíst článek</span></a>
        </div>
      </div>
    </div>
  </div>
<#else>
  <!-- Regular Article -->
  <div class="card article-card border-0 bg-light">
    <div class="row g-0">
      <div class="col-md-4">
        <@img.articleCardImage imageName=article.image alt=article.title />
      </div>
      <div class="col-md-8">
        <div class="card-body">
          <div class="mb-2">
            <@badge.categoryBadge article=article />
            <small class="text-muted ms-2">${article.validFrom?string["d. MMMM yyyy"]}</small>
          </div>
          <h3 class="card-title"><a href="/${article.slug}" class="text-decoration-none"><span data-article-title data-article-id="${article.id}">${article.title}</span></a></h3>
          <p class="card-text"><span data-article-perex data-article-id="${article.id}">${(article.perex!"")?truncate(350, "...")}</span></p>
          <a href="/${article.slug}" class="btn btn-sm btn-outline-primary"><span data-i18n-key="article.read.button">Přečíst článek</span></a>
        </div>
      </div>
    </div>
  </div>
</#if>
</#macro>
