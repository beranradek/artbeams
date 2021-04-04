<#import "/layouts/webLayout.ftl" as webLayout>
<@webLayout.page>
    <h2>
      <#if status == 401>Stránka vyžaduje přihlášení
      <#elseif status == 403>Přístup nebyl povolen
      <#else>Nastala neočekávaná chyba
      </#if>
    </h2>
</@webLayout.page>
