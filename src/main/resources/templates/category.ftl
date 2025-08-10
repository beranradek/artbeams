<#import "/newWebLayout.ftl" as layout>
<#import "/components/articleCardModern.ftl" as articleCard>
<@layout.page>
  <#list articles as article>
    <@articleCard.articleCardModern article=article />
  </#list>
</@layout.page>
