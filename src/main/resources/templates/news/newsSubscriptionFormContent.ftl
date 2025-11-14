<#-- News subscription form content for AJAX updates -->
<#import "/spring.ftl" as spring />
<#import "/forms.ftl" as forms />

<#assign newsSubscriptionFields = newsSubscriptionFormMapping.fields>
<#assign globalMessages = newsSubscriptionFormMapping.validationResult.globalMessages>
<@forms.globalMessages messages=globalMessages />
<div class="input-group">
  <input type="email" class="form-control<#if newsSubscriptionFields.email.validationMessages?has_content> is-invalid</#if>" id="${newsSubscriptionFields.email.elementId}" name="${newsSubscriptionFields.email.name}" value="${newsSubscriptionFields.email.value!}" required placeholder="${xlat['news.form.placeholder']}"/>
  <button type="submit" class="btn btn-primary">${xlat['news.form.button']}</button>
</div>
<#if newsSubscriptionFields.email.validationMessages?has_content>
  <div class="mt-1">
    <#list newsSubscriptionFields.email.validationMessages as message>
      <div data-message-key="${message.msgKey!}">
        <small class="text-danger">
          ${(xlat[message.msgKey!]!)?has_content?then(xlat[message.msgKey!], 'Vyplňte prosím správnou hodnotu.')}
        </small>
      </div>
    </#list>
  </div>
</#if>
