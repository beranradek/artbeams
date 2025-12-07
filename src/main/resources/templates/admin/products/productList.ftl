<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pag>
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

<div class="mb-3">
  <a class="btn btn-primary" href="/admin/products/${emptyId}/edit" role="button">New Product</a>

  <form action="/admin/products/sync-all-from-simpleshop" method="post" style="display: inline; margin-left: 10px;">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit" class="btn btn-info" onclick="return confirm('This will sync all products that have SimpleShop Product ID set. Continue?');">Sync All from SimpleShop</button>
  </form>

  <strong style="margin-left: 20px;">Total products:</strong> ${resultPage.pagination.totalCount!0}
</div>

<!-- Search Form -->
<div class="card mb-3">
  <div class="card-body">
    <form method="GET" action="/admin/products" class="row g-3">
      <div class="col-md-8">
        <label for="searchInput" class="form-label">Search</label>
        <input type="text" class="form-control" id="searchInput" name="search"
               placeholder="Search by title, slug, or subtitle..."
               value="${searchTerm}">
      </div>
      <div class="col-md-4 d-flex align-items-end gap-2">
        <button type="submit" class="btn btn-primary">Search</button>
        <a href="/admin/products" class="btn btn-secondary">Clear</a>
      </div>
    </form>
    <small class="text-muted mt-2 d-block">Search searches in product title, slug, and subtitle</small>
  </div>
</div>

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
<#list resultPage.records as product>
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

<#-- Preserve search parameters in pagination -->
<#assign additionalParams = "">
<#if searchTerm?has_content>
  <#assign additionalParams = additionalParams + "&search=" + searchTerm?url>
</#if>
<@pag.pagination resultPage.pagination additionalParams />

</@layout.page>
