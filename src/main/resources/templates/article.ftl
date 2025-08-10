<#import "/newWebLayout.ftl" as layout>
<#import "/author.ftl" as author>
<#import "/metadata.ftl" as metadata>
<#import "/socialShare.ftl" as socialShare>
<#-- <#import "/fbComments.ftl" as fbComments> -->
<#import "/comments/commentAdd.ftl" as commentAdd>
<#import "/comments/commentList.ftl" as commentList>
<@layout.page pageStyles="/static/css/articles.css">
    <h1 class="blog-post-title">${article.title!}</h1>

    <#if userAccessReport?? && userAccessReport.mobileDevice && article.image??>
      <div class="article-image-detail">
        <img alt="" src="/media/${article.image}?size=${xlat['article.img.small.width']}" />
      </div>
    <#else>
      <#if article.image??>
        <div class="article-image-detail">
          <img alt="" src="/media/${article.image}?size=${xlat['article.img.big.width']}" />
        </div>
      </#if>
    </#if>

    <#if article.showOnBlog>
      <@metadata.metadata></@metadata.metadata>
      <@socialShare.share></@socialShare.share>
    </#if>
    <div>${article.body!}</div>
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
