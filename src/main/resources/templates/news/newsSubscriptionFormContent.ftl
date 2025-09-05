<#-- News subscription form content for AJAX updates -->
<#import "/spring.ftl" as spring />
<#import "/forms.ftl" as forms />

<#assign newsSubscriptionFields = newsSubscriptionFormMapping.fields>
<#assign globalMessages = newsSubscriptionFormMapping.validationResult.globalMessages>
<@forms.globalMessages messages=globalMessages />
<div class="input-group">
  <@forms.inputText type="email" field=newsSubscriptionFields.email label=xlat['news.form.placeholder'] labelAsPlaceholder=true rowClass="ml-form-fieldRow" />
  <button type="submit" class="btn btn-primary mt-2">${xlat['news.form.button']}</button>
</div>
