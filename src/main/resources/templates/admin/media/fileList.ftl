<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>
<#if mediaFileUploadFormErrorMessage??>
  <div class="alert alert-danger" role="alert">${mediaFileUploadFormErrorMessage}</div>
</#if>

<@c.pageHeader title="Media" count="${files?size}" countLabel="Files on this page" />

<@c.filterBar action="/admin/media" clearUrl="/admin/media">
  <@c.filterField label="Filename" forId="searchInput" grow=true>
    <input type="text" class="form-control" id="searchInput" name="search" value="${searchTerm!""}"
           placeholder="Enter filename" title="Case-insensitive search by filename">
  </@c.filterField>
  <@c.filterField label="Content type" forId="contentTypeFilter">
    <select class="form-select" id="contentTypeFilter" name="contentType">
      <option value="">All types</option>
      <option value="image/" <#if contentTypeFilter?? && contentTypeFilter == "image/">selected</#if>>Images</option>
      <option value="application/pdf" <#if contentTypeFilter?? && contentTypeFilter == "application/pdf">selected</#if>>PDF</option>
      <option value="application/" <#if contentTypeFilter?? && contentTypeFilter == "application/">selected</#if>>Applications</option>
    </select>
  </@c.filterField>
  <@c.filterField label="Access" forId="privateAccessFilter">
    <select class="form-select" id="privateAccessFilter" name="privateAccess">
      <option value="">All</option>
      <option value="false" <#if privateAccessFilter?? && privateAccessFilter == "false">selected</#if>>Public</option>
      <option value="true" <#if privateAccessFilter?? && privateAccessFilter == "true">selected</#if>>Private</option>
    </select>
  </@c.filterField>
</@c.filterBar>

<form action="/admin/media/upload" method="POST" enctype="multipart/form-data" class="admin-upload">
  <input type="file" class="form-control admin-upload-file" name="${mediaFileUploadForm.fields.file.name}" aria-label="File to upload" />
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <label>Convert to
    <select class="form-select" name="${mediaFileUploadForm.fields.format.name}" id="${mediaFileUploadForm.fields.format.elementId}">
      <option value=""></option>
      <#list fileFormats as fileFormat>
        <option value="${fileFormat}">${fileFormat}</option>
      </#list>
    </select>
  </label>
  <label>Width
    <input type="text" class="form-control admin-upload-width" name="${mediaFileUploadForm.fields.width.name}" value="${mediaFileUploadForm.fields.width.value!}" id="${mediaFileUploadForm.fields.width.elementId}" placeholder="px"/>
  </label>
  <label class="form-check form-switch m-0 p-0 gap-2">
    <input type="checkbox" class="form-check-input m-0" role="switch" name="${mediaFileUploadForm.fields.privateAccess.name}" /> Private
  </label>
  <button type="submit" class="btn btn-primary btn-sm ms-auto">Upload</button>
</form>

<@c.table>
  <thead>
    <tr>
      <th scope="col">File name</th>
      <th scope="col">Content type</th>
      <th scope="col">Size [B]</th>
      <th scope="col">Access</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list files as file>
    <tr>
      <td><a href="/media/${file.filename}?size=${file.width}" target="_blank" rel="noopener">${file.filename}</a></td>
      <td>${file.contentType!}</td>
      <td>${file.size!}</td>
      <td><#if file.privateAccess><span class="badge text-bg-warning">Private</span><#else><span class="badge text-bg-light border">Public</span></#if></td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.deleteForm url="/admin/media/${file.filename}/delete?size=${file.width}" confirm="Delete ${file.filename}?" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=5 />
</#list>
  </tbody>
</@c.table>
</@layout.page>
