<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pagination>
<@layout.page noUp=true>

<div class="card mb-3">
  <div class="card-body">
    <h5 class="card-title">Search Comments</h5>
    <form action="/admin/comments" method="GET" class="row g-3">
      <div class="col-md-6">
        <label for="search" class="form-label">Search</label>
        <input type="text" class="form-control" id="search" name="search" 
               placeholder="Search by author, email, or content..." 
               value="${searchTerm}">
        <small class="form-text text-muted">Search in author name, email, or comment content</small>
      </div>
      <div class="col-md-3">
        <label for="state" class="form-label">State</label>
        <select class="form-select" id="state" name="state">
          <option value="">All States</option>
          <#list commentStates as state>
            <option value="${state}"<#if state == selectedState> selected</#if>>${state}</option>
          </#list>
        </select>
      </div>
      <div class="col-md-3 d-flex align-items-end">
        <button type="submit" class="btn btn-primary me-2">Search</button>
        <a href="/admin/comments" class="btn btn-secondary">Clear</a>
      </div>
    </form>
  </div>
</div>

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

<#assign additionalParams><#if searchTerm?has_content>&search=${searchTerm?url}</#if><#if selectedState?has_content>&state=${selectedState?url}</#if></#assign>
<@pagination.pagination pagination=resultPage.pagination additionalParams=additionalParams />

</@layout.page>
