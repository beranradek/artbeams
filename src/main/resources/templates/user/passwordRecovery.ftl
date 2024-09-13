<#import "/adminLayout.ftl" as layout>
<#import "/forms.ftl" as forms>
<@layout.page>
<#assign fields = passwordRecoveryForm.fields>
<div class="password-recovery-form">
    <form action="/password-recovery" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

      <h3>${xlat['passwordRecovery.header']}</h3>

      <p>${xlat['passwordRecovery.instructions']}</p>

      <@forms.inputText type="email" field=fields.email label="${xlat['email']}" size=60 vertical=true />
      <@forms.buttonSubmit text="${xlat['passwordRecovery.submit']}" class="btn btn-primary" />
    </form>
</div>
</@layout.page>
