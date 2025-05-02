<#import "/adminLayout.ftl" as layout>
<@layout.page>

<h2>Create New Order</h2>

<#if errorMessage??>
    <div class="alert alert-danger" role="alert">
        ${errorMessage}
    </div>
</#if>

<form action="/admin/orders/create" method="POST">
    <div class="mb-3">
        <label for="userId" class="form-label">Customer:</label>
        <select name="${createForm.fields.userId.name}" id="${createForm.fields.userId.elementId}" class="form-select">
            <option value="">-- Select Customer --</option>
            <#list users as user>
                <option value="${user.id}" <#if createForm.fields.userId.value == user.id>selected</#if>>
                    ${user.login} <#if user.name??>(${user.name})</#if>
                </option>
            </#list>
        </select>
        <#if createForm.fields.userId.violationMessage??>
            <div class="form-text text-danger">${createForm.fields.userId.violationMessage}</div>
        </#if>
    </div>
    
    <div class="mb-3">
        <label for="productId" class="form-label">Product:</label>
        <select name="${createForm.fields.productId.name}" id="${createForm.fields.productId.elementId}" class="form-select">
            <option value="">-- Select Product --</option>
            <#list products as product>
                <option value="${product.id}" <#if createForm.fields.productId.value == product.id>selected</#if>>
                    ${product.title} (${product.price})
                </option>
            </#list>
        </select>
        <#if createForm.fields.productId.violationMessage??>
            <div class="form-text text-danger">${createForm.fields.productId.violationMessage}</div>
        </#if>
    </div>
    
    <div class="alert alert-info">
        Order number will be generated automatically and the initial state will be set to CONFIRMED.
    </div>
    
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    
    <div class="mb-3">
        <button type="submit" class="btn btn-primary">Create Order</button>
        <a href="/admin/orders" class="btn btn-secondary">Cancel</a>
    </div>
</form>

</@layout.page> 