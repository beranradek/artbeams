<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card border-danger">
                <div class="card-header bg-danger text-white">
                    <h3 class="mb-0">
                        <i class="fas fa-exclamation-triangle"></i>
                        Smazání účtu
                    </h3>
                </div>
                <div class="card-body">
                    <#if error??>
                        <div class="alert alert-danger" role="alert">
                            ${error}
                        </div>
                    </#if>
                    
                    <h4>Opravdu chcete smazat svůj účet?</h4>
                    
                    <div class="alert alert-warning mt-3" role="alert">
                        <h5 class="alert-heading">Varování - Tato akce je nevratná!</h5>
                        <hr>
                        <p class="mb-0">Po smazání účtu:</p>
                        <ul class="mt-2">
                            <li>Vaše osobní údaje budou anonymizovány v souladu s GDPR</li>
                            <li>Již se nebudete moci přihlásit</li>
                            <li>Vaše objednávky zůstanou v systému, ale bez osobních údajů</li>
                            <li>Vaše komentáře zůstanou v systému, ale budou anonymní</li>
                            <li>Přístup k produktům v členské sekci bude zrušen</li>
                        </ul>
                    </div>
                    
                    <div class="mt-4">
                        <p><strong>Uživatel:</strong> ${user.fullName} (${user.login})</p>
                        <#if user.email??>
                            <p><strong>Email:</strong> ${user.email}</p>
                        </#if>
                    </div>
                    
                    <form action="/clenska-sekce/muj-profil/smazat-ucet" method="post" class="mt-4">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        
                        <div class="d-flex justify-content-between">
                            <a href="/clenska-sekce/muj-profil" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i>
                                Zrušit
                            </a>
                            <button type="submit" class="btn btn-danger">
                                <i class="fas fa-trash-alt"></i>
                                Ano, smazat můj účet
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

</@layout.page>
