<#assign pageStyles = "/static/css/articles.css">
<#assign pageStyles2 = "/static/css/sales-styles.css">
<#import "/newWebLayout.ftl" as layout>
<@layout.page pageStyles=pageStyles pageStyles2=pageStyles2>

<h1 class="blog-post-title">${article.title!}</h1>

<div>${article.body!}</div>

</@layout.page>
