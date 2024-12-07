<#import "/webLayout.ftl" as layout>
<#import "/contact/contactFormEnvelope.ftl" as contactFormEnvelope>
<@layout.page>
  <div class="container my-5">
      <div class="row">
          <!-- Left Section -->
          <div class="col-md-6">
              <h3>${xlat['author.name']}</h3>
              <#if idNumber??><p><strong>IČ:</strong> ${idNumber}</p></#if>
              <#if contactAddress??><p>${contactAddress}</p></#if>
              <#if contactEmail??>
                <p><strong>E-mail:</strong> <a href="mailto:${contactEmail}">${contactEmail}</a></p>
              </#if>
              <#if contactPhone??>
                <p><strong>Telefon:</strong> <a href="tel:${contactPhone}</a></p>
                <p class="text-muted">Prosím respektujte soukromí a volejte jen v nejnutnějších případech.</p>
              </#if>
              <#if facebookFunPageUrl??>
                <p><a href="${facebookFunPageUrl}" target="_blank"><strong>${facebookFunPageName}</strong></a></p>
              </#if>
          </div>

          <!-- Right Section: Contact Form -->
          <div class="col-md-6">
            <@contactFormEnvelope.contact></@contactFormEnvelope.contact>
          </div>
      </div>
  </div>
</@layout.page>
