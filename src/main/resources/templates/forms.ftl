<#macro globalMessages messages rowClass="ml-form-fieldRow">
    <div class="${rowClass}">
        <#list messages as message>
            <div class="ml-field-group">
                <div class="alert alert-danger" role="alert">${(xlat[message.msgKey!]!)?has_content?then(xlat[message.msgKey!], 'Nepřeložená chyba')}</div>
            </div>
        </#list>
    </div>
</#macro>

<#macro inputMessages messages rowClass="ml-form-fieldRow">
    <div class="${rowClass}">
        <#list messages as message>
            <div class="ml-field-group">
                <div class="alert alert-danger" role="alert">${(xlat[message.msgKey!]!)?has_content?then(xlat[message.msgKey!], 'Nepřeložená chyba')}</div>
            </div>
        </#list>
    </div>
</#macro>

<#macro inputTextArea field label required=true rows="8" cols="45" maxlength="20000">
    <div class="form-group row">
        <label for="${field.elementId}" class="label-fix<#if field.validationMessages?has_content> text-danger</#if>">${label}<#if required> *</#if></label>
        <textarea class="form-control<#if field.validationMessages?has_content> is-invalid</#if>" name="${field.name}" id="${field.elementId}" rows="${rows}" cols="${cols}" maxlength="${maxlength}"<#if required> required="required"</#if>>${field.value!}</textarea>
        <#if field.validationMessages?has_content>
            <div class="col-sm-10">
                <#list field.validationMessages as message>
                    <div data-message-key="${message.msgKey!}">
                        <small class="text-danger">
                            ${(xlat[message.msgKey!]!)?has_content?then(xlat[message.msgKey!], 'Vyplňte prosím správnou hodnotu.')}
                        </small>
                    </div>
                </#list>
            </div>
        </#if>
    </div>
</#macro>

<#macro inputText field label type="text" required=true size="64">
    <div class="form-group row">
        <label for="${field.elementId}" class="col-form-label col-sm-2 label-fix<#if field.validationMessages?has_content> text-danger</#if>">${label}<#if required> *</#if></label>
        <div class="col-sm-10">
            <input type="${type}" class="form-control<#if field.validationMessages?has_content> is-invalid</#if>" id="${field.elementId}" name="${field.name}" value="${field.value!}" size="${size}"<#if required> required="required"</#if>/>
        </div>
        <#if field.validationMessages?has_content>
            <div class="col-sm-2"></div>
            <div class="col-sm-10">
                <#list field.validationMessages as message>
                    <div data-message-key="${message.msgKey!}">
                        <small class="text-danger">
                            ${(xlat[message.msgKey!]!)?has_content?then(xlat[message.msgKey!], 'Vyplňte prosím správnou hodnotu.')}
                        </small>
                    </div>
                </#list>
            </div>
        </#if>
    </div>
</#macro>

<#macro inputHidden field>
    <input type="hidden" name="${field.name}" value="${field.value!}"/>
</#macro>

<#macro buttonSubmit text>
    <div class="form-group">
      <button name="submit" type="submit" class="btn">${text}</button>
    </div>
</#macro>
