<#import "/adminLayout.ftl" as layout>
<@layout.page>
  <h1>Edit Course</h1>
  <form action="/admin/courses/save" method="post">
    <input type="hidden" name="course.id" value="${editForm.data.id!}" />
    <div class="mb-3">
      <label class="form-label">Slug</label>
      <input class="form-control" name="course.slug" value="${editForm.data.slug!}" />
    </div>
    <div class="mb-3">
      <label class="form-label">Title</label>
      <input class="form-control" name="course.title" value="${editForm.data.title!}" />
    </div>
    <div class="mb-3">
      <label class="form-label">Assign Products (comma separated ids)</label>
      <input class="form-control" name="course.productIds" value="" />
    </div>
    <button type="submit" class="btn btn-primary">Save</button>
    <a href="/admin/courses" class="btn btn-secondary">Cancel</a>
  </form>
</@layout.page>
