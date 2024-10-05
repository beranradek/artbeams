<#import "/loginLikePageLayout.ftl" as layout>
<@layout.page>

<div class="centered-box login-form">
    <h2 class="logo"><a href="/">${xlat['website.title']}</a></h2>
    <h3>Přihlásit se</h3>
    <#if _requestParameterError??>
        <div class="alert alert-danger" role="alert">Neplatný login nebo heslo.</div>
    </#if>
    <#if _requestParameterLogout??>
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

        <#if _requestParameterError??>
            <div class="form-group">
                <div class="col-sm-12 text-danger text-align-right">
                    <a href="/password-recovery">${(xlat[login.forgotten-password!]!)?has_content?then(xlat[login.forgotten-password!], 'Zapomněli jste heslo?')}</a>
                </div>
            </div>
        </#if>

        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="submit" class="btn btn-primary">Přihlásit se</button>
            </div>
        </div>
    </form>
</div>

</@layout.page>
