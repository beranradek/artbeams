<#import "/member/memberLayout.ftl" as layout>
<@layout.page>
<#assign fields = editForm.fields>

<form action="/clenska-sekce/muj-profil" method="post" class="form-horizontal">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <h3>Můj profil</h3>

  <div class="form-group row">
    <label for="${fields.login.elementId}" class="col-sm-2 col-form-label">Uživatelské jméno (nelze změnit)</label>
    <div class="col-sm-3">
      <input type="text" name="${fields.login.name}" value="${fields.login.value!}" id="${fields.login.elementId}" readonly size="30" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.firstName.elementId}" class="col-sm-2 col-form-label">Křestní jméno</label>
    <div class="col-sm-3">
      <input type="text" name="${fields.firstName.name}" value="${fields.firstName.value!}" id="${fields.firstName.elementId}" size="100" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.lastName.elementId}" class="col-sm-2 col-form-label">Příjmení</label>
    <div class="col-sm-3">
      <input type="text" name="${fields.lastName.name}" value="${fields.lastName.value!}" id="${fields.lastName.elementId}" size="100" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.email.elementId}" class="col-sm-2 col-form-label">E-mail (povinný)</label>
    <div class="col-sm-3">
      <input type="text" name="${fields.email.name}" value="${fields.email.value!}" id="${fields.email.elementId}" size="100" class="form-control"/>
    </div>
  </div>

  <h3>Nové heslo</h3>

  <div class="form-group row">
    <label for="${fields.password.elementId}" class="col-sm-2 col-form-label">Heslo</label>
    <div class="col-sm-3">
      <input type="password" name="${fields.password.name}" value="${fields.password.value!}" id="${fields.password.elementId}" size="30" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.password2.elementId}" class="col-sm-2 col-form-label">Heslo (znovu pro kontrolu)</label>
    <div class="col-sm-3">
      <input type="password" name="${fields.password2.name}" value="${fields.password2.value!}" id="${fields.password2.elementId}" size="30" class="form-control"/>
    </div>
  </div>
  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-10">
        <button type="submit" class="btn btn-primary">Uložit profil</button>
    </div>
  </div>
</form>
</@layout.page>
