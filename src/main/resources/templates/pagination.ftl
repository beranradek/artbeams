<#macro pagination pagination>
<#if pagination.pageCount gt 1>
<div class="pagination">
  <#if !pagination.isFirstPage()>
    <a href="?offset=${pagination.previousOffset}&limit=${pagination.limit}">&laquo; Previous</a>
  </#if>
  <#list 1..pagination.pageCount as page>
    <a href="?offset=${(page - 1) * pagination.limit}&limit=${pagination.limit}"<#if page == pagination.page> class="active"</#if>>${page}</a>
  </#list>
  <#if !pagination.isLastPage()>
    <a href="?offset=${pagination.nextOffset}&limit=${pagination.limit}">Next &raquo;</a>
  </#if>
</div>

<style>
  .pagination a {
    margin: 0 5px;
    text-decoration: none;
  }
  .pagination a.active {
    font-weight: bold;
  }
</style>
</#if>
</#macro>
