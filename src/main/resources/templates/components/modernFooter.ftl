<#macro modernFooter>
<!-- Footer -->
<footer class="pt-5 pb-4">
  <div class="container">
    <div class="row">
      <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
        <h5 class="footer-heading text-uppercase mb-4">${xlat['website.title']}</h5>
        <p>${xlat['website.purpose']}</p>
        <div class="mt-4">
          <#if xlat['fb.page.url']??>
            <a href="${xlat['fb.page.url']}" class="footer-social-icon"><i class="bi bi-facebook"></i></a>
          </#if>
          <#if xlat['instagram.url']??>
            <a href="${xlat['instagram.url']}" class="footer-social-icon"><i class="bi bi-instagram"></i></a>
          </#if>
          <#if xlat['twitter.url']??>
            <a href="${xlat['twitter.url']}" class="footer-social-icon"><i class="bi bi-twitter"></i></a>
          </#if>
          <#if xlat['youtube.url']??>
            <a href="${xlat['youtube.url']}" class="footer-social-icon"><i class="bi bi-youtube"></i></a>
          </#if>
        </div>
      </div>

      <#-- TBD: Not useful with only blog link. Maybe create list of e-books?
      <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
        <h5 class="footer-heading text-uppercase mb-4">Užitečné odkazy</h5>
        <ul class="list-unstyled">
          <li>
            <a href="/" class="footer-link">Blog</a>
          </li>
          <#if xlat['author.url']??>
          <li>
            <a href="${xlat['author.url']}" class="footer-link">O mně</a>
          </li>
          </#if>
          <#if xlat['products.url']??>
          <li>
            <a href="${xlat['products.url']}" class="footer-link">E-booky</a>
          </li>
          </#if>
          <#if xlat['contact.url']??>
          <li>
            <a href="${xlat['contact.url']}" class="footer-link">Kontakt</a>
          </li>
          </#if>
        </ul>
      </div>
      -->

      <#if articleCategories??>
      <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
        <h5 class="footer-heading text-uppercase mb-4">Rubriky</h5>
        <ul class="list-unstyled">
          <#-- TBD: Fill in article categories for contact page and product pages. -->
          <#list articleCategories as category>
            <#if category?index < 5>
            <li>
              <a href="${xlat['categories.url.base']}/${category.slug}" class="footer-link">${category.title}</a>
            </li>
            </#if>
          </#list>
        </ul>
      </div>
      </#if>

      <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
        <h5 class="footer-heading text-uppercase mb-4">Kontakt</h5>
        <ul class="list-unstyled">
          <li>
            <a href="/kontakt" class="footer-link">Kontakt</a>
          </li>
          <#if xlat['contact.address']??>
          <li class="mb-2">
            <i class="bi bi-geo-alt me-2 text-secondary-custom"></i>
            <span>${xlat['contact.address']}</span>
          </li>
          </#if>
          <#if xlat['contact.email']??>
          <li class="mb-2">
            <i class="bi bi-envelope me-2 text-secondary-custom"></i>
            <a href="mailto:${xlat['contact.email']}" class="footer-link">${xlat['contact.email']}</a>
          </li>
          </#if>
          <#if xlat['contact.phone']??>
          <li>
            <i class="bi bi-telephone me-2 text-secondary-custom"></i>
            <a href="tel:${xlat['contact.phone']}" class="footer-link">${xlat['contact.phone']}</a>
          </li>
          </#if>
        </ul>
      </div>

      <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
        <h5 class="footer-heading text-uppercase mb-4">${xlat['news.form.title']}</h5>
        <p class="small mb-3">${xlat['news.form.intro']}</p>
        
        <form class="news-subscription-form" action="/news/subscribe" method="post">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <div class="news-subscription-form-ajax-content">
            <div class="input-group">
              <input type="email" 
                     class="form-control" 
                     name="email"
                     placeholder="${xlat['news.form.placeholder']}"
                     required>
              <button type="submit" class="btn btn-primary">${xlat['news.form.button']}</button>
            </div>
            <input type="hidden" id="g-recaptcha-response" name="g-recaptcha-response" value="">
          </div>
        </form>
      </div>
    </div>
    
    <hr class="my-4 footer-line" />
    
    <div class="row align-items-center">
      <div class="col-md-7 text-center text-md-start">
        <p class="small mb-0">
          &copy; ${.now?string('yyyy')} ${xlat['website.title']}. ${xlat['website.disclaimer']}
        </p>
      </div>
      <div class="col-md-5 text-center text-md-end">
        <ul class="list-inline mb-0">
          <#if xlat['terms-and-conditions.url']??>
          <li class="list-inline-item">
            <a href="${xlat['terms-and-conditions.url']}" class="footer-link small">${xlat['terms-and-conditions.title']}</a>
          </li>
          <li class="list-inline-item">
            <span class="text-secondary-custom mx-2">•</span>
          </li>
          </#if>
          <#if xlat['personal-data.protection.url']??>
          <li class="list-inline-item">
            <a href="${xlat['personal-data.protection.url']}" class="footer-link small">${xlat['personal-data.protection.title']}</a>
          </li>
          <li class="list-inline-item">
            <span class="text-secondary-custom mx-2">•</span>
          </li>
          </#if>
          <#if xlat['cookies.url']??>
            <li class="list-inline-item">
              <a href="${xlat['cookies.url']}" class="footer-link small">${xlat['cookies.title']}</a>
            </li>
            <li class="list-inline-item">
              <span class="text-secondary-custom mx-2">•</span>
            </li>
          </#if>
          <li class="list-inline-item">
            <a href="#body-element" class="footer-link small"><i class="fa-solid fa-arrow-up"></i> ${xlat['goto.up']}</a>
          </li>
        </ul>
      </div>
    </div>
  </div>
</footer>
</#macro>
