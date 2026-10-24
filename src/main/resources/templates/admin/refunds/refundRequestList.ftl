<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Žádosti o vrácení objednávek">
  <a href="/admin/orders" class="btn btn-outline-secondary btn-sm">Zpět na objednávky</a>
</@c.pageHeader>

<#if refundRequests?size == 0>
  <div class="alert alert-success" role="status">Žádné žádosti nečekají na vyřízení.</div>
<#else>
  <p class="text-muted">Před potvrzením ověř, že peníze byly vráceny mimo aplikaci.</p>
  <@c.table>
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
  </@c.table>
</#if>

</@layout.page>
