<#import "/adminLayout.ftl" as layout>
<@layout.page>

<a class="btn btn-primary" href="/admin/products/${emptyId}/edit" role="button">New Product</a>

<table class="table table-sm">
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Slug</th>
      <th scope="col">File name</th>
      <th scope="col">Regular price</th>
      <th scope="col">Discounted price</th>
      <th scope="col">Last modified</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list products as product>
    <tr>
        <td>${product.title!}</td>
        <td>${product.slug!}</td>
        <td>${product.fileName!}</td>
        <td>${product.priceRegular!}</td>
        <td>${product.priceDiscounted!}</td>
        <td><#if product.common.modified??>${product.common.modified?string["d.M.yyyy, HH:mm"]}</#if></td>
        <td><a href="/admin/products/${product.id}/edit">Edit</a></td>
    </tr>
</#list>
  </tbody>
</table>
</@layout.page>
