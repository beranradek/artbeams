<#import "/adminLayout.ftl" as layout>
<@layout.page>

<a class="btn btn-primary" href="/admin/articles/${emptyId}/edit" role="button">New Article</a>
<a class="btn btn-secondary" href="/admin/evernote/import" role="button">Sync with Evernote</a>

<table class="table table-sm">
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
<#list articles as article>
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
</@layout.page>
