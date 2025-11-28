<#import "/adminLayout.ftl" as layout>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>

<#assign fields = editForm.fields>
<h1><#if fields.originalKey.value?has_content>Edit Localisation<#else>New Localisation</#if></h1>

<form action="/admin/localisations/save" method="post" class="form-horizontal">
  <input type="hidden" name="${fields.originalKey.name}" value="${fields.originalKey.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div class="form-group row">
    <label for="${fields.entryKey.elementId}" class="col-sm-2 col-form-label">Entry Key</label>
    <div class="col-sm-6">
      <input type="text" name="${fields.entryKey.name}" value="${fields.entryKey.value!}" id="${fields.entryKey.elementId}" class="form-control" required/>
      <#if fields.entryKey.validationMessages?has_content>
        <div class="text-danger">${fields.entryKey.validationMessages?join(", ")}</div>
      </#if>
    </div>
  </div>

  <div class="form-group row">
    <label for="${fields.entryValue.elementId}" class="col-sm-2 col-form-label">Entry Value</label>
    <div class="col-sm-6">
      <textarea name="${fields.entryValue.name}" id="${fields.entryValue.elementId}" rows="3" class="form-control" required>${fields.entryValue.value!}</textarea>
      <#if fields.entryValue.validationMessages?has_content>
        <div class="text-danger">${fields.entryValue.validationMessages?join(", ")}</div>
      </#if>
    </div>
  </div>

  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-6">
      <button type="submit" class="btn btn-primary">Submit</button>
      <a href="/admin/localisations" class="btn btn-secondary">Cancel</a>
    </div>
  </div>
</form>
</@layout.page>
