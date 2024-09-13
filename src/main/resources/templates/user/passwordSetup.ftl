<#import "/adminLayout.ftl" as layout>
<#import "/forms.ftl" as forms>
<@layout.page>
<#assign fields = passwordSetupForm.fields>
<div class="password-setup-form">
    <form action="/nastaveni-hesla" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

      <h3>${xlat['member.passwordSetup.header']}</h3>

      <p>${xlat['user']}: ${fields.login.value!}</p>

      <@forms.inputHidden field=fields.login />
      <@forms.inputHidden field=fields.token />
      <@forms.inputText type="password" field=fields.password label="${xlat['password']}" size=60 vertical=true />
      <@forms.inputText type="password" field=fields.password2 label="${xlat['password.again-for-control']}" size=60 vertical=true />
      <@forms.buttonSubmit text="${xlat['createAccount']}" class="btn btn-primary" />
    </form>
</div>
</@layout.page>