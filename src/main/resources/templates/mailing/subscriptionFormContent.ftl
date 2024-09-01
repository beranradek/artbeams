<#import "/forms.ftl" as forms>
<#assign fields = subscriptionFormMapping.fields>
<#assign globalMessages = subscriptionFormMapping.validationResult.globalMessages>
<@forms.globalMessages messages=globalMessages />
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
