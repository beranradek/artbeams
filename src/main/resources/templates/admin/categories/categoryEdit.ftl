<#import "/layouts/adminLayout.ftl" as layout>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>

<#assign fields = editForm.fields>
<h1>${fields.title.value!}</h1>

<form action="/admin/categories/save" method="post" class="form-horizontal">
  <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <div class="form-group">
    <label>Slug
      <input type="text" name="${fields.slug.name}" value="${fields.slug.value!}" size="30"/>
    </label>
    <label>Title
      <input type="text" name="${fields.title.name}" value="${fields.title.value!}" size="100"/>
    </label>
  </div>
  <div class="form-group">
    <label for="${fields.description.elementId}">Description</label>
    <textarea name="${fields.description.name}" id="${fields.description.elementId}" rows="2" cols="160">${fields.description.value!}</textarea>
  </div>
  <div class="form-group">
    <label>Valid from
      <input type="text" name="${fields.validFrom.name}" value="${fields.validFrom.value}"/>
    </label>
  </div>
  <div class="form-group">
    <label>Valid to
      <input type="text" name="${fields.validTo.name}" value="${fields.validTo.value}"/>
    </label>
  </div>
  <div class="form-group">
    <button type="submit" class="btn btn-primary">Submit</button>
  </div>
</form>
</@layout.page>
