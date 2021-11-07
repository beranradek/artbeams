<#import "/layouts/adminLayout.ftl" as layout>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>

<#assign fields = editForm.fields>
<h1>${fields.title.value!}</h1>

<form action="/admin/products/save" method="post" class="form-horizontal">
  <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <div class="form-group row">
    <label for="${fields.slug.elementId}" class="col-sm-2 col-form-label">Slug</label>
    <div class="col-sm-10"><input type="text" name="${fields.slug.name}" value="${fields.slug.value!}" id="${fields.slug.elementId}" size="80"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.title.elementId}" class="col-sm-2 col-form-label">Title</label>
    <div class="col-sm-10"><input type="text" name="${fields.title.name}" value="${fields.title.value!}" id="${fields.title.elementId}" size="80"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.fileName.elementId}" class="col-sm-2 col-form-label">File name (of media)</label>
    <div class="col-sm-10"><input type="text" name="${fields.fileName.name}" value="${fields.fileName.value!}" id="${fields.fileName.elementId}" size="80"/></div>
  </div>
  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-10"><button type="submit" class="btn btn-primary">Submit</button></div>
  </div>
</form>
</@layout.page>
