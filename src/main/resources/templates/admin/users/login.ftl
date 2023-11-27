<#import "/adminLayout.ftl" as layout>
<@layout.page>

<div class="login-form">
    <h2>${xlat['website.title']}</h2>
    <#if RequestParameters.error??>
        <div class="alert alert-danger" role="alert">Neplatný login nebo heslo.</div>
    </#if>
    <#if RequestParameters.logout??>
        <div class="alert alert-success" role="alert">Byl(a) jste úspěšně odhlášen(a).</div>
    </#if>

    <form action="/login" method="post" class="form-horizontal">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div class="form-group">
            <label for="username" class="col-sm-12 col-form-label">Login: </label>
            <div class="col-sm-12">
                <input type="text" id="username" name="username" class="form-control" />
            </div>
        </div>
        <div class="form-group">
            <label for="password" class="col-sm-12 col-form-label">Heslo: </label>
            <div class="col-sm-12">
                <input type="password" id="password" name="password" class="form-control" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="submit" class="btn btn-primary">Přihlásit se</button>
            </div>
        </div>
    </form>
</div>

</@layout.page>
