<#import "/adminLayout.ftl" as layout>
<@layout.page>
  <h1>Edit Module</h1>
  <form action="save" method="post">
    <input type="hidden" name="module.id" value="${editForm.data.id!}" />
    <div class="mb-3">
      <label class="form-label">Title</label>
      <input class="form-control" name="module.title" value="${editForm.data.title!}" />
    </div>
    <div class="mb-3">
      <label class="form-label">Short description</label>
      <textarea class="form-control" name="module.shortDescription">${editForm.data.shortDescription!}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">Save</button>
  </form>
</@layout.page>
