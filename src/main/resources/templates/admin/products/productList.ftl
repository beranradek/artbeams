<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
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

<@c.pageHeader title="Products" count="${resultPage.pagination.totalCount!0}" countLabel="Total products">
  <form action="/admin/products/sync-all-from-simpleshop" method="post" class="admin-icon-form"
        data-confirm="This will sync all products that have SimpleShop Product ID set. Continue?">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit" class="btn btn-outline-secondary btn-sm"><@c.icon "refresh"/> Sync all from SimpleShop</button>
  </form>
  <@c.newButton url="/admin/products/${emptyId}/edit" label="New Product" />
</@c.pageHeader>

<@c.filterBar action="/admin/products" clearUrl="/admin/products" submitLabel="Search">
  <@c.filterField label="Search" forId="searchInput" grow=true>
    <input type="text" class="form-control" id="searchInput" name="search" value="${searchTerm}"
           placeholder="Title, slug or subtitle" title="Searches in product title, slug and subtitle">
  </@c.filterField>
</@c.filterBar>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Slug</th>
      <th scope="col">File name</th>
      <th scope="col">Regular price</th>
      <th scope="col">Discounted price</th>
      <th scope="col">Last modified</th>
      <th scope="col" class="admin-col-actions">Actions</th>
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
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.editLink url="/admin/products/${product.id}/edit" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=7 />
</#list>
  </tbody>
</@c.table>

<#-- Preserve search parameters in pagination -->
<#assign additionalParams = "">
<#if searchTerm?has_content>
  <#assign additionalParams = additionalParams + "&search=" + searchTerm?url>
</#if>
<@c.pagination resultPage.pagination additionalParams />

</@layout.page>
