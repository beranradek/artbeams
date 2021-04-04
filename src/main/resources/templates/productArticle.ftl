<#import "/layouts/webLayout.ftl" as layout>
<@layout.page>

<h1 class="blog-post-title">${article.title!}</h1>

<div class="row">

<#if userAccessReport?? && userAccessReport.mobileDevice && article.image??>
  <div class="col-sm-6">
    <div class="article-image-detail">
      <img alt="" src="/media/${article.image}" />
    </div>
  </div>
<#elseif article.imageDetail??>
  <div class="col-sm-6">
    <div class="article-image-detail">
      <img alt="" src="/media/${article.imageDetail}" />
    </div>
  </div>
<#elseif article.image??>
  <div class="col-sm-6">
    <div class="article-image-detail">
      <img alt="" src="/media/${article.image}" />
    </div>
  </div>
</#if>

<#if article.image?? || article.imageDetail??>
  <#assign bodyColClass = "col-sm-6">
<#else>
  <#assign bodyColClass = "col-sm-12">
</#if>
<div class="${bodyColClass}">
  ${article.body!}
</div>

</div>

</@layout.page>
