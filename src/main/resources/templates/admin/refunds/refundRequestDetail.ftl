<#import "/adminLayout.ftl" as layout>
<@layout.page>

<h1>Refund request for order ${order.orderNumber}</h1>
<dl class="row">
  <dt class="col-sm-3">Customer</dt><dd class="col-sm-9"><#if order.createdBy??>${order.createdBy.login}</#if></dd>
  <dt class="col-sm-3">Requested</dt><dd class="col-sm-9">${refundRequest.requestedAt?string["d.M.yyyy, HH:mm"]}</dd>
  <dt class="col-sm-3">Reason</dt><dd class="col-sm-9" style="white-space: pre-wrap;"><#if refundRequest.reason??>${refundRequest.reason?html}<#else><em>Not specified</em></#if></dd>
  <dt class="col-sm-3">Order state</dt><dd class="col-sm-9">${order.state}</dd>
</dl>

<#if refundRequest.status.name() == "REQUESTED">
  <div class="alert alert-warning">Confirm only after the money was actually refunded outside of the application. This action revokes download access.</div>
  <form method="post" action="/admin/refund-requests/${refundRequest.id}/refund" data-confirm="Confirm the refund and revoke access to the products?">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit" class="btn btn-danger">Confirm refund</button>
    <a href="/admin/refund-requests" class="btn btn-outline-secondary">Back</a>
  </form>
<#else>
  <div class="alert alert-success">Request was resolved ${refundRequest.resolvedAt?string["d.M.yyyy, HH:mm"]}.</div>
  <a href="/admin/refund-requests" class="btn btn-outline-secondary">Back</a>
</#if>
</@layout.page>
