<#import "/adminLayout.ftl" as layout>
<@layout.page>

<a class="btn btn-primary" href="/admin/users/${emptyId}/edit" role="button">New User</a>

<table class="table table-sm">
  <thead>
    <tr>
      <th scope="col">Login</th>
      <th scope="col">First name</th>
      <th scope="col">Last name</th>
      <th scope="col">Last modified</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list users as user>
    <tr>
        <td>${user.login}</td>
        <td>${user.firstName!}</td>
        <td>${user.lastName!}</td>
        <td><#if user.common.modified??>${user.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
        <td><a href="/admin/users/${user.id}/edit">Edit</a></td>
    </tr>
</#list>
  </tbody>
</table>
</@layout.page>
