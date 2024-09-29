<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<h1>${userProduct.title}</h1>
<#if userProduct.subtitle??>
    <h2>${userProduct.subtitle}</h2>
</#if>

</@layout.page>
