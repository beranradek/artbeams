<#assign fields = subscriptionFormMapping.fields>
<#assign globalMessages = subscriptionFormMapping.validationResult.globalMessages>
<div class="ml-form-fieldRow">
    <#list globalMessages as globalMessage>
        <div class="ml-field-group">
            <div class="alert alert-danger" role="alert">${(xlat[globalMessage.msgKey!]!)?has_content?then(xlat[globalMessage.msgKey!], 'Nepřeložená chyba')}</div>
        </div>
    </#list>
</div>
<div class="ml-form-fieldRow">
   <div class="ml-field-group ml-field-name">
      <input type="text" class="form-control" name="${fields.name.name}" value="${fields.name.value!}" aria-label="name" data-inputmask="" placeholder="Jméno" autocomplete="given-name">
   </div>
</div>
<div class="ml-form-fieldRow">
   <div class="ml-field-group ml-field-email ml-validate-email ml-validate-required">
      <input type="email" class="form-control" name="${fields.email.name}" value="${fields.email.value!}" aria-label="email" aria-required="true" required data-inputmask="" placeholder="Email" autocomplete="email">
   </div>
</div>
