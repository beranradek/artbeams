<#import "/standalonePageLayout.ftl" as layout>
<#import "/forms.ftl" as forms>
<@layout.page>
<#assign fields = passwordSetupForm.fields>
<div class="centered-box password-setup-form">
    <form action="/password-setup" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

      <h3>${xlat['member.passwordSetup.header']}</h3>

      <p>${xlat['user']}: ${fields.login.value!}</p>

      <@forms.inputHidden field=fields.login />
      <@forms.inputHidden field=fields.token />
      <@forms.globalMessages messages=passwordSetupForm.validationResult.globalMessages />
      <@forms.inputText type="password" field=fields.password label="${xlat['password']}" size=60 vertical=true />
      <@forms.inputText type="password" field=fields.password2 label="${xlat['password.again-for-control']}" size=60 vertical=true />
      <@forms.buttonSubmit text="${xlat['passwordSetup.setPassword']}" class="btn btn-primary" />
    </form>
</div>
</@layout.page>
