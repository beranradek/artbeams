<#import "/adminLayout.ftl" as layout>
<@layout.page>
<#if mediaFileUploadFormErrorMessage??>
  <div class="alert alert-danger" role="alert">${mediaFileUploadFormErrorMessage}</div>
</#if>

<!-- Search and Filter Form -->
<div class="card mb-3">
  <div class="card-body">
    <form method="GET" action="/admin/media" class="row g-3">
      <div class="col-md-4">
        <label for="searchInput" class="form-label">Search by filename</label>
        <input type="text" class="form-control" id="searchInput" name="search"
               placeholder="Enter filename..."
               value="${searchTerm!""}">
      </div>
      <div class="col-md-3">
        <label for="contentTypeFilter" class="form-label">Content Type</label>
        <select class="form-select" id="contentTypeFilter" name="contentType">
          <option value="">All Types</option>
          <option value="image/" <#if contentTypeFilter?? && contentTypeFilter == "image/">selected</#if>>Images</option>
          <option value="application/pdf" <#if contentTypeFilter?? && contentTypeFilter == "application/pdf">selected</#if>>PDF</option>
          <option value="application/" <#if contentTypeFilter?? && contentTypeFilter == "application/">selected</#if>>Applications</option>
        </select>
      </div>
      <div class="col-md-2">
        <label for="privateAccessFilter" class="form-label">Access</label>
        <select class="form-select" id="privateAccessFilter" name="privateAccess">
          <option value="">All</option>
          <option value="false" <#if privateAccessFilter?? && privateAccessFilter == "false">selected</#if>>Public</option>
          <option value="true" <#if privateAccessFilter?? && privateAccessFilter == "true">selected</#if>>Private</option>
        </select>
      </div>
      <div class="col-md-3 d-flex align-items-end gap-2">
        <button type="submit" class="btn btn-primary">Apply Filters</button>
        <a href="/admin/media" class="btn btn-secondary">Clear</a>
      </div>
    </form>
    <small class="text-muted mt-2 d-block">Search by filename (case-insensitive)</small>
  </div>
</div>

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

<table class="table table-sm admin-table">
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
