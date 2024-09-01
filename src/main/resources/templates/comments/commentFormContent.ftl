<#import "/forms.ftl" as forms>
<#assign fields = commentForm.fields>
<@forms.globalMessages messages=commentForm.validationResult.globalMessages />
<@forms.inputTextArea field=fields.comment label="Komentář" />
<@forms.inputText field=fields.userName label="Jméno" />
<@forms.inputText field=fields.email label="Email" />
