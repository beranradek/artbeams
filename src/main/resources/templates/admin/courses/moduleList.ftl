<#import "/adminLayout.ftl" as layout>
<@layout.page>
  <h1>Modules for course ${courseId}</h1>
  <a href="/admin/courses/${courseId}/modules/0/edit" class="btn btn-primary">New Module</a>
  <#if modules?has_content>
    <ul>
      <#list modules as m>
        <li>${m.title} - <a href="/admin/courses/${courseId}/modules/${m.id}/edit">Edit</a></li>
      </#list>
    </ul>
  <#else>
    <p>No modules.</p>
  </#if>
</@layout.page>
