<#import "/adminLayout.ftl" as layout>
<@layout.page>

<h1>Žádost o vrácení objednávky ${order.orderNumber}</h1>
<dl class="row">
  <dt class="col-sm-3">Zákazník</dt><dd class="col-sm-9"><#if order.createdBy??>${order.createdBy.login}</#if></dd>
  <dt class="col-sm-3">Podáno</dt><dd class="col-sm-9">${refundRequest.requestedAt?string["d.M.yyyy, HH:mm"]}</dd>
  <dt class="col-sm-3">Důvod</dt><dd class="col-sm-9" style="white-space: pre-wrap;"><#if refundRequest.reason??>${refundRequest.reason?html}<#else><em>Neuveden</em></#if></dd>
  <dt class="col-sm-3">Stav objednávky</dt><dd class="col-sm-9">${order.state}</dd>
</dl>

<#if refundRequest.status.name() == "REQUESTED">
  <div class="alert alert-warning">Potvrď až po skutečném vrácení peněz mimo aplikaci. Tato akce zruší přístup ke stažení.</div>
  <form method="post" action="/admin/refund-requests/${refundRequest.id}/refund" onsubmit="return window.confirm('Potvrdit vrácení peněz a odebrat přístup k produktům?');">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit" class="btn btn-danger">Potvrdit vrácení peněz</button>
    <a href="/admin/refund-requests" class="btn btn-secondary">Zpět</a>
  </form>
<#else>
  <div class="alert alert-success">Žádost byla vyřízena ${refundRequest.resolvedAt?string["d.M.yyyy, HH:mm"]}.</div>
  <a href="/admin/refund-requests" class="btn btn-secondary">Zpět</a>
</#if>
</@layout.page>
