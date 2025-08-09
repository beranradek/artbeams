<#import "/webLayout.ftl" as layout>
<#import "/author.ftl" as author>
<#import "/metadata.ftl" as metadata>
<#import "/socialShare.ftl" as socialShare>
<#-- <#import "/fbComments.ftl" as fbComments> -->
<#import "/comments/commentAdd.ftl" as commentAdd>
<#import "/comments/commentList.ftl" as commentList>
<@layout.page>
    <h1 class="display-4 fw-bold mb-4 text-primary-custom">${article.title!}</h1>

    <#if userAccessReport?? && userAccessReport.mobileDevice && article.image??>
      <div class="article-image-detail mb-4">
        <img alt="" src="/media/${article.image}?size=${xlat['article.img.small.width']}" class="featured-image rounded-4 shadow-sm" />
      </div>
    <#else>
      <#if article.image??>
        <div class="article-image-detail mb-4">
          <img alt="" src="/media/${article.image}?size=${xlat['article.img.big.width']}" class="featured-image rounded-4 shadow-sm" />
        </div>
      </#if>
    </#if>

    <#if article.showOnBlog>
      <@metadata.metadata></@metadata.metadata>
      <@socialShare.share></@socialShare.share>
    </#if>
    <div class="fs-5 lh-lg">${article.body!}</div>
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
