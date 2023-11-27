<#import "/adminLayout.ftl" as layout>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>
<h1>User accesses to entities were aggregated</h1>
</@layout.page>
