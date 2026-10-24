<#import "/adminLayout.ftl" as layout>
<@layout.page>
  <#assign fields = editForm.fields>
  <h1>Edit Module</h1>
  <#if errorMessage??>
    <div class="alert alert-danger" role="alert">${errorMessage}</div>
  </#if>
  <form action="save" method="post" class="admin-form">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}" />
    <div class="mb-3">
      <label class="form-label" for="${fields.title.elementId}">Title</label>
      <input class="form-control" type="text" name="${fields.title.name}" id="${fields.title.elementId}" value="${fields.title.value!}" />
    </div>
    <div class="mb-3">
      <label class="form-label" for="${fields.shortDescription.elementId}">Short description</label>
      <textarea class="form-control" name="${fields.shortDescription.name}" id="${fields.shortDescription.elementId}">${fields.shortDescription.value!}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">Save</button>
  </form>
</@layout.page>
