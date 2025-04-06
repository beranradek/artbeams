<#assign pageStyles = "/static/css/sales-styles.css">
<#assign pageStyles2 = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<#import "/webLayout.ftl" as layout>
<@layout.page pageStyles=pageStyles pageStyles2=pageStyles2>

<h1 class="blog-post-title">${article.title!}</h1>

<div>${article.body!}</div>

</@layout.page>
