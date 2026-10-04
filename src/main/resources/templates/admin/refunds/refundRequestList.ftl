<#import "/adminLayout.ftl" as layout>
<@layout.page>

<h1>Žádosti o vrácení objednávek</h1>

<#if refundRequests?size == 0>
  <div class="alert alert-success" role="status">Žádné žádosti nečekají na vyřízení.</div>
<#else>
  <p class="text-muted">Před potvrzením ověř, že peníze byly vráceny mimo aplikaci.</p>
  <table class="table table-sm admin-table">
    <thead><tr><th>Objednávka</th><th>Zákazník</th><th>Podáno</th><th>Důvod</th><th></th></tr></thead>
    <tbody>
      <#list refundRequests as refundRequest>
        <#assign order = ordersById[refundRequest.orderId]>
        <tr>
          <td>${order.orderNumber}</td>
          <td><#if order.createdBy??>${order.createdBy.login}</#if></td>
          <td>${refundRequest.requestedAt?string["d.M.yyyy, HH:mm"]}</td>
          <td><#if refundRequest.reason??>${refundRequest.reason?html}<#else><em>Neuveden</em></#if></td>
          <td><a class="btn btn-sm btn-outline-primary" href="/admin/refund-requests/${refundRequest.id}">Otevřít</a></td>
        </tr>
      </#list>
    </tbody>
  </table>
</#if>

<a href="/admin/orders" class="btn btn-secondary">Zpět na objednávky</a>
</@layout.page>
