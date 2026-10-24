<#import "/adminLayout.ftl" as layout>
<@layout.page>
  <#assign fields = editForm.fields>
  <h1>Edit Course</h1>
  <#if errorMessage??>
    <div class="alert alert-danger" role="alert">${errorMessage}</div>
  </#if>
  <form action="/admin/courses/save" method="post" class="admin-form">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}" />
    <div class="mb-3">
      <label class="form-label" for="${fields.slug.elementId}">Slug</label>
      <input class="form-control" type="text" name="${fields.slug.name}" id="${fields.slug.elementId}" value="${fields.slug.value!}" />
    </div>
    <div class="mb-3">
      <label class="form-label" for="${fields.title.elementId}">Title</label>
      <input class="form-control" type="text" name="${fields.title.name}" id="${fields.title.elementId}" value="${fields.title.value!}" />
    </div>
    <div class="mb-3">
      <label class="form-label" for="${fields.productIds.elementId}">Assign Products (comma separated ids)</label>
      <input class="form-control" type="text" name="${fields.productIds.name}" id="${fields.productIds.elementId}" value="${fields.productIds.value!}" />
    </div>
    <button type="submit" class="btn btn-primary">Save</button>
    <a href="/admin/courses" class="btn btn-outline-secondary">Cancel</a>
  </form>
</@layout.page>
