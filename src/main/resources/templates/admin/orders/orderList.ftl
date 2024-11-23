<#import "/adminLayout.ftl" as layout>
<@layout.page>

<table class="table table-sm">
  <thead>
    <tr>
      <th scope="col">Order time</th>
      <th scope="col">User login</th>
      <th scope="col">User name</th>
      <th scope="col">Products</th>
    </tr>
  </thead>
  <tbody>
<#list orders as order>
    <tr>
        <td>${order.orderTime?string["d.M.yyyy, HH:mm"]}</td>
        <td><#if order.createdBy??>${order.createdBy.login}</#if></td>
        <td><#if order.createdBy??>${order.createdBy.name}</#if></td>
        <td>
            <ul>
            <#list order.items as item>
                <li>${item.productName} x ${item.quantity}</li>
            </#list>
            </ul>
        </td>
    </tr>
</#list>
  </tbody>
</table>
</@layout.page>
