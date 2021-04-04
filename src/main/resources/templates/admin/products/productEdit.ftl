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
  <div class="form-group">
    <label>Slug
      <input type="text" name="${fields.slug.name}" value="${fields.slug.value!}" size="30"/>
    </label>
    <label>Title
      <input type="text" name="${fields.title.name}" value="${fields.title.value!}" size="100"/>
    </label>
    <label>File name (of media)
      <input type="text" name="${fields.fileName.name}" value="${fields.fileName.value!}" size="60"/>
    </label>
  </div>
  <div class="form-group">
    <button type="submit" class="btn btn-primary">Submit</button>
  </div>
</form>
</@layout.page>
