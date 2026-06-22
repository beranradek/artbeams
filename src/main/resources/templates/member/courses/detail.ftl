<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<div class="container">
    <div class="row">
        <div class="col-md-8">
            <h1>${course.title}</h1>
            <p>${course.perex?if_exists!''}</p>

            <form method="get" action="/clenska-sekce/courses/${course.slug}/search" class="mb-3">
                <div class="input-group">
                    <input type="text" name="q" class="form-control" placeholder="Hledat v kurzu" value="${q?if_exists!''}" />
                    <button class="btn btn-primary" type="submit">Hledat</button>
                </div>
            </form>

            <#if articles?has_content>
                <div id="course-articles">
                    <#list articles as a>
                        <div class="card mb-2">
                            <div class="card-body">
                                <h5 class="card-title"><a href="/articles/${a.slug}">${a.title}</a></h5>
                                <p class="card-text">${a.perex?if_exists!''}</p>
                            </div>
                        </div>
                    </#list>
                </div>
            <#else>
                <div class="alert alert-secondary">Žádné články.</div>
            </#if>
        </div>
        <div class="col-md-4">
            <div>
                <h4>Moduly</h4>
                <ul>
                    <#list course.modules as m>
                        <li><a href="/clenska-sekce/courses/${course.slug}/modules/${m.id}">${m.title}</a></li>
                    </#list>
                </ul>
            </div>
        </div>
    </div>
</div>

</@layout.page>
