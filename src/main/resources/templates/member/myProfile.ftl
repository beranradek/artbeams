<#import "/member/memberLayout.ftl" as layout>
<#import "/forms.ftl" as forms>
<@layout.page>
<#assign fields = editForm.fields>

<form action="/clenska-sekce/muj-profil" method="post" class="form-horizontal">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <h3>Můj profil</h3>
    <@forms.inputHidden field=fields.login />
    <@forms.globalMessages messages=editForm.validationResult.globalMessages />
    <@forms.inputText field=fields.firstName label="Křestní jméno" required=false size=100 inputDivClass="col-sm-3" labelFix=false />
    <@forms.inputText field=fields.lastName label="Příjmení" required=false size=100 inputDivClass="col-sm-3" labelFix=false />

    <h3>${xlat['newPassword']}</h3>

    <@forms.inputText type="password" field=fields.password label="${xlat['password']}" required=false size=60 inputDivClass="col-sm-3" labelFix=false />
    <@forms.inputText type="password" field=fields.password2 label="${xlat['password.again-for-control']}" required=false size=60 inputDivClass="col-sm-3" labelFix=false />
    <@forms.buttonSubmit text="Uložit profil" class="btn btn-primary" />
</form>

<div class="mt-5 pt-4 border-top">
    <p class="text-muted">
        Smazání účtu je nevratná operace. Vaše osobní údaje budou anonymizovány v souladu s GDPR.
    </p>
    <a href="/clenska-sekce/muj-profil/smazat-ucet" class="btn btn-outline-danger">
        <i class="fas fa-trash-alt"></i>
        Smazat můj účet
    </a>
</div>

</@layout.page>
