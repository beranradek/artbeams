<#import "/adminLayout.ftl" as layout>
<@layout.page>

<div class="mb-3">
  <a href="/admin/orders/create" class="btn btn-primary">New Order</a>
</div>

<table class="table table-sm admin-table">
  <thead>
    <tr>
      <th scope="col">Order number</th>
      <th scope="col">Order time</th>
      <th scope="col">User</th>
      <th scope="col">Products</th>
      <th scope="col">Price</th>
      <th scope="col">State</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list orders as order>
    <tr>
        <td>${order.orderNumber}</td>
        <td>${order.orderTime?string["d.M.yyyy, HH:mm"]}</td>
        <td>
            <#if order.createdBy??>
                ${order.createdBy.login}
                <#if order.createdBy.name??>
                    <br/>${order.createdBy.name}
                </#if>
            </#if>
        </td>
        <td>
            <ul>
            <#list order.items as item>
                <li>${item.productName} x ${item.quantity}
                    <#if item.downloaded??>
                        <br/>Downloaded: ${item.downloaded?string["d.M.yyyy, HH:mm"]}
                    </#if>
                </li>
            </#list>
            </ul>
        </td>
        <td>${order.price}</td>
        <td>
            <form action="/admin/orders/${order.id}/state" method="POST">
                <select name="state" onchange="this.form.submit()">
                    <#list orderStates as state>
                        <option value="${state}"<#if state == order.state> selected</#if>>${state}</option>
                    </#list>
                </select>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </td>
        <td>
            <form action="/admin/orders/${order.id}" method="POST" onsubmit="return window.confirm('Are you sure you want to delete this order?');">
                <input type="hidden" name="_method" value="DELETE"/>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn">Delete</button>
            </form>
        </td>
    </tr>
</#list>
  </tbody>
</table>
</@layout.page>
