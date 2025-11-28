<#import "/newWebLayout.ftl" as layout>
<#import "/components/articleCardModern.ftl" as articleCard>
<@layout.page>
  <#if articles?has_content>
    <#-- Featured Article (first one) -->
    <@articleCard.articleCardModern article=articles[0] featured=true />
    
    <#-- Regular Articles -->
    <#if articles?size gt 1>
      <#list articles[1..] as article>
        <@articleCard.articleCardModern article=article />
      </#list>
    </#if>
    
    <!-- Pagination -->
    <#if pagination??>
      <nav class="my-5">
        <ul class="pagination justify-content-center">
          <#if pagination.hasPrevious>
            <li class="page-item">
              <a class="page-link" href="?page=${pagination.previousPage}"><span data-i18n-key="pagination.previous">Předchozí</span></a>
            </li>
          <#else>
            <li class="page-item disabled">
              <a class="page-link" href="#" tabindex="-1" aria-disabled="true"><span data-i18n-key="pagination.previous">Předchozí</span></a>
            </li>
          </#if>
          
          <#list pagination.pageNumbers as pageNum>
            <#if pageNum == pagination.currentPage>
              <li class="page-item active"><a class="page-link" href="#">${pageNum}</a></li>
            <#else>
              <li class="page-item"><a class="page-link" href="?page=${pageNum}">${pageNum}</a></li>
            </#if>
          </#list>
          
          <#if pagination.hasNext>
            <li class="page-item">
              <a class="page-link" href="?page=${pagination.nextPage}"><span data-i18n-key="pagination.next">Další</span></a>
            </li>
          <#else>
            <li class="page-item disabled">
              <a class="page-link" href="#" tabindex="-1" aria-disabled="true"><span data-i18n-key="pagination.next">Další</span></a>
            </li>
          </#if>
        </ul>
      </nav>
    </#if>
  <#else>
    <div class="text-center py-5">
      <h3><span data-i18n-key="homepage.no.articles.heading">Žádné články zatím nejsou k dispozici.</span></h3>
      <p class="text-muted"><span data-i18n-key="homepage.no.articles.text">Brzy zde najdete zajímavý obsah!</span></p>
    </div>
  </#if>
</@layout.page>
