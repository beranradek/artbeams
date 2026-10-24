<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Orders" count="${resultPage.pagination.totalCount!0}" countLabel="Total orders">
  <a href="/admin/refund-requests" class="btn btn-outline-warning btn-sm">Refund requests <span class="badge text-bg-warning">${openRefundRequestCount!0}</span></a>
  <@c.newButton url="/admin/orders/create" label="New Order" />
</@c.pageHeader>

<@c.filterBar action="/admin/orders" clearUrl="/admin/orders" submitLabel="Search">
  <@c.filterField label="Search" forId="searchInput" grow=true>
    <input type="text" class="form-control" id="searchInput" name="search" value="${searchTerm!""}"
           placeholder="Order number, user login or name" title="Searches in order number, user login, first name and last name">
  </@c.filterField>
  <@c.filterField label="State" forId="stateFilter">
    <select class="form-select" id="stateFilter" name="state">
      <option value="">All states</option>
      <#list orderStates as state>
        <option value="${state}" <#if stateFilter?? && stateFilter == state>selected</#if>>${state}</option>
      </#list>
    </select>
  </@c.filterField>
</@c.filterBar>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Order number</th>
      <th scope="col">Order time</th>
      <th scope="col">User</th>
      <th scope="col">Products</th>
      <th scope="col">Price</th>
      <th scope="col">State</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as order>
    <tr>
      <td>${order.orderNumber}</td>
      <td>${order.orderTime?string["d.M.yyyy, HH:mm"]}</td>
      <td>
        <#if order.createdBy??>
          ${order.createdBy.login}
          <#if order.createdBy.name??><div class="text-muted small">${order.createdBy.name}</div></#if>
        </#if>
      </td>
      <td>
        <ul>
        <#list order.items as item>
          <li>${item.productName} x ${item.quantity}
            <#if item.downloaded??><div class="text-muted small">Downloaded: ${item.downloaded?string["d.M.yyyy, HH:mm"]}</div></#if>
          </li>
        </#list>
        </ul>
      </td>
      <td class="text-nowrap">${order.price}</td>
      <td>
        <#if order.state.name() == "REFUNDED">
          <span class="badge text-bg-warning">REFUNDED</span>
        <#else>
          <form action="/admin/orders/${order.id}/state" method="POST">
            <select name="state" class="form-select form-select-sm admin-state-select" aria-label="Order state">
              <#list orderStates as state>
                <option value="${state}"<#if state == order.state.name()> selected</#if>>${state}</option>
              </#list>
            </select>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          </form>
        </#if>
      </td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.editLink url="/admin/orders/${order.id}" />
          <@c.deleteForm url="/admin/orders/${order.id}" method="DELETE" confirm="Are you sure you want to delete this order?" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=7 />
</#list>
  </tbody>
</@c.table>

<#-- Preserve search and filter parameters in pagination -->
<#assign additionalParams = "">
<#if searchTerm?has_content>
  <#assign additionalParams = additionalParams + "&search=" + searchTerm?url>
</#if>
<#if stateFilter?has_content>
  <#assign additionalParams = additionalParams + "&state=" + stateFilter?url>
</#if>
<@c.pagination resultPage.pagination additionalParams />

</@layout.page>
