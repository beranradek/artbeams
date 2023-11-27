<#import "/adminLayout.ftl" as layout>
<@layout.page>

<a class="btn btn-primary" href="/admin/categories/${emptyId}/edit" role="button">New Category</a>

<table class="table table-sm">
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Slug</th>
      <th scope="col">Last modified</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list categories as category>
    <tr>
        <td>${category.title!}</td>
        <td>${category.slug!}</td>
        <td><#if category.common.modified??>${category.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
        <td><a href="/admin/categories/${category.id}/edit">Edit</a></td>
    </tr>
</#list>
  </tbody>
</table>
</@layout.page>
