<#import "/newWebLayout.ftl" as layout>
<#import "/components/articleCardModern.ftl" as articleCard>
<@layout.page>
  <div class="row"><p>Výsledky vyhledávání <strong>'${query}'</strong>:<#if query?length < 2> Zadejte prosím více znaků.</#if></p></div>
  <#list articles as article>
    <@articleCard.articleCardModern article=article />
  </#list>
</@layout.page>
