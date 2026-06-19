<#-- Member courses list -->
<div id="menu-kurz">
    <h2>Kurzy</h2>
    <ul id="course-list">
    <#if courses?has_content>
        <#list courses as course>
            <li><a href="/clenska-sekce/courses/${course.slug}">${course.title}</a></li>
        </#list>
    <#else>
        <li>Žádné kurzy</li>
    </#if>
    </ul>
</div>

<#-- simple listing tiles -->
<div class="container">
    <h1>Moje kurzy</h1>
    <div class="row">
        <#if courses?has_content>
            <#list courses as course>
                <div class="col-md-4">
                    <h3><a href="/clenska-sekce/courses/${course.slug}">${course.title}</a></h3>
                    <p>${course.perex}</p>
                </div>
            </#list>
        <#else>
            <p>Prozatím nemáte žádné kurzy.</p>
        </#if>
    </div>
</div>
