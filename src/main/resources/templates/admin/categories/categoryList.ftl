<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pag>
<@layout.page>

<div class="mb-3">
  <a class="btn btn-primary" href="/admin/categories/${emptyId}/edit" role="button">New Category</a>
  <strong style="margin-left: 20px;">Total categories:</strong> ${resultPage.pagination.totalCount!0}
</div>

<table class="table table-sm admin-table">
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Slug</th>
      <th scope="col">Last modified</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as category>
    <tr>
        <td>${category.title!}</td>
        <td>${category.slug!}</td>
        <td><#if category.common.modified??>${category.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
        <td><a href="/admin/categories/${category.id}/edit">Edit</a></td>
    </tr>
</#list>
  </tbody>
</table>

<@pag.pagination resultPage.pagination />

</@layout.page>
