<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Config" count="${resultPage.pagination.totalCount!0}" countLabel="Total entries">
  <@c.newButton url="/admin/config/new/edit" label="New Config Entry" />
</@c.pageHeader>

<@c.filterBar action="/admin/config" clearUrl="/admin/config" submitLabel="Search">
  <@c.filterField label="Search" forId="searchInput" grow=true>
    <input type="text" class="form-control" id="searchInput" name="search" value="${search!}" placeholder="Search in keys and values">
  </@c.filterField>
</@c.filterBar>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Entry Key</th>
      <th scope="col">Entry Value</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as config>
    <tr>
      <td>${config.entryKey!}</td>
      <td>${config.entryValue!}</td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.editLink url="/admin/config/${config.entryKey}/edit" />
          <@c.deleteForm url="/admin/config/${config.entryKey}" method="DELETE" confirm="Are you sure you want to delete this config entry?" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=3 />
</#list>
  </tbody>
</@c.table>

<#assign searchParam = (search?? && search?has_content)?then("&search=" + search?url, "") />
<@c.pagination pagination=resultPage.pagination additionalParams=searchParam />

</@layout.page>
