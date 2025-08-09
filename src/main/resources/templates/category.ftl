<#import "/webLayout.ftl" as layout>
<#import "/articleCard.ftl" as articleCard>
<@layout.page>
  <div class="row g-4">
    <#list articles as article>
      <div class="col-md-4">
        <@articleCard.articleCard article=article />
      </div>
    </#list>
  </div>
</@layout.page>
