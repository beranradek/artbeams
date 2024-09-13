<#macro globalMessages messages rowClass="form-group row">
    <div class="${rowClass}">
        <#list messages as message>
            <div data-message-key="${message.msgKey!}">
                <small class="text-danger">
                    ${(xlat[message.msgKey!]!)?has_content?then(xlat[message.msgKey!], 'Nepřeložená chyba')}
                </small>
            </div>
        </#list>
    </div>
</#macro>

<#macro inputTextArea field label required=true rows="8" cols="45" maxlength="20000" vertical=false>
    <div class="form-group row">
        <label for="${field.elementId}" class="label-fix<#if field.validationMessages?has_content> text-danger</#if>">${label}<#if required> *</#if></label>
        <textarea class="form-control<#if field.validationMessages?has_content> is-invalid</#if>" name="${field.name}" id="${field.elementId}" rows="${rows}" cols="${cols}" maxlength="${maxlength}"<#if required> required="required"</#if>>${field.value!}</textarea>
        <#if field.validationMessages?has_content>
            <div class="<#if vertical> col-sm-12<#else>col-sm-10</#if>">
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

<#macro inputText field label type="text" required=true size="64" rowClass="form-group row" labelAsPlaceholder=false vertical=false readonly=false>
    <div class="${rowClass}">
        <#if labelAsPlaceholder>
            <input type="${type}" class="form-control<#if field.validationMessages?has_content> is-invalid</#if>" id="${field.elementId}" name="${field.name}" value="${field.value!}" size="${size}"<#if readonly> readonly</#if><#if required> required</#if> placeholder="${label}"/>
        <#else>
            <label for="${field.elementId}" class="col-form-label<#if vertical> col-sm-12<#else> col-sm-2 label-fix</#if><#if field.validationMessages?has_content> text-danger</#if>">${label}<#if required> *</#if></label>
            <div class="<#if vertical> col-sm-12<#else>col-sm-10</#if>">
                <input type="${type}" class="form-control<#if field.validationMessages?has_content> is-invalid</#if>" id="${field.elementId}" name="${field.name}" value="${field.value!}" size="${size}"<#if readonly> readonly</#if><#if required> required</#if>/>
            </div>
        </#if>
        <#if field.validationMessages?has_content>
            <#if !vertical>
                <div class="col-sm-2"></div>
            </#if>
            <div class="<#if vertical> col-sm-12<#else>col-sm-10</#if>">
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

<#macro buttonSubmit text class="btn">
    <div class="form-group">
      <button name="submit" type="submit" class="${class}">${text}</button>
    </div>
</#macro>
