<#import "/standalonePageLayout.ftl" as layout>
<#import "/forms.ftl" as forms>
<@layout.page>
<#assign fields = passwordRecoveryForm.fields>
<div class="centered-box password-recovery-form">
    <form action="/password-recovery" method="post" class="passwordRecoveryForm">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <input type="hidden" id="g-recaptcha-response" name="g-recaptcha-response">

      <h3>${xlat['passwordRecovery.header']}</h3>

      <p>${xlat['passwordRecovery.instructions']}</p>

      <@forms.globalMessages messages=passwordRecoveryForm.validationResult.globalMessages />
      <@forms.inputText type="email" field=fields.email label="${xlat['email']}" size=60 vertical=true />
      <@forms.buttonSubmit text="${xlat['passwordRecovery.submit']}" class="btn btn-primary" />
    </form>

    <script nonce="${_cspNonce}">
      <!-- Function registered on document ready -->
      ready(handleRecaptchaFormWithClass("passwordRecoveryForm"));
    </script>
</div>
</@layout.page>
