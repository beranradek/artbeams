<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pag>
<@layout.page>

<div class="d-flex justify-content-between align-items-center mb-3">
  <a class="btn btn-primary" href="/admin/users/${emptyId}/edit" role="button">New User</a>
  <div>
    <strong>Total users:</strong> ${resultPage.pagination.totalCount!0}
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

<@pag.pagination resultPage.pagination />

</@layout.page>
