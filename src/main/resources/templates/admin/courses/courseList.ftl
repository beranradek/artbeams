<#import "/adminLayout.ftl" as layout>
<@layout.page>
  <h1>Courses</h1>
  <a href="/admin/courses/0/edit" class="btn btn-primary">New Course</a>
  <#if courses?has_content>
    <ul>
      <#list courses as c>
        <li>${c.title} - <a href="/admin/courses/${c.id}/edit">Edit</a></li>
      </#list>
    </ul>
  <#else>
    <p>No courses found.</p>
  </#if>
</@layout.page>
