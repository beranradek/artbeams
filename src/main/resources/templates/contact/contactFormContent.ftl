<#import "/forms.ftl" as forms>
<#assign fields = contactForm.fields>
<@forms.globalMessages messages=contactForm.validationResult.globalMessages />
<@forms.inputText field=fields.name label="Jméno" required=false />
<@forms.inputText type="email" field=fields.email label="E-mail" required=true />
<@forms.inputText field=fields.phone label="Telefon" required=false />
<@forms.inputTextArea field=fields.message label="Zpráva" required=true />
