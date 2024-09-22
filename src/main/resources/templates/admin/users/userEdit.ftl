<#import "/adminLayout.ftl" as layout>
<#import "/forms.ftl" as forms>
<@layout.page>
<#assign fields = editForm.fields>

<h1>${fields.login.value!}</h1>

<form action="/admin/users/save" method="post" class="form-horizontal">
  <@forms.inputHidden field=fields.id />
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <@forms.globalMessages messages=editForm.validationResult.globalMessages />
  <@forms.inputText field=fields.login label="Login" size=30 inputDivClass="col-sm-3" labelFix=false />
  <@forms.inputText field=fields.firstName label="First name" required=false size=100 inputDivClass="col-sm-3" labelFix=false />
  <@forms.inputText field=fields.lastName label="Last name" required=false size=100 inputDivClass="col-sm-3" labelFix=false />
  <@forms.inputText type="email" field=fields.email label="E-mail" required=false size=100 inputDivClass="col-sm-3" labelFix=false />

  <h3>New password</h3>

  <@forms.inputText type="password" field=fields.password label="Password" required=false size=60 inputDivClass="col-sm-3" labelFix=false />
  <@forms.inputText type="password" field=fields.password2 label="Repeat password" required=false size=60 inputDivClass="col-sm-3" labelFix=false />

  <div class="form-group row">
    <label for="${fields.roleIds.elementId}" class="col-sm-2 col-form-label">Roles</label>
    <div class="col-sm-3">
        <select name="${fields.roleIds.name}" id="${fields.roleIds.elementId}" multiple size="5" class="form-control">
          <#list roles as role>
            <option value="${role.id}"<#if fields.roleIds.filledObjects?seq_contains(role.id)> selected</#if>>${role.name}</option>
          </#list>
        </select>
     </div>
  </div>
  <@forms.buttonSubmit text="Submit" class="btn btn-primary" />
</form>
</@layout.page>
