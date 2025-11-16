<#import "/adminLayout.ftl" as layout>
<@layout.page>
<#if errorMessage??>
  <div class="alert alert-danger" role="alert">${errorMessage}</div>
</#if>
<#if RequestParameters.syncSuccess??>
  <div class="alert alert-success" role="alert">
    SimpleShop synchronization successful!
    <#if RequestParameters.message??><br>${RequestParameters.message}</#if>
    <#if RequestParameters.fields?? && RequestParameters.fields != "">
      <br>Updated fields: ${RequestParameters.fields}
    </#if>
  </div>
</#if>
<#if RequestParameters.syncError??>
  <div class="alert alert-danger" role="alert">
    SimpleShop synchronization failed!
    <#if RequestParameters.message??><br>${RequestParameters.message}</#if>
  </div>
</#if>

<#assign fields = editForm.fields>
<h1>${fields.title.value!}</h1>

<form action="/admin/products/save" method="post" class="form-horizontal">
  <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <div class="form-group row">
    <label for="${fields.slug.elementId}" class="col-sm-2 col-form-label">Slug</label>
    <div class="col-sm-3"><input type="text" name="${fields.slug.name}" value="${fields.slug.value!}" id="${fields.slug.elementId}" size="80" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.title.elementId}" class="col-sm-2 col-form-label">Title</label>
    <div class="col-sm-3"><input type="text" name="${fields.title.name}" value="${fields.title.value!}" id="${fields.title.elementId}" size="80" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.subtitle.elementId}" class="col-sm-2 col-form-label">Subtitle</label>
    <div class="col-sm-3"><input type="text" name="${fields.subtitle.name}" value="${fields.subtitle.value!}" id="${fields.subtitle.elementId}" size="80" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.fileName.elementId}" class="col-sm-2 col-form-label">File name (of media)</label>
    <div class="col-sm-3"><input type="text" name="${fields.fileName.name}" value="${fields.fileName.value!}" id="${fields.fileName.elementId}" size="80" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.listingImage.elementId}" class="col-sm-2 col-form-label">Listing image</label>
    <div class="col-sm-3"><input type="text" name="${fields.listingImage.name}" value="${fields.listingImage.value!}" id="${fields.listingImage.elementId}" size="80" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.image.elementId}" class="col-sm-2 col-form-label">Image</label>
    <div class="col-sm-3"><input type="text" name="${fields.image.name}" value="${fields.image.value!}" id="${fields.image.elementId}" size="80" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.confirmationMailingGroupId.elementId}" class="col-sm-2 col-form-label">Confirmation mailing group id</label>
    <div class="col-sm-3"><input type="text" name="${fields.confirmationMailingGroupId.name}" value="${fields.confirmationMailingGroupId.value!}" id="${fields.confirmationMailingGroupId.elementId}" size="80" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.mailingGroupId.elementId}" class="col-sm-2 col-form-label">Mailing group id</label>
    <div class="col-sm-3"><input type="text" name="${fields.mailingGroupId.name}" value="${fields.mailingGroupId.value!}" id="${fields.mailingGroupId.elementId}" size="80" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.priceRegularAmount.elementId}" class="col-sm-2 col-form-label">Regular price</label>
    <div class="col-sm-3"><input type="text" name="${fields.priceRegularAmount.name}" value="${fields.priceRegularAmount.value!}" id="${fields.priceRegularAmount.elementId}" size="10" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.priceDiscountedAmount.elementId}" class="col-sm-2 col-form-label">Discounted price</label>
    <div class="col-sm-3"><input type="text" name="${fields.priceDiscountedAmount.name}" value="${fields.priceDiscountedAmount.value!}" id="${fields.priceDiscountedAmount.elementId}" size="10" class="form-control"/></div>
  </div>
  <div class="form-group row">
    <label for="${fields.simpleShopProductId.elementId}" class="col-sm-2 col-form-label">SimpleShop Product ID</label>
    <div class="col-sm-3">
      <input type="text" name="${fields.simpleShopProductId.name}" value="${fields.simpleShopProductId.value!}" id="${fields.simpleShopProductId.elementId}" size="20" class="form-control" placeholder="Optional: For sync from SimpleShop"/>
      <small class="form-text text-muted">Enter SimpleShop product ID to enable synchronization</small>
    </div>
  </div>
  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-3"><button type="submit" class="btn btn-primary">Submit</button></div>
  </div>
</form>

<#if fields.id.value?? && fields.id.value != "" && fields.simpleShopProductId.value?? && fields.simpleShopProductId.value != "">
<hr class="my-4">
<h3>SimpleShop Synchronization</h3>
<p>This product is linked to SimpleShop product ID: <strong>${fields.simpleShopProductId.value}</strong></p>
<form action="/admin/products/${fields.id.value}/sync-from-simpleshop" method="post" style="display: inline;">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <button type="submit" class="btn btn-info">Sync from SimpleShop</button>
</form>
<p class="text-muted mt-2">This will update the product name and price from SimpleShop.</p>
</#if>
</@layout.page>
