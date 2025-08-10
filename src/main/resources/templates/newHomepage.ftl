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
              <a class="page-link" href="?page=${pagination.previousPage}">Předchozí</a>
            </li>
          <#else>
            <li class="page-item disabled">
              <a class="page-link" href="#" tabindex="-1" aria-disabled="true">Předchozí</a>
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
              <a class="page-link" href="?page=${pagination.nextPage}">Další</a>
            </li>
          <#else>
            <li class="page-item disabled">
              <a class="page-link" href="#" tabindex="-1" aria-disabled="true">Další</a>
            </li>
          </#if>
        </ul>
      </nav>
    </#if>
  <#else>
    <div class="text-center py-5">
      <h3>Žádné články zatím nejsou k dispozici.</h3>
      <p class="text-muted">Brzy zde najdete zajímavý obsah!</p>
    </div>
  </#if>
</@layout.page>