<#import "/layouts/webLayout.ftl" as layout>
<#import "/articleCard.ftl" as articleCard>
<@layout.page>
  <#list articles as article>
    <@articleCard.articleCard article=article />
  </#list>
</@layout.page>
