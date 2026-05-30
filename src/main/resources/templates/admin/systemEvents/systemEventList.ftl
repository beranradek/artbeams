<#import "/adminLayout.ftl" as layout>
<@layout.page>
<h1>System Events</h1>

<form method="GET" action="/admin/system-events" class="row g-3 mb-4">
  <div class="col-md-2">
    <label class="form-label">Severity</label>
    <select name="severity" class="form-select">
      <option value="">(any)</option>
      <#list severities as s>
        <option value="${s}" <#if selectedSeverity == s>selected</#if>>${s}</option>
      </#list>
    </select>
  </div>
  <div class="col-md-4">
    <label class="form-label">Event type</label>
    <select name="eventType" class="form-select">
      <option value="">(any)</option>
      <#list eventTypes as t>
        <option value="${t}" <#if selectedEventType == t>selected</#if>>${t}</option>
      </#list>
    </select>
  </div>
  <div class="col-md-2">
    <label class="form-label">Start date</label>
    <input type="date" class="form-control" name="startDate" value="${selectedStartDate}"/>
  </div>
  <div class="col-md-2">
    <label class="form-label">End date</label>
    <input type="date" class="form-control" name="endDate" value="${selectedEndDate}"/>
  </div>
  <div class="col-md-2 d-flex align-items-end">
    <button type="submit" class="btn btn-primary me-2">Filter</button>
    <a href="/admin/system-events" class="btn btn-secondary">Clear</a>
  </div>
</form>

<#-- resultPage.data can be null on empty results; treat as empty list -->
<#assign entries = (resultPage.data)![]>
<#if entries?has_content>
  <div class="table-responsive">
    <table class="table table-striped table-hover">
      <thead>
      <tr>
        <th>Time</th>
        <th>Severity</th>
        <th>Type</th>
        <th>Message</th>
        <th>User</th>
        <th>Entity</th>
      </tr>
      </thead>
      <tbody>
      <#list entries as e>
        <tr>
          <td>${e.eventTime?string["yyyy-MM-dd HH:mm:ss"]}</td>
          <td>${e.severity}</td>
          <td>${e.eventType}</td>
          <td>
            <div><strong>${e.message?html}</strong></div>
            <#if e.origin??><div class="text-muted">Origin: ${e.origin?html}</div></#if>
            <#if e.ipAddress??><div class="text-muted">IP: ${e.ipAddress?html}</div></#if>
            <#if e.details??><details class="mt-1"><summary>details</summary><pre class="mt-2">${e.details?html}</pre></details></#if>
            <#if e.stackTrace??><details class="mt-1"><summary>stack trace</summary><pre class="mt-2">${e.stackTrace?html}</pre></details></#if>
          </td>
          <td>${e.userId!""}</td>
          <td><#if e.entityType??>${e.entityType}:${e.entityId!""}</#if></td>
        </tr>
      </#list>
      </tbody>
    </table>
  </div>
<#else>
  <p class="text-muted">No events found.</p>
</#if>
</@layout.page>
