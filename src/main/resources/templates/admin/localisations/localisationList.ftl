<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pagination>
<@layout.page noUp=true>

<form method="GET" action="/admin/localisations" class="mb-3">
  <div class="row">
    <div class="col-md-6">
      <div class="input-group">
        <input type="text" class="form-control" name="search" placeholder="Search in keys and values..." value="${search!}" />
        <div class="input-group-append">
          <button class="btn btn-primary" type="submit">Search</button>
          <#if search?? && search?has_content>
            <a href="/admin/localisations" class="btn btn-secondary">Clear</a>
          </#if>
        </div>
      </div>
    </div>
  </div>
</form>

<a class="btn btn-primary" href="/admin/localisations/new/edit" role="button">New Localisation</a>

<table class="table table-sm admin-table">
  <thead>
    <tr>
      <th scope="col">Entry Key</th>
      <th scope="col">Entry Value</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as localisation>
    <tr>
        <td>${localisation.entryKey!}</td>
        <td>${localisation.entryValue!}</td>
        <td>
            <a href="/admin/localisations/${localisation.entryKey}/edit">Edit</a>
            |
            <form action="/admin/localisations/${localisation.entryKey}" method="POST" style="display:inline;" onsubmit="return window.confirm('Are you sure you want to delete this localisation?');">
                <input type="hidden" name="_method" value="DELETE"/>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn-link" style="border:none;background:none;padding:0;color:#007bff;cursor:pointer;text-decoration:underline;">Delete</button>
            </form>
        </td>
    </tr>
</#list>
  </tbody>
</table>

<#assign searchParam = (search?? && search?has_content)?then("&search=" + search?url, "") />
<@pagination.pagination pagination=resultPage.pagination additionalParams=searchParam />

</@layout.page>
