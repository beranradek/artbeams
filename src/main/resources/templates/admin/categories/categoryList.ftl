<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Categories" count="${resultPage.pagination.totalCount!0}" countLabel="Total categories">
  <@c.newButton url="/admin/categories/${emptyId}/edit" label="New Category" />
</@c.pageHeader>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Slug</th>
      <th scope="col">Last modified</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as category>
    <tr>
      <td>${category.title!}</td>
      <td>${category.slug!}</td>
      <td><#if category.common.modified??>${category.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.editLink url="/admin/categories/${category.id}/edit" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=4 />
</#list>
  </tbody>
</@c.table>

<@c.pagination pagination=resultPage.pagination />

</@layout.page>
