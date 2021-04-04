<#import "/layouts/adminLayout.ftl" as layout>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>
<h1>Updated articles</h1>
<#list updatedArticles as article>
    ${article.title}<br/>
</#list>
</@layout.page>
