<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Articles" count="${resultPage.pagination.totalCount!0}" countLabel="Total articles">
  <#if canPublish!false>
    <a class="btn btn-outline-secondary btn-sm" href="/admin/evernote/import"><@c.icon "refresh"/> Sync with Evernote</a>
    <a class="btn btn-outline-secondary btn-sm" href="/admin/google-docs/authorization"><@c.icon "log-in"/> Authorize Google Docs</a>
  </#if>
  <@c.newButton url="/admin/articles/${emptyId}/edit" label="New Article" />
</@c.pageHeader>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Slug</th>
      <th scope="col">External ID</th>
      <th scope="col">Last modified</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as article>
    <tr>
      <td>${article.title!}</td>
      <td>${article.slug!}</td>
      <td>${article.externalId!}</td>
      <td><#if article.common.modified??>${article.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.editLink url="/admin/articles/${article.id}/edit" />
          <@c.deleteForm url="/admin/articles/${article.id}/delete" confirm="Do you really want to delete this article?" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=5 />
</#list>
  </tbody>
</@c.table>

<@c.pagination pagination=resultPage.pagination />

</@layout.page>
