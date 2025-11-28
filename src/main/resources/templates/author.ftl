<#macro author>
<div class="card mb-3 author">
  <div class="row g-0">
    <div class="col-md-1">
      <img alt="${xlat['author.name']}" src="${xlat['author.img.src']}" loading="lazy" width="${xlat['author.img.width']}" height="${xlat['author.img.height']}" class="img-fluid rounded-start avatar" />
    </div>
    <div class="col-md-11">
      <div class="card-body">
        <strong class="card-title text-gray-dark"><span data-i18n-key="author.name">${xlat['author.name']}</span></strong>
        <p class="card-text"><small class="text-body-secondary"><span data-i18n-key="author.about">${xlat['author.about']}</span></small></p>
      </div>
    </div>
  </div>
</div>
</#macro>
