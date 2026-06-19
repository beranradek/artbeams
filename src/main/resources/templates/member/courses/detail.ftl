<#-- Course detail with modules and search form -->
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
    <h1>${course.title}</h1>
    <p>${course.perex}</p>

    <form action="/clenska-sekce/courses/${course.slug}/search" method="get">
        <input type="text" name="q" value="${q!}" placeholder="Hledat v kurzu" />
        <button type="submit">Hledat</button>
    </form>

    <div id="course-list">
        <#if articles?has_content>
            <ul>
            <#list articles as a>
                <li><a href="/a/${a.slug}">${a.title}</a></li>
            </#list>
            </ul>
        <#else>
            <p>Žádné články.</p>
        </#if>
    </div>

    <h2>Moduly</h2>
    <ul>
        <#list course.modules as m>
            <li><a href="/clenska-sekce/courses/${course.slug}/modules/${m.id}">${m.title}</a></li>
        </#list>
    </ul>
</div>
