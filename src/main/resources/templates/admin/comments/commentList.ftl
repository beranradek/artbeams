<#import "/adminLayout.ftl" as layout>
<@layout.page>

<table class="table table-sm">
  <thead>
    <tr>
      <th scope="col">Created</th>
      <th scope="col">User</th>
      <th scope="col">Email</th>
      <th scope="col">Comment</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list comments as comment>
    <tr>
        <td>${comment.common.created?string["d.M.yyyy, HH:mm"]}</td>
        <td>${comment.userName}</td>
        <td>${comment.email}</td>
        <td>${comment.comment}</td>
        <td>
            <form action="/admin/comments/${comment.id}" method="POST" onsubmit="return window.confirm('Are you sure you want to delete this comment?');">
                <input type="hidden" name="_method" value="DELETE"/>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn">Delete</button>
            </form>
        </td>
    </tr>
</#list>
  </tbody>
</table>
</@layout.page>