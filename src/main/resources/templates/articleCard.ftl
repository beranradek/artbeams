<#macro articleCard article>

  <div class="blog-item border rounded overflow-hidden shadow-sm p-3">
    <#if article.image??>
      <a href="/${article.slug}" class="blog-item-image"><img alt="${article.image}" src="/media/${article.image}?size=${xlat['article.img.small.width']}" loading="lazy" height="${xlat['article.img.small.width']}" width="${xlat['article.img.small.width']}" /></a>
    </#if>
    <span class="blog-item-text">
        <h3 class="item-title mb-0"><a href="/${article.slug}">${article.title}</a></h3>
        <span class="item-date mb-1 text-muted">${article.validFrom?string["d.M.yyyy, HH:mm"]}</span>
        <p class="item-perex mb-auto">${(article.perex)[0..*350]}</p>
    </span>
  </div>
</#macro>
