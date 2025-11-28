<#import "/forms.ftl" as forms>
<#macro commentAdd>
<#assign fields = commentForm.fields>
<div class="comment-add" id="comment-add">
    <h3 class="comment-add-title"><span data-i18n-key="comment.add.title">Napsat komentář</span></h3>
    <form action="/comments" method="post" class="comment-form">
      <@forms.inputHidden field=fields.id />
      <@forms.inputHidden field=fields.entityId />
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <#-- Form content refreshed by AJAX: -->
       <div class="comment-form-ajax-content">
          <#include "/comments/commentFormContent.ftl">
       </div>
      <p class="comment-info"><span data-i18n-key="comment.add.info">Povinné údaje jsou označeny *. Emailová adresa nebude zveřejněna. Vaše osobní údaje budou použity pouze pro účely zpracování tohoto komentáře.</span> <a href="${xlat['personal-data.protection.url']}"><span data-i18n-key="personal-data.protection.title">${xlat['personal-data.protection.title']}</span></a>.
      <span data-i18n-key="comment.add.moderation">Komentáře s odkazy jsou před zveřejněním schvalovány. HTML znaky nejsou povoleny.</span></p>
      <@forms.buttonSubmit text="Odeslat komentář" />
    </form>
</div>
<script nonce="${_cspNonce}">
  <!-- Function registered on document ready -->
  ready(ajaxHandleFormWithClass("comment-form", true));
</script>
</#macro>
