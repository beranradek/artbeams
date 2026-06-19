<#-- Module view listing articles for a module -->
<div id="menu-kurz">
    <h2>Kurzy</h2>
    <ul>
        <#if courses?has_content>
            <#list courses as c>
                <li><a href="/clenska-sekce/courses/${c.slug}">${c.title}</a></li>
            </#list>
        </#if>
    </ul>
</div>

<div class="container">
    <h1>${course.title} — ${module.title}</h1>
    <p>${module.perex}</p>

    <div id="module-list">
        <#if articles?has_content>
            <ul>
                <#list articles as a>
                    <li><a href="/a/${a.slug}">${a.title}</a></li>
                </#list>
            </ul>
        <#else>
            <p>Žádné články v modulu.</p>
        </#if>
    </div>
</div>
