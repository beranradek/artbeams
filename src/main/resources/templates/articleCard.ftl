<#macro articleCard article>
  <div class="blog-item row border rounded overflow-hidden shadow-sm" style="width:100%">
    <#if article.image??>
      <div class="blog-item-image">
        <a href="/${article.slug}"><img alt="${article.image}" src="/media/${article.image}" height="260" width="260" /></a>
      </div>
    </#if>
    <div class="blog-item-text<#if article.image??> col-md-6<#else> col-md-12</#if>">
      <h3 class="item-title mb-0"><a href="/${article.slug}">${article.title}</a></h3>
      <div class="item-date mb-1 text-muted">${article.validFrom?string["d.M.yyyy, HH:mm"]}</div>
      <p class="item-perex mb-auto">${(article.perex)[0..*350]}</p>
    </div>
  </div>
  <div style="clear:both"></div>
</#macro>
