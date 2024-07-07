<#import "/errorLayout.ftl" as webLayout>
<@webLayout.page>
    <h2>
      <#if status == 400>Neplatná vstupní data
      <#elseif status == 401>Tato akce vyžaduje přihlášení
      <#elseif status == 403>Přístup nebyl povolen
      <#elseif status == 500>Interní chyba serveru
      <#else>Nastala neočekávaná chyba
      </#if>
    </h2>
</@webLayout.page>
