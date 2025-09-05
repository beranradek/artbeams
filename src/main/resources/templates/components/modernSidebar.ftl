<#if subscriptionFormMapping??>
<#import "/mailing/subscriptionForm.ftl" as subscriptionForm>
</#if>
<#import "/components/responsiveImage.ftl" as img>

<#macro modernSidebar>
<!-- Sidebar -->
<div class="col-lg-4">
  <!-- About Section -->
  <div class="card sidebar border-0 shadow-sm">
    <div class="card-body">
      <h4>O stránkách</h4>
      <p><em>${xlat['website.title']}</em> ${xlat['website.purpose']}</p>
    </div>
  </div>
  
  <!-- Latest Posts -->
  <#if latestArticles??>
  <div class="card sidebar border-0 shadow-sm">
    <div class="card-body">
      <h4>Nejnovější příspěvky</h4>
      <ul class="list-unstyled">
        <#list latestArticles as article>
        <li>
          <a href="/${article.slug}" class="text-decoration-none d-flex align-items-center">
            <div class="flex-shrink-0">
              <@img.thumbnailImage imageName=article.image alt=article.title size="50" />
            </div>
            <div class="flex-grow-1 ms-3">
              <p class="mb-0">${article.title}</p>
              <small class="text-muted">${article.validFrom?string["d. MMMM yyyy"]}</small>
            </div>
          </a>
        </li>
        </#list>
      </ul>
    </div>
  </div>
  </#if>
  
  <!-- Categories -->
  <#if articleCategories??>
  <div class="card sidebar border-0 shadow-sm">
    <div class="card-body">
      <h4>Rubriky</h4>
      <div class="d-flex flex-wrap gap-2">
        <#list articleCategories as category>
        <a href="${xlat['categories.url.base']}/${category.slug}" class="btn btn-sm btn-outline-secondary mb-2">${category.title}</a>
        </#list>
      </div>
    </div>
  </div>
  </#if>
  
  <!-- Social Networks -->
  <div class="card sidebar border-0 shadow-sm">
    <div class="card-body">
      <h4>Sociální sítě</h4>
      <div class="d-flex">
        <#if xlat['fb.page.url']??>
          <a href="${xlat['fb.page.url']}?ref=embed_page">
            <img src="/media/facebook_page.webp"
                 class="social-page-image responsive-image-width-height"
                 alt="Facebook stránka"
                 width="381"
                 height="69"
                 loading="lazy"
            />
          </a>
          <#-- Facebook Icon:
          <a href="${xlat['fb.page.url']}" class="social-icon" data-bs-toggle="tooltip" data-bs-placement="top" title="Facebook">
            <i class="bi bi-facebook"></i>
          </a>
          -->
        </#if>
        <#if xlat['twitter.url']??>
          <a href="${xlat['twitter.url']}" class="social-icon" data-bs-toggle="tooltip" data-bs-placement="top" title="Twitter">
            <i class="bi bi-twitter"></i>
          </a>
        </#if>
        <#if xlat['instagram.url']??>
          <a href="${xlat['instagram.url']}" class="social-icon" data-bs-toggle="tooltip" data-bs-placement="top" title="Instagram">
            <i class="bi bi-instagram"></i>
          </a>
        </#if>
        <#if xlat['whatsapp.url']??>
          <a href="${xlat['whatsapp.url']}" class="social-icon" data-bs-toggle="tooltip" data-bs-placement="top" title="WhatsApp">
            <i class="bi bi-whatsapp"></i>
          </a>
        </#if>
      </div>
    </div>
  </div>
  
  <!-- E-book subscription form -->
  <#if subscriptionFormMapping??>
    <#if xlat['mailer-lite.form.title']??>
    <div class="newsletter-section">
      <h4>Stáhněte si zdarma e-book</h4>
      <@subscriptionForm.subscriptionForm productSlug=xlat['offer1.productSlug'] subscriptionFormMapping=subscriptionFormMapping formClass='offer1-sidebar-subscription-form' textColor='white'></@subscriptionForm.subscriptionForm>
    </div>
    </#if>
  </#if>
  
  <#-- Facebook Page Widget - not used anymore
  <#if xlat['fb.page.url']??>
  <div class="card sidebar border-0 shadow-sm">
    <div class="card-body p-2">
      <div class="fb-page" data-href="${xlat['fb.page.url']}"
      data-tabs="timeline" data-width="292" data-height="500" data-small-header="true"
      data-adapt-container-width="true" data-hide-cover="false" data-show-facepile="false"
      data-lazy="true">
        <blockquote cite="${xlat['fb.page.url']}" class="fb-xfbml-parse-ignore">
          <a href="${xlat['fb.page.url']}">${xlat['fb.page.title']}</a>
        </blockquote>
      </div>
    </div>
  </div>
  </#if>
  -->
</div>
</#macro>
