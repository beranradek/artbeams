<#import "/member/memberLayout.ftl" as layout>
<@layout.page>
<#assign fields = passwordSetupForm.fields>

<form action="/nastaveni-hesla" method="post" class="form-horizontal">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <h2>${xlat['website.title']}</h2>
  <h3>${xlat['member.passwordSetup.header']}</h3>

  <div class="form-group row">
    <label for="${fields.login.elementId}" class="col-sm-2 col-form-label">${xlat['username.cannot-be-changed']}</label>
    <div class="col-sm-3">
      <input type="text" name="${fields.login.name}" value="${fields.login.value!}" id="${fields.login.elementId}" readonly size="30" class="form-control"/>
    </div>
  </div>

  <h3>${xlat['newPassword']}</h3>

  <div class="form-group row">
    <label for="${fields.password.elementId}" class="col-sm-2 col-form-label">${xlat['password']}</label>
    <div class="col-sm-3">
      <input type="password" name="${fields.password.name}" value="${fields.password.value!}" id="${fields.password.elementId}" size="30" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.password2.elementId}" class="col-sm-2 col-form-label">${xlat['password.again-for-control']}</label>
    <div class="col-sm-3">
      <input type="password" name="${fields.password2.name}" value="${fields.password2.value!}" id="${fields.password2.elementId}" size="30" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-10">
        <button type="submit" class="btn btn-primary">${xlat['createAccount']}</button>
    </div>
  </div>
</form>
</@layout.page>
