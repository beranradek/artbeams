<#import "/adminLayout.ftl" as layout>
<@layout.page>

<#if RequestParameters?? && RequestParameters.syncSuccess??>
  <div class="alert alert-success" role="alert">
    Bulk synchronization completed!
    <#if RequestParameters.total??>
      <br>Total products: ${RequestParameters.total}
    </#if>
    <#if RequestParameters.success??>
      <br>Successful: ${RequestParameters.success}
    </#if>
    <#if RequestParameters.updated??>
      <br>Updated: ${RequestParameters.updated}
    </#if>
  </div>
</#if>
<#if RequestParameters?? && RequestParameters.syncError??>
  <div class="alert alert-danger" role="alert">
    Bulk synchronization failed!
    <#if RequestParameters.message??><br>${RequestParameters.message}</#if>
  </div>
</#if>

<a class="btn btn-primary" href="/admin/products/${emptyId}/edit" role="button">New Product</a>

<form action="/admin/products/sync-all-from-simpleshop" method="post" style="display: inline; margin-left: 10px;">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <button type="submit" class="btn btn-info" onclick="return confirm('This will sync all products that have SimpleShop Product ID set. Continue?');">Sync All from SimpleShop</button>
</form>

<table class="table table-sm admin-table">
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
