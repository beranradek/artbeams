<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<style nonce="${_cspNonce}">
@media (max-width: 960px) {
  .column-right {
    min-width: 900px;
  }
}

.illustration-image {
  opacity: 0.9;
  border-radius: 20px;
  margin-bottom: 20px;
}
</style>

<div class="container">
  <div class="row">
    <div class="col">
      <#list userProducts as userProduct>
          <div class="card-holder">
            <div class="card-content">
              <a href="/clenska-sekce/${userProduct.slug}">
                  <div class="card-image"<#if userProduct.listingImage??> style="background-image: url('${userProduct.listingImage}');"</#if>>
                  </div>
                  <div class="card-title-desc">
                    <h3 class="card-title">${userProduct.title}</h3>
                    <div class="card-hline-holder">
                      <hr class="card-hline">
                    </div>
                    <div class="card-desc-holder">
                      <p class="card-desc"><#if userProduct.subtitle??>${userProduct.subtitle}</#if></p>
                      <svg class="" focusable="false" aria-hidden="true" viewBox="0 0 24 24" data-testid="ChevronRightIcon" stroke="#fff" width="20px" height="20px"><path d="M9 18L15 12L9 6" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path></svg>
                    </div>
                  </div>
                </a>
            </div>
          </div>
      <#else>
          <div class="alert alert-warning" role="alert">
              Zatím jste si od nás nepořídili žádný produkt. To nevadí, můžete si na stránkách nějaký vybrat.
          </div>
      </#list>
    </div>
    <div class="col column-right">
        <img src="/static/images/night-tree.webp" class="img-fluid illustration-image" alt="">
    </div>
  </div>
</div>

</@layout.page>
