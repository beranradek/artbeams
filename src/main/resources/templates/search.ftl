<#import "/layouts/webLayout.ftl" as layout>
<#import "/articleCard.ftl" as articleCard>
<@layout.page>
  <div class="row"><p>Výsledky vyhledávání <strong>'${query}'</strong>:<#if query?length < 2> Zadejte prosím více znaků.</#if></p></div>
  <#list articles as article>
    <@articleCard.articleCard article=article />
  </#list>
</@layout.page>
