<#import "/layouts/adminLayout.ftl" as layout>
<@layout.page>
<#if mediaFileUploadFormErrorMessage??>
  <div class="alert alert-danger" role="alert">${mediaFileUploadFormErrorMessage}</div>
</#if>

<form action="/admin/media/upload" method="POST" enctype="multipart/form-data">
    <input type="file" name="${mediaFileUploadForm.fields.file.name}" />
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    &nbsp;<label>Convert to:
        <select name="${mediaFileUploadForm.fields.format.name}" id="${mediaFileUploadForm.fields.format.elementId}">
          <option value=""></option>
          <#list fileFormats as fileFormat>
            <option value="${fileFormat}">${fileFormat}</option>
          </#list>
        </select>
    </label>
    &nbsp;<label>to width:
        <input type="text" name="${mediaFileUploadForm.fields.width.name}" value="${mediaFileUploadForm.fields.width.value!}" id="${mediaFileUploadForm.fields.width.elementId}" size="5"/>
    </label>
    &nbsp;<label>Private access
      <input type="checkbox" name="${mediaFileUploadForm.fields.privateAccess.name}" />
    </label>
    <button type="submit" class="btn btn-primary">Upload new file</button>
</form>

<table class="table table-sm">
  <thead>
    <tr>
      <th scope="col">File name</th>
      <th scope="col">Content type</th>
      <th scope="col">Size [B]</th>
      <th scope="col">Private access</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list files as file>
    <tr>
        <td><a href="/media/${file.filename}?size=${file.width}" target="_blank">${file.filename}</a></td>
        <td>${file.contentType!}</td>
        <td>${file.size!}</td>
        <td>${file.privateAccess?c}</td>
        <td>
            <form action="/admin/media/${file.filename}/delete?size=${file.width}" method="POST">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn">Delete</button>
            </form>
        </td>
    </tr>
</#list>
  </tbody>
</table>
</@layout.page>
