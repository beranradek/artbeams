<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Comments" count="${resultPage.pagination.totalCount!0}" countLabel="Total comments" />

<@c.filterBar action="/admin/comments" clearUrl="/admin/comments" submitLabel="Search">
  <@c.filterField label="Search" forId="search" grow=true>
    <input type="text" class="form-control" id="search" name="search" value="${searchTerm}"
           placeholder="Author, email or content" title="Searches in author name, email and comment content">
  </@c.filterField>
  <@c.filterField label="State" forId="state">
    <select class="form-select" id="state" name="state">
      <option value="">All states</option>
      <#list commentStates as state>
        <option value="${state}"<#if state == selectedState> selected</#if>>${state}</option>
      </#list>
    </select>
  </@c.filterField>
</@c.filterBar>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Created</th>
      <th scope="col">User</th>
      <th scope="col">Email</th>
      <th scope="col">Comment</th>
      <th scope="col">State</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as comment>
    <tr>
      <td>${comment.common.created?string["d.M.yyyy, HH:mm"]}</td>
      <td>${comment.userName}</td>
      <td>${comment.email}</td>
      <td>${comment.comment}</td>
      <td>
        <form action="/admin/comments/${comment.id}/state" method="POST">
          <select name="state" class="form-select form-select-sm admin-state-select">
            <#list commentStates as state>
              <option value="${state}"<#if state == comment.state> selected</#if>>${state}</option>
            </#list>
          </select>
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
      </td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.deleteForm url="/admin/comments/${comment.id}" method="DELETE" confirm="Are you sure you want to delete this comment?" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=6 />
</#list>
  </tbody>
</@c.table>

<#assign additionalParams><#if searchTerm?has_content>&search=${searchTerm?url}</#if><#if selectedState?has_content>&state=${selectedState?url}</#if></#assign>
<@c.pagination pagination=resultPage.pagination additionalParams=additionalParams />

</@layout.page>
