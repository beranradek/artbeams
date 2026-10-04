<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<div class="container">
  <div class="row"><div class="col-md-8">
    <h2>Potvrdit žádost o vrácení</h2>
    <p>Objednávka č. <strong>${order.orderNumber}</strong>. Žádost odešleme ke kontrole; platbu vracíme ručně mimo aplikaci.</p>
    <#if reason??>
      <div class="mb-3">
        <strong>Uvedený důvod:</strong>
        <div class="border rounded p-3 mt-1" style="white-space: pre-wrap;">${reason?html}</div>
      </div>
    <#else>
      <p class="text-muted">Důvod jste neuvedli.</p>
    </#if>
    <form method="post" action="/clenska-sekce/moje-objednavky/${order.id}/vraceni">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <#if reason??><input type="hidden" name="reason" value="${reason?html}"/></#if>
      <button type="submit" class="btn btn-danger">Závazně odeslat žádost</button>
      <a href="/clenska-sekce/moje-objednavky/${order.id}/vraceni" class="btn btn-link">Upravit</a>
    </form>
  </div></div>
</div>

</@layout.page>
