<#import "/newWebLayout.ftl" as layout>
<#import "/components/articleCardModern.ftl" as articleCard>
<@layout.page>
  <div class="row"><p><span data-i18n-key="search.results.label">Výsledky vyhledávání</span> <strong>'${query}'</strong>:<#if query?length < 2> <span data-i18n-key="search.enter.more.chars">Zadejte prosím více znaků.</span></#if></p></div>
  <#list articles as article>
    <@articleCard.articleCardModern article=article />
  </#list>
</@layout.page>
