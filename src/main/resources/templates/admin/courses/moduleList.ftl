<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Modules for course ${courseId}">
  <@c.newButton url="/admin/courses/${courseId}/modules/0/edit" label="New Module" />
</@c.pageHeader>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list modules as module>
    <tr>
      <td>${module.title}</td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.editLink url="/admin/courses/${courseId}/modules/${module.id}/edit" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=2 message="No modules." />
</#list>
  </tbody>
</@c.table>
</@layout.page>
