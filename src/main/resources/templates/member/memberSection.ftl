<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<#list userProducts as userProduct>
    <div class="card">
        <div class="card-header">

        </div>
        <div class="card-body">
            <a href="/clenska-sekce/${userProduct.slug}"><h3 class="card-title">${userProduct.title}</h3></a>
            <p class="card-text">${userProduct.subtitle}</p>
        </div>
    </div>
<#else>
    <div class="alert alert-warning" role="alert">
        Zatím jste si od nás nepořídili žádný produkt. To nevadí, můžete si na stránkách nějaký vybrat.
    </div>
</#list>

</@layout.page>
