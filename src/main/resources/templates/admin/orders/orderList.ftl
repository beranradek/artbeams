<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pag>
<@layout.page>

<div class="mb-3">
  <a href="/admin/orders/create" class="btn btn-primary">New Order</a>
  <strong>Total orders:</strong> ${resultPage.pagination.totalCount!0}
</div>

<!-- Search and Filter Form -->
<div class="card mb-3">
  <div class="card-body">
    <form method="GET" action="/admin/orders" class="row g-3">
      <div class="col-md-5">
        <label for="searchInput" class="form-label">Search</label>
        <input type="text" class="form-control" id="searchInput" name="search"
               placeholder="Search by order number, user login, or user name..."
               value="${searchTerm}">
      </div>
      <div class="col-md-3">
        <label for="stateFilter" class="form-label">State</label>
        <select class="form-select" id="stateFilter" name="state">
          <option value="">All States</option>
          <#list orderStates as state>
            <option value="${state}" <#if stateFilter == state>selected</#if>>${state}</option>
          </#list>
        </select>
      </div>
      <div class="col-md-4 d-flex align-items-end gap-2">
        <button type="submit" class="btn btn-primary">Apply Filters</button>
        <a href="/admin/orders" class="btn btn-secondary">Clear</a>
      </div>
    </form>
    <small class="text-muted mt-2 d-block">Search searches in order number, user login, first name, and last name</small>
  </div>
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
<#list resultPage.records as order>
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
            <a href="/admin/orders/${order.id}" class="btn btn-sm">Edit</a>
            <form action="/admin/orders/${order.id}" method="POST" onsubmit="return window.confirm('Are you sure you want to delete this order?');" style="display:inline;">
                <input type="hidden" name="_method" value="DELETE"/>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-sm">Delete</button>
            </form>
        </td>
    </tr>
</#list>
  </tbody>
</table>

<#-- Preserve search and filter parameters in pagination -->
<#assign additionalParams = "">
<#if searchTerm?has_content>
  <#assign additionalParams = additionalParams + "&search=" + searchTerm?url>
</#if>
<#if stateFilter?has_content>
  <#assign additionalParams = additionalParams + "&state=" + stateFilter?url>
</#if>
<@pag.pagination resultPage.pagination additionalParams />

</@layout.page>
