<#import "/adminLayout.ftl" as layout>
<@layout.page>

<h1>Order ${order.orderNumber}</h1>

<div class="row mb-4">
  <div class="col-md-6">
    <h3>Order Information</h3>
    <table class="table">
      <tr>
        <th>Order Number:</th>
        <td>${order.orderNumber}</td>
      </tr>
      <tr>
        <th>Order Time:</th>
        <td>${order.orderTime?string["d.M.yyyy, HH:mm"]}</td>
      </tr>
      <tr>
        <th>User:</th>
        <td>
          <#if order.createdBy??>
            ${order.createdBy.login}
            <#if order.createdBy.name??>
              <br/>${order.createdBy.name}
            </#if>
          </#if>
        </td>
      </tr>
      <tr>
        <th>State:</th>
        <td>
          <form action="/admin/orders/${order.id}/state" method="POST">
            <select name="state" onchange="this.form.submit()" class="form-control">
              <#list orderStates as state>
                <option value="${state}"<#if state == order.state> selected</#if>>${state}</option>
              </#list>
            </select>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          </form>
        </td>
      </tr>
      <tr>
        <th>Price:</th>
        <td>${order.price}</td>
      </tr>
      <tr>
        <th>Paid Time:</th>
        <td>
          <#if order.paidTime??>
            ${order.paidTime?string["d.M.yyyy, HH:mm:ss"]}
          <#else>
            <em>Not paid yet</em>
          </#if>
        </td>
      </tr>
      <tr>
        <th>Payment Method:</th>
        <td>
          <#if order.paymentMethod??>
            ${order.paymentMethod}
          <#else>
            <em>Not specified</em>
          </#if>
        </td>
      </tr>
    </table>
  </div>

  <div class="col-md-6">
    <h3>Order Items</h3>
    <table class="table">
      <thead>
        <tr>
          <th>Product</th>
          <th>Quantity</th>
          <th>Downloaded</th>
        </tr>
      </thead>
      <tbody>
        <#list order.items as item>
          <tr>
            <td>${item.productName}</td>
            <td>${item.quantity}</td>
            <td>
              <#if item.downloaded??>
                ${item.downloaded?string["d.M.yyyy, HH:mm"]}
              <#else>
                -
              </#if>
            </td>
          </tr>
        </#list>
      </tbody>
    </table>
  </div>
</div>

<div class="row">
  <div class="col-md-12">
    <h3>Admin Notes</h3>
    <form action="/admin/orders/${order.id}/notes" method="POST">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <div class="form-group">
        <textarea name="notes" class="form-control" rows="5" placeholder="Add admin notes here...">${order.notes!}</textarea>
      </div>
      <button type="submit" class="btn btn-primary">Save Notes</button>
      <a href="/admin/orders" class="btn btn-secondary">Back to List</a>
    </form>
  </div>
</div>

</@layout.page>
