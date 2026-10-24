<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="User Activity Logs" count="${resultPage.pagination.totalCount!0}" countLabel="Total entries" />

<@c.filterBar action="/admin/activity-logs" clearUrl="/admin/activity-logs">
  <@c.filterField label="User ID" forId="userId" grow=true>
    <input type="text" class="form-control" id="userId" name="userId" value="${selectedUserId}" placeholder="User ID">
  </@c.filterField>
  <@c.filterField label="Action" forId="actionType">
    <select class="form-select" id="actionType" name="actionType">
      <option value="">All actions</option>
      <#list actionTypes as type>
        <option value="${type}" <#if selectedActionType == type>selected</#if>>${type}</option>
      </#list>
    </select>
  </@c.filterField>
  <@c.filterField label="Entity" forId="entityType">
    <select class="form-select" id="entityType" name="entityType">
      <option value="">All entities</option>
      <#list entityTypes as type>
        <option value="${type}" <#if selectedEntityType == type>selected</#if>>${type}</option>
      </#list>
    </select>
  </@c.filterField>
  <@c.filterField label="From" forId="startDate">
    <input type="date" class="form-control" id="startDate" name="startDate" value="${selectedStartDate}">
  </@c.filterField>
  <@c.filterField label="To" forId="endDate">
    <input type="date" class="form-control" id="endDate" name="endDate" value="${selectedEndDate}">
  </@c.filterField>
</@c.filterBar>

<!-- Activity Log Table -->
<@c.table>
    <thead>
        <tr>
            <th scope="col" style="width: 180px">Time</th>
            <th scope="col" style="width: 150px">User ID</th>
            <th scope="col" style="width: 150px">Action</th>
            <th scope="col" style="width: 120px">Entity Type</th>
            <th scope="col" style="width: 150px">Entity ID</th>
            <th scope="col">Details</th>
            <th scope="col" style="width: 120px">IP Address</th>
        </tr>
    </thead>
    <tbody>
<#if resultPage.records?has_content>
    <#list resultPage.records as log>
        <tr>
            <td><#if log.actionTime??>${log.actionTime?string["d.M.yyyy, HH:mm:ss"]}</#if></td>
            <td>
                <span class="text-truncate d-inline-block" style="max-width: 150px" title="${log.userId}">
                    ${log.userId}
                </span>
            </td>
            <td>
                <#if log.actionType.value == "LOGIN">
                    <span class="badge bg-success">LOGIN</span>
                <#elseif log.actionType.value == "LOGOUT">
                    <span class="badge bg-secondary">LOGOUT</span>
                <#elseif log.actionType.value == "ORDER_CREATED">
                    <span class="badge bg-info">ORDER_CREATED</span>
                <#elseif log.actionType.value == "PAYMENT_CONFIRMED">
                    <span class="badge bg-primary">PAYMENT_CONFIRMED</span>
                <#elseif log.actionType.value == "PRODUCT_DOWNLOADED">
                    <span class="badge bg-warning text-dark">PRODUCT_DOWNLOADED</span>
                <#else>
                    <span class="badge bg-light text-dark">${log.actionType.value}</span>
                </#if>
            </td>
            <td><#if log.entityType??>${log.entityType.value}<#else>-</#if></td>
            <td>
                <#if log.entityId??>
                    <span class="text-truncate d-inline-block" style="max-width: 150px" title="${log.entityId}">
                        ${log.entityId}
                    </span>
                <#else>
                    -
                </#if>
            </td>
            <td>
                <#if log.details??>
                    <span class="text-truncate d-inline-block" style="max-width: 300px" title="${log.details}">
                        ${log.details}
                    </span>
                <#else>
                    -
                </#if>
            </td>
            <td><#if log.ipAddress??>${log.ipAddress}<#else>-</#if></td>
        </tr>
    </#list>
<#else>
    <@c.emptyRow colspan=7 message="No activity log entries found. Try adjusting your filters." />
</#if>
    </tbody>
</@c.table>

<@c.pagination resultPage.pagination />

</@layout.page>
