<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pagination>
<@layout.page noUp=true>

<a class="btn btn-primary" href="/admin/articles/${emptyId}/edit" role="button">New Article</a>
<a class="btn btn-secondary" href="/admin/evernote/import" role="button">Sync with Evernote</a>
<a class="btn btn-secondary" href="/admin/google-docs/authorization" role="button">Authorize Google Docs</a>

<table class="table table-sm admin-table">
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Slug</th>
      <th scope="col">External ID</th>
      <th scope="col">Last modified</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as article>
    <tr>
        <td>${article.title!}</td>
        <td>${article.slug!}</td>
        <td>${article.externalId!}</td>
        <td><#if article.common.modified??>${article.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
        <td><a href="/admin/articles/${article.id}/edit">Edit</a></td>
    </tr>
</#list>
  </tbody>
</table>

<@pagination.pagination pagination=resultPage.pagination />

</@layout.page>
