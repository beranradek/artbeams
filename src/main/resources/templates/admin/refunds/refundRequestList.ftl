<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Refund Requests">
  <a href="/admin/orders" class="btn btn-outline-secondary btn-sm">Back to orders</a>
</@c.pageHeader>

<#if refundRequests?size == 0>
  <div class="alert alert-success" role="status">No refund requests are waiting.</div>
<#else>
  <p class="text-muted">Before confirming, check that the money was refunded outside of the application.</p>
  <@c.table>
    <thead><tr><th>Order</th><th>Customer</th><th>Requested</th><th>Reason</th><th></th></tr></thead>
    <tbody>
      <#list refundRequests as refundRequest>
        <#assign order = ordersById[refundRequest.orderId]>
        <tr>
          <td>${order.orderNumber}</td>
          <td><#if order.createdBy??>${order.createdBy.login}</#if></td>
          <td>${refundRequest.requestedAt?string["d.M.yyyy, HH:mm"]}</td>
          <td><#if refundRequest.reason??>${refundRequest.reason?html}<#else><em>Not specified</em></#if></td>
          <td><a class="btn btn-sm btn-outline-primary" href="/admin/refund-requests/${refundRequest.id}">Open</a></td>
        </tr>
      </#list>
    </tbody>
  </@c.table>
</#if>

</@layout.page>
