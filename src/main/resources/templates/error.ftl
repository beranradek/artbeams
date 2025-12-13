<#import "/errorLayout.ftl" as webLayout>
<@webLayout.page>
    <#if errorCode?? && errorCode == "consent-required">
        <h2>${xlat["error.consent-required.title"]!errorCode}</h2>
        <p>${xlat["error.consent-required.message"]!errorMessage}</p>
        <p><a href="/clenska-sekce/muj-profil" class="btn btn-primary">Přejít na profil</a></p>
    <#else>
        <h2>
          <#if status == 400>Neplatná vstupní data
          <#elseif status == 401>
            <#if errorCode?? && errorCode == "consent-required">
              ${xlat["error.consent-required.title"]!"Vyžadován souhlas"}
            <#else>
              Tato akce vyžaduje přihlášení
            </#if>
          <#elseif status == 403>Přístup nebyl povolen
          <#elseif status == 500>Interní chyba serveru
          <#else>Nastala neočekávaná chyba
          </#if>
        </h2>
        <#if errorCode?? && errorCode == "consent-required">
          <p>${xlat["error.consent-required.message"]!errorMessage}</p>
          <p><a href="/clenska-sekce/muj-profil" class="btn btn-primary">Přejít na profil</a></p>
        </#if>
    </#if>
</@webLayout.page>
