<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pagination>
<@layout.page noUp=true>

<a class="btn btn-primary" href="/admin/config/new/edit" role="button">New Config Entry</a>

<table class="table table-sm admin-table">
  <thead>
    <tr>
      <th scope="col">Entry Key</th>
      <th scope="col">Entry Value</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as config>
    <tr>
        <td>${config.entryKey!}</td>
        <td>${config.entryValue!}</td>
        <td>
            <a href="/admin/config/${config.entryKey}/edit">Edit</a>
            |
            <form action="/admin/config/${config.entryKey}" method="POST" style="display:inline;" onsubmit="return window.confirm('Are you sure you want to delete this config entry?');">
                <input type="hidden" name="_method" value="DELETE"/>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn-link" style="border:none;background:none;padding:0;color:#007bff;cursor:pointer;text-decoration:underline;">Delete</button>
            </form>
        </td>
    </tr>
</#list>
  </tbody>
</table>

<@pagination.pagination pagination=resultPage.pagination />

</@layout.page>
