<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<div class="container">
  <div class="row"><div class="col-md-8">
    <h2>Žádost o vrácení objednávky</h2>
    <p>Objednávka č. <strong>${order.orderNumber}</strong>. Po odeslání žádost zkontrolujeme a vrácení platby provedeme ručně.</p>
    <form method="post" action="/clenska-sekce/moje-objednavky/${order.id}/vraceni/potvrdit">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <div class="mb-3">
        <label for="reason" class="form-label">Důvod (nepovinné)</label>
        <textarea id="reason" name="reason" class="form-control" rows="5" maxlength="2000"></textarea>
      </div>
      <button type="submit" class="btn btn-danger">Pokračovat k potvrzení</button>
      <a href="/clenska-sekce/moje-objednavky" class="btn btn-link">Zpět</a>
    </form>
  </div></div>
</div>

</@layout.page>
