<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pag>
<@layout.page>

<div class="d-flex justify-content-between align-items-center mb-3">
  <a class="btn btn-primary" href="/admin/users/${emptyId}/edit" role="button">New User</a>
  <div>
    <strong>Total users:</strong> ${resultPage.pagination.totalCount!0}
  </div>
</div>

<!-- Search and Filter Form -->
<div class="card mb-3">
  <div class="card-body">
    <form method="GET" action="/admin/users" class="row g-3">
      <div class="col-md-5">
        <label for="searchInput" class="form-label">Search</label>
        <input type="text" class="form-control" id="searchInput" name="search"
               placeholder="Search by login, email, first name, or last name..."
               value="${searchTerm}">
      </div>
      <div class="col-md-3">
        <label for="roleFilter" class="form-label">Role</label>
        <select class="form-select" id="roleFilter" name="role">
          <option value="">All Roles</option>
          <#list roles as role>
            <option value="${role.id}" <#if roleFilter == role.id>selected</#if>>${role.name}</option>
          </#list>
        </select>
      </div>
      <div class="col-md-4 d-flex align-items-end gap-2">
        <button type="submit" class="btn btn-primary">Apply Filters</button>
        <a href="/admin/users" class="btn btn-secondary">Clear</a>
      </div>
    </form>
    <small class="text-muted mt-2 d-block">Search searches in login, email, first name, and last name fields</small>
  </div>
</div>

<table class="table table-sm admin-table">
  <thead>
    <tr>
      <th scope="col">Login</th>
      <th scope="col">First name</th>
      <th scope="col">Last name</th>
      <th scope="col">Last modified</th>
      <th scope="col">Edit</th>
      <th scope="col">Login as</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as user>
    <tr>
        <td>${user.login}</td>
        <td>${user.firstName!}</td>
        <td>${user.lastName!}</td>
        <td><#if user.common.modified??>${user.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
        <td>
            <a href="/admin/users/${user.id}/edit">Edit</a>
        </td>
        <td>
            <form action="/admin/users/${user.id}/login-as" method="POST">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn">Login as</button>
            </form>
        </td>
    </tr>
</#list>
  </tbody>
</table>

<#-- Preserve search and filter parameters in pagination -->
<#assign additionalParams = "">
<#if searchTerm?has_content>
  <#assign additionalParams = additionalParams + "&search=" + searchTerm?url>
</#if>
<#if roleFilter?has_content>
  <#assign additionalParams = additionalParams + "&role=" + roleFilter?url>
</#if>
<@pag.pagination resultPage.pagination additionalParams />

</@layout.page>
