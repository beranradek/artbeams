<#import "/layouts/adminLayout.ftl" as layout>
<@layout.page>
<#assign fields = editForm.fields>
<h1>${fields.login.value!}</h1>

<form action="/admin/users/save" method="post" class="form-horizontal">
  <input type="hidden" name="${fields.id.name}" value="${fields.id.value!}"/>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <div class="form-group">
    <label>Login
      <input type="text" name="${fields.login.name}" value="${fields.login.value!}" size="30"/>
    </label>
  </div>
  <div class="form-group">
    <label>Password
      <input type="password" name="${fields.password.name}" value="${fields.password.value!}" size="30"/>
    </label>
  </div>
  <div class="form-group">
    <label>Password (for validation)
      <input type="password" name="${fields.password2.name}" value="${fields.password2.value!}" size="30"/>
    </label>
  </div>
  <div class="form-group">
    <label>First name
      <input type="text" name="${fields.firstName.name}" value="${fields.firstName.value!}" size="100"/>
    </label>
  </div>
  <div class="form-group">
    <label>Last name
      <input type="text" name="${fields.lastName.name}" value="${fields.lastName.value!}" size="100"/>
    </label>
  </div>
  <div class="form-group">
    <label>E-mail
      <input type="text" name="${fields.email.name}" value="${fields.email.value!}" size="100"/>
    </label>
  </div>
  <div class="form-group">
    <label>Roles</label>
    <select name="${fields.roleIds.name}" multiple size="5">
      <#list roles as role>
        <option value="${role.id}"<#if fields.roleIds.filledObjects?seq_contains(role.id)> selected</#if>>${role.name}</option>
      </#list>
    </select>
  </div>
  <div class="form-group">
    <button type="submit" class="btn btn-primary">Submit</button>
  </div>
</form>
</@layout.page>
