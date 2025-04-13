<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<h1>${userProduct.title}</h1>
<#if userProduct.subtitle??>
    <h2>${userProduct.subtitle}</h2>
</#if>

<div class="mt-4">
    <a href="/clenska-sekce/${userProduct.slug}/download" class="btn btn-primary">Stáhnout produkt</a>
</div>

</@layout.page>
