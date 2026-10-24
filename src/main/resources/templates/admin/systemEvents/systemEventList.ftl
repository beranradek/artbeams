<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="System Events" />

<@c.filterBar action="/admin/system-events" clearUrl="/admin/system-events">
  <@c.filterField label="Severity" forId="severity">
    <select name="severity" id="severity" class="form-select">
      <option value="">(any)</option>
      <#list severities as s>
        <option value="${s}" <#if selectedSeverity == s>selected</#if>>${s}</option>
      </#list>
    </select>
  </@c.filterField>
  <@c.filterField label="Event type" forId="eventType" grow=true>
    <select name="eventType" id="eventType" class="form-select">
      <option value="">(any)</option>
      <#list eventTypes as t>
        <option value="${t}" <#if selectedEventType == t>selected</#if>>${t}</option>
      </#list>
    </select>
  </@c.filterField>
  <@c.filterField label="From" forId="startDate">
    <input type="date" class="form-control" id="startDate" name="startDate" value="${selectedStartDate}"/>
  </@c.filterField>
  <@c.filterField label="To" forId="endDate">
    <input type="date" class="form-control" id="endDate" name="endDate" value="${selectedEndDate}"/>
  </@c.filterField>
</@c.filterBar>

<#-- resultPage.data can be null on empty results; treat as empty list -->
<#assign entries = (resultPage.data)![]>
<@c.table>
  <thead>
    <tr>
      <th scope="col">Time</th>
      <th scope="col">Severity</th>
      <th scope="col">Type</th>
      <th scope="col">Message</th>
      <th scope="col">User</th>
      <th scope="col">Entity</th>
    </tr>
  </thead>
  <tbody>
  <#list entries as e>
    <tr>
      <td class="text-nowrap">${e.eventTime?string["yyyy-MM-dd HH:mm:ss"]}</td>
      <td>${e.severity}</td>
      <td>${e.eventType}</td>
      <td>
        <div><strong>${e.message?html}</strong></div>
        <#if e.origin??><div class="text-muted small">Origin: ${e.origin?html}</div></#if>
        <#if e.ipAddress??><div class="text-muted small">IP: ${e.ipAddress?html}</div></#if>
        <#if e.details??><details class="mt-1"><summary>details</summary><pre class="mt-2">${e.details?html}</pre></details></#if>
        <#if e.stackTrace??><details class="mt-1"><summary>stack trace</summary><pre class="mt-2">${e.stackTrace?html}</pre></details></#if>
      </td>
      <td>${e.userId!""}</td>
      <td><#if e.entityType??>${e.entityType}:${e.entityId!""}</#if></td>
    </tr>
  <#else>
    <@c.emptyRow colspan=6 message="No events found." />
  </#list>
  </tbody>
</@c.table>
</@layout.page>
