<#import "/newWebLayout.ftl" as layout>
<#import "/author.ftl" as author>
<#import "/metadata.ftl" as metadata>
<#import "/socialShare.ftl" as socialShare>
<#import "/components/responsiveImage.ftl" as img>
<#-- <#import "/fbComments.ftl" as fbComments> -->
<#import "/comments/commentAdd.ftl" as commentAdd>
<#import "/comments/commentList.ftl" as commentList>
<@layout.page pageStyles="/static/css/articles.css">
    <h1 class="blog-post-title">${article.title!}</h1>

    <#if article.image??>
      <div class="article-image-detail">
        <@img.articleDetailImage imageName=article.image alt=article.title />
      </div>
    </#if>

    <#if article.showOnBlog>
      <@metadata.metadata></@metadata.metadata>
      <@socialShare.share></@socialShare.share>
    </#if>
    <div class="article-body">${article.body!}</div>
    <#if article.showOnBlog>
      <@socialShare.share></@socialShare.share>
      <#-- <@fbComments.fbComments></@fbComments.fbComments> -->
      <@commentList.commentList title="${article.title!}"></@commentList.commentList>
      <#if commentForm??>
        <@commentAdd.commentAdd></@commentAdd.commentAdd>
      </#if>
      <@author.author></@author.author>
    </#if>
</@layout.page>
