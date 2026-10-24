<#import "/adminLayout.ftl" as layout>
<#import "/adminComponents.ftl" as c>
<@layout.page>

<@c.pageHeader title="Courses">
  <@c.newButton url="/admin/courses/0/edit" label="New Course" />
</@c.pageHeader>

<@c.table>
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col" class="admin-col-actions">Actions</th>
    </tr>
  </thead>
  <tbody>
<#list courses as course>
    <tr>
      <td>${course.title}</td>
      <td class="admin-col-actions">
        <@c.rowActions>
          <@c.editLink url="/admin/courses/${course.id}/edit" />
        </@c.rowActions>
      </td>
    </tr>
<#else>
    <@c.emptyRow colspan=2 message="No courses found." />
</#list>
  </tbody>
</@c.table>
</@layout.page>
