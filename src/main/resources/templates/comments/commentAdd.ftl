<#import "/forms.ftl" as forms>
<#macro commentAdd>
<#assign fields = commentForm.fields>
<div class="comment-add" id="comment-add">
    <h3 class="comment-add-title">Napsat komentář</h3>
    <form action="/comments" method="post" class="comment-form">
      <@forms.inputHidden field=fields.id />
      <@forms.inputHidden field=fields.entityId />
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <#-- Form content refreshed by AJAX: -->
       <div class="comment-form-ajax-content">
          <#include "/comments/commentFormContent.ftl">
       </div>
      <p class="comment-info">Povinné údaje jsou označeny *. Emailová adresa nebude zveřejněna. Vaše osobní údaje budou použity pouze pro účely zpracování tohoto komentáře. <a href="/ochrana-osobnich-udaju">Zásady zpracování osobních údajů</a>.</p>
      <@forms.buttonSubmit text="Odeslat komentář" />
    </form>
</div>
<script nonce="${_cspNonce}">
  <!-- Function registered on document ready -->
  ready(ajaxHandleFormWithClass("comment-form", true));
</script>
</#macro>
