<#import "/layouts/adminLayout.ftl" as layout>
<@layout.page>
<#assign fields = editForm.fields>
<h1>${fields.login.value!}</h1>

<form action="/admin/users/save" method="post" class="form-horizontal">
  <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div class="form-group row">
    <label for="${fields.login.elementId}" class="col-sm-2 col-form-label">Login</label>
    <div class="col-sm-10">
      <input type="text" name="${fields.login.name}" value="${fields.login.value!}" id="${fields.login.elementId}" size="30"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.password.elementId}" class="col-sm-2 col-form-label">Password</label>
    <div class="col-sm-10">
      <input type="password" name="${fields.password.name}" value="${fields.password.value!}" id="${fields.password.elementId}" size="30"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.password2.elementId}" class="col-sm-2 col-form-label">Password (for validation)</label>
    <div class="col-sm-10">
      <input type="password" name="${fields.password2.name}" value="${fields.password2.value!}" id="${fields.password2.elementId}" size="30"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.firstName.elementId}" class="col-sm-2 col-form-label">First name</label>
    <div class="col-sm-10">
      <input type="text" name="${fields.firstName.name}" value="${fields.firstName.value!}" id="${fields.firstName.elementId}" size="100"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.lastName.elementId}" class="col-sm-2 col-form-label">Last name</label>
    <div class="col-sm-10">
      <input type="text" name="${fields.lastName.name}" value="${fields.lastName.value!}" id="${fields.lastName.elementId}" size="100"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.email.elementId}" class="col-sm-2 col-form-label">E-mail</label>
    <div class="col-sm-10">
      <input type="text" name="${fields.email.name}" value="${fields.email.value!}" id="${fields.email.elementId}" size="100"/>
    </div>
  </div>
  <div class="form-group row">
    <label for="${fields.roleIds.elementId}" class="col-sm-2 col-form-label">Roles</label>
    <div class="col-sm-10">
        <select name="${fields.roleIds.name}" id="${fields.roleIds.elementId}" multiple size="5">
          <#list roles as role>
            <option value="${role.id}"<#if fields.roleIds.filledObjects?seq_contains(role.id)> selected</#if>>${role.name}</option>
          </#list>
        </select>
     </div>
  </div>
  <div class="form-group row">
    <div class="col-sm-2 col-form-label"></div>
    <div class="col-sm-10">
        <button type="submit" class="btn btn-primary">Submit</button>
    </div>
  </div>
</form>
</@layout.page>
