<#macro categoryBadge article featured=false>
<#-- Determine badge color based on category or default -->>
<#assign badgeClass = "badge bg-secondary-custom">
<#assign badgeText = "Blog">

<#if article.categories?? && article.categories?size > 0>
  <#assign category = article.categories[0]>
  <#assign badgeText = category.title>
  <#-- Assign colors based on category names or create a mapping -->>
  <#if category.title?lower_case?contains("spánek") || category.title?lower_case?contains("sleep")>
    <#assign badgeClass = "badge badge-bright-blue">
  <#elseif category.title?lower_case?contains("výživa") || category.title?lower_case?contains("stravování") || category.title?lower_case?contains("nutrition")>
    <#assign badgeClass = "badge badge-bright-yellow">
  <#elseif category.title?lower_case?contains("relaxace") || category.title?lower_case?contains("meditace") || category.title?lower_case?contains("relaxation")>
    <#assign badgeClass = "badge badge-bright-red">
  <#elseif category.title?lower_case?contains("režim") || category.title?lower_case?contains("routine")>
    <#assign badgeClass = "badge bg-secondary-custom">
  <#else>
    <#assign badgeClass = "badge bg-secondary-custom">
  </#if>
</#if>

<#if featured>
  <#assign badgeClass = "badge badge-bright-blue">
  <#assign badgeText = "Doporučujeme">
</#if>

<span class="${badgeClass}">${badgeText}</span>
</#macro>