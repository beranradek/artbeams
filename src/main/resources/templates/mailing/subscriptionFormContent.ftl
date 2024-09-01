<#import "/forms.ftl" as forms>
<#assign fields = subscriptionFormMapping.fields>
<#assign globalMessages = subscriptionFormMapping.validationResult.globalMessages>
<@forms.globalMessages messages=globalMessages />
<@forms.inputText field=fields.name label="Jméno" required=false labelAsPlaceholder=true rowClass="ml-form-fieldRow" />
<@forms.inputText type="email" field=fields.email label="Email" labelAsPlaceholder=true rowClass="ml-form-fieldRow" />
