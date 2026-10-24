<#-- Shared building blocks of administration pages. Styles are in /static/css/admin-ui.css (loaded by adminLayout.ftl only). -->

<#-- Inline icons from Lucide (https://lucide.dev, ISC license); no external requests, color follows the text color. -->
<#assign iconPaths = {
  "pencil": '<path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/>',
  "trash": '<path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/>',
  "plus": '<path d="M5 12h14"/><path d="M12 5v14"/>',
  "search": '<circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/>',
  "x": '<path d="M18 6 6 18"/><path d="m6 6 12 12"/>',
  "chevron-left": '<path d="m15 18-6-6 6-6"/>',
  "chevron-right": '<path d="m9 18 6-6-6-6"/>',
  "arrow-up": '<path d="m5 12 7-7 7 7"/><path d="M12 19V5"/>',
  "log-in": '<path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" x2="3" y1="12" y2="12"/>',
  "log-out": '<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" x2="9" y1="12" y2="12"/>',
  "user": '<circle cx="12" cy="12" r="10"/><circle cx="12" cy="10" r="3"/><path d="M7 20.662V19a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v1.662"/>',
  "refresh": '<path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/><path d="M21 3v5h-5"/><path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/><path d="M8 16H3v5"/>',
  "globe": '<circle cx="12" cy="12" r="10"/><path d="M12 2a14.5 14.5 0 0 0 0 20 14.5 14.5 0 0 0 0-20"/><path d="M2 12h20"/>',
  "external": '<path d="M15 3h6v6"/><path d="M10 14 21 3"/><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>',
  "ellipsis": '<circle cx="12" cy="12" r="1"/><circle cx="19" cy="12" r="1"/><circle cx="5" cy="12" r="1"/>'
}>

<#macro icon name>
<svg class="adm-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" focusable="false">${iconPaths[name]}</svg><#t>
</#macro>


<#-- Page title with optional total count; nested content (buttons) is placed on the right side. -->
<#macro pageHeader title count="" countLabel="">
<div class="admin-page-header">
  <h1 class="admin-title">${title}</h1>
  <#if count?has_content>
    <span class="admin-count" title="${countLabel}">${count}</span>
  </#if>
  <div class="admin-page-actions"><#nested></div>
</div>
</#macro>

<#-- Primary "create" button used in page header. -->
<#macro newButton url label>
<a class="btn btn-primary btn-sm" href="${url}"><@icon "plus"/> ${label}</a>
</#macro>

<#-- Compact one-line GET filter form. Nested content = fields (use filterField). -->
<#macro filterBar action clearUrl submitLabel="Filter">
<form method="GET" action="${action}" class="admin-filter">
  <#nested>
  <div class="admin-filter-buttons">
    <button type="submit" class="btn btn-primary btn-sm"><@icon "search"/> ${submitLabel}</button>
    <a href="${clearUrl}" class="btn btn-outline-secondary btn-sm" title="Clear filters"><@icon "x"/> Clear</a>
  </div>
</form>
</#macro>

<#-- Single labeled field of filterBar; grow=true makes the field take all remaining width. -->
<#macro filterField label forId grow=false>
<div class="admin-filter-field<#if grow> admin-filter-grow</#if>">
  <label for="${forId}" class="form-label">${label}</label>
  <#nested>
</div>
</#macro>

<#-- Responsive data table. Nested content = thead and tbody. -->
<#macro table class="">
<div class="admin-table-wrap">
  <table class="table table-sm admin-table ${class}">
    <#nested>
  </table>
</div>
</#macro>

<#macro emptyRow colspan message="No records found.">
<tr><td colspan="${colspan}" class="admin-empty">${message}</td></tr>
</#macro>

<#-- Container of icon actions in a table row. -->
<#macro rowActions>
<div class="admin-row-actions"><#nested></div>
</#macro>

<#macro editLink url title="Edit">
<a href="${url}" class="admin-icon-btn" title="${title}" aria-label="${title}"><@icon "pencil"/></a>
</#macro>

<#-- POST form with an icon-only submit button; method override (_method) and confirmation are optional. -->
<#macro iconForm url iconName title confirm="" method="" danger=false>
<form action="${url}" method="POST" class="admin-icon-form"<#if confirm?has_content> data-confirm="${confirm?html}"</#if>>
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <#if method?has_content><input type="hidden" name="_method" value="${method}"/></#if>
  <button type="submit" class="admin-icon-btn<#if danger> admin-icon-danger</#if>" title="${title}" aria-label="${title}"><@icon iconName/></button>
</form>
</#macro>

<#macro deleteForm url confirm method="">
<@iconForm url=url iconName="trash" title="Delete" confirm=confirm method=method danger=true />
</#macro>

<#-- Pagination with first/last page, window around current page and summary. additionalParams keeps active filters. -->
<#macro pagination pagination additionalParams="">
<#if pagination.pageCount gt 1>
<#assign current = pagination.page>
<#assign last = pagination.pageCount>
<#assign base = "?limit=${pagination.limit}${additionalParams}&offset=">
<nav class="admin-pagination" aria-label="Pagination">
  <ul class="pagination pagination-sm mb-0">
    <li class="page-item<#if pagination.isFirstPage()> disabled</#if>">
      <a class="page-link" href="${base}${pagination.previousOffset}" aria-label="Previous page"><@icon "chevron-left"/></a>
    </li>
    <#list 1..last as p>
      <#if p == 1 || p == last || (p >= current - 2 && p <= current + 2)>
        <li class="page-item<#if p == current> active</#if>"<#if p == current> aria-current="page"</#if>>
          <a class="page-link" href="${base}${(p - 1) * pagination.limit}">${p}</a>
        </li>
      <#elseif p == current - 3 || p == current + 3>
        <li class="page-item disabled"><span class="page-link">&hellip;</span></li>
      </#if>
    </#list>
    <li class="page-item<#if pagination.isLastPage()> disabled</#if>">
      <a class="page-link" href="${base}${pagination.nextOffset}" aria-label="Next page"><@icon "chevron-right"/></a>
    </li>
  </ul>
  <#if pagination.totalCount??>
    <span class="admin-pagination-summary">${pagination.offset + 1}&ndash;${[pagination.offset + pagination.limit, pagination.totalCount]?min} of ${pagination.totalCount}</span>
  </#if>
</nav>
</#if>
</#macro>
