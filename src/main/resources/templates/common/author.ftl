<#import "/layouts/webLayout.ftl" as layout>
<@layout.page>
    <h1 class="blog-post-title">${article.title}</h1>
    <div>${article.body}</div>
</@layout.page>
