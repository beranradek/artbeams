<#import "/forms.ftl" as forms>
<#macro contact>
<div class="contact-form-holder" id="contact-form-holder">
    <h3>Máte jakékoliv dotazy? Napište mi</h3>
    <form action="/kontakt" method="post" class="contact-form">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <#-- Form content refreshed by AJAX: -->
       <div class="contact-form-ajax-content">
          <#include "/contact/contactFormContent.ftl">
       </div>
      <p class="contact-info">
        Povinné údaje jsou označené *.
        Vaše osobní údaje budou použity pouze pro účely řešení dotazu.
        <a href="${xlat['personal-data.protection.url']}">${xlat['personal-data.protection.title']}</a>.
      </p>
      <@forms.buttonSubmit text="Odeslat" />
    </form>
</div>
<script nonce="${_cspNonce}">
  <!-- Function registered on document ready -->
  ready(ajaxHandleFormWithClass("contact-form", true));
</script>
</#macro>
