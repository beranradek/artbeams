<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Consents" count="${resultPage.pagination.totalCount!0}" countLabel="Total consents" />

<@c.table>
  <thead>
    <tr>
      <th scope="col">Login</th>
      <th scope="col">Valid From</th>
      <th scope="col">Valid To</th>
      <th scope="col">Consent Type</th>
      <th scope="col">Product ID</th>
    </tr>
  </thead>
  <tbody>
<#list resultPage.records as consent>
    <tr>
      <td>${consent.login}</td>
      <td>${consent.validFrom?string["d.M.yyyy, HH:mm"]}</td>
      <td>${consent.validTo?string["d.M.yyyy, HH:mm"]}</td>
      <td>${consent.consentType}</td>
      <td>${consent.originProductId!"-"}</td>
    </tr>
<#else>
    <@c.emptyRow colspan=5 />
</#list>
  </tbody>
</@c.table>

<@c.pagination pagination=resultPage.pagination />

</@layout.page>
