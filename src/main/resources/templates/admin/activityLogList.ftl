<#import "/adminLayout.ftl" as layout>
<#import "/pagination.ftl" as pag>
<@layout.page>

<div class="mb-4">
    <h2>User Activity Logs</h2>
    <p class="text-muted">Track user actions including logins, orders, product downloads, and member section activities.</p>
</div>

<!-- Filters -->
<div class="card mb-4">
    <div class="card-header">
        <h5 class="mb-0">Filters</h5>
    </div>
    <div class="card-body">
        <form method="GET" action="/admin/activity-logs">
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="userId" class="form-label">User ID</label>
                    <input type="text" class="form-control" id="userId" name="userId" value="${selectedUserId}" placeholder="Enter user ID">
                </div>
                <div class="col-md-3">
                    <label for="actionType" class="form-label">Action Type</label>
                    <select class="form-select" id="actionType" name="actionType">
                        <option value="">All Actions</option>
                        <#list actionTypes as type>
                            <option value="${type}" <#if selectedActionType == type>selected</#if>>${type}</option>
                        </#list>
                    </select>
                </div>
                <div class="col-md-3">
                    <label for="entityType" class="form-label">Entity Type</label>
                    <select class="form-select" id="entityType" name="entityType">
                        <option value="">All Entities</option>
                        <#list entityTypes as type>
                            <option value="${type}" <#if selectedEntityType == type>selected</#if>>${type}</option>
                        </#list>
                    </select>
                </div>
                <div class="col-md-3">
                    <label for="startDate" class="form-label">Start Date</label>
                    <input type="date" class="form-control" id="startDate" name="startDate" value="${selectedStartDate}">
                </div>
                <div class="col-md-3">
                    <label for="endDate" class="form-label">End Date</label>
                    <input type="date" class="form-control" id="endDate" name="endDate" value="${selectedEndDate}">
                </div>
                <div class="col-md-6 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary me-2">Apply Filters</button>
                    <a href="/admin/activity-logs" class="btn btn-secondary">Clear Filters</a>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Results Summary -->
<div class="d-flex justify-content-between align-items-center mb-3">
    <div>
        <strong>Total entries:</strong> ${resultPage.pagination.totalCount!0}
    </div>
    <div>
        <span class="text-muted">Showing ${(resultPage.pagination.offset + 1)} - ${(resultPage.pagination.offset + resultPage.records?size)} of ${resultPage.pagination.totalCount!0}</span>
    </div>
</div>

<!-- Activity Log Table -->
<table class="table table-sm table-hover admin-table">
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
    <tr>
        <td colspan="7" class="text-center text-muted py-4">
            No activity log entries found. Try adjusting your filters.
        </td>
    </tr>
</#if>
    </tbody>
</table>

<@pag.pagination resultPage.pagination />

</@layout.page>
