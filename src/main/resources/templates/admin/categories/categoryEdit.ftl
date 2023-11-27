<#import "/adminLayout.ftl" as layout>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>

<#assign fields = editForm.fields>
<h1>${fields.title.value!}</h1>

<form action="/admin/categories/save" method="post" class="form-horizontal">
  <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <div class="form-group row">
    <label for="${fields.slug.elementId}" class="col-sm-2 col-form-label">Slug</label>
    <div class="col-sm-3"><input type="text" name="${fields.slug.name}" value="${fields.slug.value!}" id="${fields.slug.elementId}" size="30" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.title.elementId}" class="col-sm-2 col-form-label">Title</label>
    <div class="col-sm-3"><input type="text" name="${fields.title.name}" value="${fields.title.value!}" id="${fields.title.elementId}" size="100" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.description.elementId}" class="col-sm-2 col-form-label">Description</label>
    <div class="col-sm-6"><textarea name="${fields.description.name}" id="${fields.description.elementId}" rows="2" cols="160" class="form-control" style="max-width:100%">${fields.description.value!}</textarea></div>
  </div>
  <div class="form-group row">
    <label for="${fields.validFrom.elementId}" class="col-sm-2 col-form-label">Valid from</label>
    <div class="col-sm-2"><input type="text" name="${fields.validFrom.name}" value="${fields.validFrom.value}" id="${fields.validFrom.elementId}" class="form-control" /></div>
  </div>
  <div class="form-group row">
    <label for="${fields.validTo.elementId}" class="col-sm-2 col-form-label">Valid to</label>
    <div class="col-sm-2"><input type="text" name="${fields.validTo.name}" value="${fields.validTo.value!}" id="${fields.validTo.elementId}" class="form-control" /></div>
  </div>
  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-3"><button type="submit" class="btn btn-primary">Submit</button></div>
  </div>
</form>
</@layout.page>
