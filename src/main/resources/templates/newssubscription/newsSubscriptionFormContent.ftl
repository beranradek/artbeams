<#-- News subscription form content for AJAX updates -->
<#import "/spring.ftl" as spring />

<#if subscriptionForm.validationResult.hasErrors() || subscriptionForm.validationResult.hasGlobalErrors()>
    <div class="alert alert-danger">
        <#if subscriptionForm.validationResult.hasGlobalErrors()>
            <#list subscriptionForm.validationResult.globalMessages as error>
                <p class="mb-0"><@spring.messageText error.text, error.text/></p>
            </#list>
        <#else>
            <p class="mb-0">${xlat['news.error.message']}</p>
        </#if>
    </div>
</#if>

<div class="input-group">
    <#assign emailField = subscriptionForm.field("email") />
    <input type="email" 
           class="form-control<#if emailField.validationResult.hasFieldErrors()> is-invalid</#if>" 
           name="${emailField.name}"
           value="${emailField.value!''}"
           placeholder="${xlat['news.form.placeholder']}"
           required>
    <button type="submit" class="btn btn-primary">${xlat['news.form.button']}</button>
    <#if emailField.validationResult.hasFieldErrors()>
        <div class="invalid-feedback">
            <#list emailField.validationResult.fieldMessages as error>
                <@spring.messageText error.text, error.text/>
            </#list>
        </div>
    </#if>
</div>

<input type="hidden" id="g-recaptcha-response" name="g-recaptcha-response" value="">