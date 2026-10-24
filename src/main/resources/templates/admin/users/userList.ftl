<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Users" count="${resultPage.pagination.totalCount!0}" countLabel="Total users">
  <@c.newButton url="/admin/users/${emptyId}/edit" label="New User" />
</@c.pageHeader>

<@c.filterBar action="/admin/users" clearUrl="/admin/users">
  <@c.filterField label="Search" forId="searchInput" grow=true>
    <input type="text" class="form-control" id="searchInput" name="search" value="${searchTerm}"
           placeholder="Login, email, first or last name" title="Searches in login, email, first name and last name">
  </@c.filterField>
  <@c.filterField label="Role" forId="roleFilter">
    <select class="form-select" id="roleFilter" name="role">
      <option value="">All roles</option>
      <#list roles as role>
        <option value="${role.id}" <#if roleFilter == role.id>selected</#if>>${role.name}</option>
      </#list>
    </select>
  </@c.filterField>
</@c.filterBar>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Login</th>
      <th scope="col">First name</th>
      <th scope="col">Last name</th>
      <th scope="col">Last modified</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as user>
    <tr>
      <td>${user.login}</td>
      <td>${user.firstName!}</td>
      <td>${user.lastName!}</td>
      <td><#if user.common.modified??>${user.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.editLink url="/admin/users/${user.id}/edit" />
          <@c.iconForm url="/admin/users/${user.id}/login-as" iconName="log-in" title="Login as ${user.login}" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=5 />
</#list>
  </tbody>
</@c.table>

<#-- Preserve search and filter parameters in pagination -->
<#assign additionalParams = "">
<#if searchTerm?has_content>
  <#assign additionalParams = additionalParams + "&search=" + searchTerm?url>
</#if>
<#if roleFilter?has_content>
  <#assign additionalParams = additionalParams + "&role=" + roleFilter?url>
</#if>
<@c.pagination resultPage.pagination additionalParams />

</@layout.page>
