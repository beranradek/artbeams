<#assign fields = subscriptionFormMapping.fields>
<#assign globalMessages = subscriptionFormMapping.validationResult.globalMessages>
<input type="hidden" name="${fields.antispamQuestion.name}" value="${fields.antispamQuestion.value!}"/>
<div class="ml-form-fieldRow">
    <#list globalMessages as globalMessage>
        <div class="ml-field-group">
            <div class="alert alert-danger" role="alert">${globalMessage.text!}</div>
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
<div class="ml-form-fieldRow ml-last-item">
   <div class="ml-field-group ml-validate-required">
      <label class="col-form-label cursor-help  ml-form-embedContent" title="Kontrolní otázka - ochrana proti robotům">${fields.antispamQuestion.value!}</label><br/>
      <input type="text" class="form-control" name="${fields.antispamAnswer.name}" value="${fields.antispamAnswer.value!}" required data-inputmask=""/>
   </div>
</div>
