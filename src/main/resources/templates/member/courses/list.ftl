<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<div class="container">
    <h1>Kurzy</h1>
    <#if courses?has_content>
        <div class="row">
            <#list courses as c>
                <div class="col-md-4">
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title"><a href="/clenska-sekce/courses/${c.slug}">${c.title}</a></h5>
                            <p class="card-text">${c.perex?if_exists!''}</p>
                        </div>
                    </div>
                </div>
            </#list>
        </div>
    <#else>
        <div class="alert alert-info">Žádné kurzy k zobrazení.</div>
    </#if>
</div>

</@layout.page>
