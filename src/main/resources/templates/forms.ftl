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

<#macro inputTextArea field label required=true rows="8" cols="45" maxlength="20000" vertical=false labelFix=true>
    <div class="form-group row">
        <label for="${field.elementId}" class="<#if labelFix>label-fix</#if><#if field.validationMessages?has_content> text-danger</#if>">${label}<#if required> *</#if></label>
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

<#macro inputText field label type="text" required=true size="64" rowClass="form-group row" labelAsPlaceholder=false vertical=false readonly=false labelFix=true inputDivClass="">
    <div class="${rowClass}">
        <#if labelAsPlaceholder>
            <input type="${type}" class="form-control<#if field.validationMessages?has_content> is-invalid</#if>" id="${field.elementId}" name="${field.name}" value="${field.value!}" size="${size}"<#if readonly> readonly</#if><#if required> required</#if> placeholder="${label}"/>
        <#else>
            <label for="${field.elementId}" class="col-form-label<#if vertical> col-sm-12<#else> col-sm-2<#if labelFix> label-fix</#if></#if><#if field.validationMessages?has_content> text-danger</#if>">${label}<#if required> *</#if></label>
            <div class="<#if inputDivClass?has_content>${inputDivClass}<#else><#if vertical>col-sm-12<#else>col-sm-10</#if></#if>">
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

<#macro inputDateTime field label required=true vertical=false labelFix=true inputDivClass="">
    <div class="form-group row">
        <label for="${field.elementId}" class="col-form-label<#if vertical> col-sm-12<#else> col-sm-2<#if labelFix> label-fix</#if></#if><#if field.validationMessages?has_content> text-danger</#if>">${label}<#if required> *</#if></label>
        <div class="<#if inputDivClass?has_content>${inputDivClass}<#else><#if vertical>col-sm-12<#else>col-sm-10</#if></#if>">
            <input type="datetime-local"
                   class="form-control<#if field.validationMessages?has_content> is-invalid</#if>"
                   id="${field.elementId}"
                   name="${field.name}_display"
                   data-original-name="${field.name}"
                   data-original-value="${field.value!}"
                   <#if required>required</#if>/>
            <input type="hidden" name="${field.name}" id="${field.elementId}_hidden" value="${field.value!}"/>
            <small class="form-text text-muted">Format: DD.MM.YYYY HH:MM</small>
        </div>
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

<#macro inputCheckbox field label vertical=false labelFix=true inputDivClass="">
    <div class="form-group row">
        <label class="col-form-label<#if vertical> col-sm-12<#else> col-sm-2<#if labelFix> label-fix</#if></#if><#if field.validationMessages?has_content> text-danger</#if>">${label}</label>
        <div class="<#if inputDivClass?has_content>${inputDivClass}<#else><#if vertical>col-sm-12<#else>col-sm-10</#if></#if>">
            <div class="form-check form-switch">
                <input type="checkbox"
                       class="form-check-input<#if field.validationMessages?has_content> is-invalid</#if>"
                       id="${field.elementId}"
                       name="${field.name}"
                       value="true"
                       <#if field.value?? && field.value?string == "true">checked</#if>/>
                <label class="form-check-label" for="${field.elementId}">
                    <#if field.value?? && field.value?string == "true">
                        <span class="badge bg-success">Enabled</span>
                    <#else>
                        <span class="badge bg-secondary">Disabled</span>
                    </#if>
                </label>
            </div>
        </div>
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

<#macro buttonSubmit text class="btn btn-primary-custom">
    <div class="form-group">
      <button name="submitButton" type="submit" class="${class}">${text}</button>
    </div>
</#macro>
