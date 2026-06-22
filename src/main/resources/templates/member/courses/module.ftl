<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<div class="container">
    <h1>${course.title} — ${module.title}</h1>
    <p>${module.perex?if_exists!module.shortDescription?if_exists!''}</p>

    <#if articles?has_content>
        <div id="module-articles">
            <#list articles as a>
                <div class="card mb-2">
                    <div class="card-body">
                        <h5 class="card-title"><a href="/a/${a.slug}">${a.title}</a></h5>
                        <p class="card-text">${a.perex?if_exists!''}</p>
                    </div>
                </div>
            </#list>
        </div>
    <#else>
        <div class="alert alert-secondary">Žádné články v modulu.</div>
    </#if>
</div>

</@layout.page>
