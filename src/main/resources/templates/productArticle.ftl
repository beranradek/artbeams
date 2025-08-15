<#assign pageStyles = "/static/css/sales-styles.css">
<#import "/newWebLayout.ftl" as layout>
<@layout.page pageStyles=pageStyles>

<h1 class="blog-post-title">${article.title!}</h1>

<div>${article.body!}</div>

</@layout.page>
