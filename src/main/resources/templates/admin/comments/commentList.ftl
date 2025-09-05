<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pagination>
<@layout.page noUp=true>

<table class="table table-sm admin-table">
  <thead>
    <tr>
      <th scope="col">Created</th>
      <th scope="col">User</th>
      <th scope="col">Email</th>
      <th scope="col">Comment</th>
      <th scope="col">State</th>
      <th scope="col">Actions</th>
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
                <select name="state" onchange="this.form.submit()">
                    <#list commentStates as state>
                        <option value="${state}"<#if state == comment.state> selected</#if>>${state}</option>
                    </#list>
                </select>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </td>
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

<@pagination.pagination pagination=resultPage.pagination />

</@layout.page>
