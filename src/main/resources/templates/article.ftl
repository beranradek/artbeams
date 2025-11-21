<#import "/newWebLayout.ftl" as layout>
<#import "/author.ftl" as author>
<#import "/metadata.ftl" as metadata>
<#import "/socialShare.ftl" as socialShare>
<#import "/components/responsiveImage.ftl" as img>
<#-- <#import "/fbComments.ftl" as fbComments> -->
<#import "/comments/commentAdd.ftl" as commentAdd>
<#import "/comments/commentList.ftl" as commentList>
<@layout.page pageStyles="/static/css/articles.css">
    <article itemscope itemtype="https://schema.org/BlogPosting">
        <header>
            <h1 class="blog-post-title" itemprop="headline">${article.title!}</h1>

            <#if article.image??>
              <figure itemprop="image" itemscope itemtype="https://schema.org/ImageObject">
                <div class="article-image-detail">
                  <@img.articleDetailImage imageName=article.image alt=article.title />
                </div>
                <meta itemprop="url" content="${_urlBase}/media/${article.image}" />
                <meta itemprop="width" content="${xlat['article.img.tablet.width']}" />
              </figure>
            </#if>

            <#if article.showOnBlog>
              <div class="metadata row">
                <div>
                  <span itemprop="author" itemscope itemtype="https://schema.org/Person">
                    <a href="/muj-pribeh" itemprop="url">
                      <svg xmlns="http://www.w3.org/2000/svg" height="14px" viewBox="0 0 448 512"><!--! user-solid Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. --><style nonce="${_cspNonce}">svg{fill:#949494}</style><path d="M224 256A128 128 0 1 0 224 0a128 128 0 1 0 0 256zm-45.7 48C79.8 304 0 383.8 0 482.3C0 498.7 13.3 512 29.7 512H418.3c16.4 0 29.7-13.3 29.7-29.7C448 383.8 368.2 304 269.7 304H178.3z"/></svg>&nbsp;<span itemprop="name">${xlat['author.name']}</span>
                    </a>
                  </span>
                  <time class="article-date" itemprop="datePublished" datetime="${article.validFrom?datetime?iso_utc}">
                    ${article.validFrom?string["d.M.yyyy, HH:mm"]}
                  </time>
                  <#if article.modified?long != article.validFrom?long>
                    <time itemprop="dateModified" datetime="${article.modified?datetime?iso_utc}" class="display-none">
                      ${article.modified?string["d.M.yyyy, HH:mm"]}
                    </time>
                  </#if>
                  <#if countOfVisits??>&nbsp;&nbsp;<svg xmlns="http://www.w3.org/2000/svg" height="14px" viewBox="0 0 576 512"><!--! eye-solid Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. --><style nonce="${_cspNonce}">svg{fill:#949494}</style><path d="M288 32c-80.8 0-145.5 36.8-192.6 80.6C48.6 156 17.3 208 2.5 243.7c-3.3 7.9-3.3 16.7 0 24.6C17.3 304 48.6 356 95.4 399.4C142.5 443.2 207.2 480 288 480s145.5-36.8 192.6-80.6c46.8-43.5 78.1-95.4 93-131.1c3.3-7.9 3.3-16.7 0-24.6c-14.9-35.7-46.2-87.7-93-131.1C433.5 68.8 368.8 32 288 32zM144 256a144 144 0 1 1 288 0 144 144 0 1 1 -288 0zm144-64c0 35.3-28.7 64-64 64c-7.1 0-13.9-1.2-20.3-3.3c-5.5-1.8-11.9 1.6-11.7 7.4c.3 6.9 1.3 13.8 3.2 20.7c13.7 51.2 66.4 81.6 117.6 67.9s81.6-66.4 67.9-117.6c-11.1-41.5-47.8-69.4-88.6-71.1c-5.8-.2-9.2 6.1-7.4 11.7c2.1 6.4 3.3 13.2 3.3 20.3z"/></svg>&nbsp;${countOfVisits}x</#if>
                  &nbsp;&nbsp;<@socialShare.shareLink></@socialShare.shareLink>
                </div>
              </div>
            </#if>
        </header>

        <#if article.perex??>
          <div itemprop="description" class="article-perex display-none">
            ${article.perex}
          </div>
        </#if>

        <div itemprop="articleBody" class="article-body">
            ${article.body!}
        </div>

        <#-- Hidden metadata for schema.org -->
        <meta itemprop="inLanguage" content="cs-CZ" />
        <#if articleCategories?? && articleCategories?size gt 0>
          <meta itemprop="articleSection" content="${articleCategories[0].title}" />
        </#if>
        <link itemprop="mainEntityOfPage" href="${_urlBase}/${article.slug}" />
        <span itemprop="publisher" itemscope itemtype="https://schema.org/Organization" class="display-none">
          <span itemprop="name">${xlat['website.title']}</span>
          <link itemprop="url" href="${_urlBase}" />
          <#if xlat['logo.img.src']?? || xlat['favicon.img.src']??>
          <span itemprop="logo" itemscope itemtype="https://schema.org/ImageObject">
            <link itemprop="url" href="${_urlBase}<#if xlat['logo.img.src']??>${xlat['logo.img.src']}<#else>${xlat['favicon.img.src']}</#if>" />
          </span>
          </#if>
        </span>

        <#if article.showOnBlog>
          <footer>
            <@socialShare.share></@socialShare.share>
            <#-- <@fbComments.fbComments></@fbComments.fbComments> -->
            <@commentList.commentList title="${article.title!}"></@commentList.commentList>
            <#if commentForm??>
              <@commentAdd.commentAdd></@commentAdd.commentAdd>
            </#if>
            <@author.author></@author.author>
          </footer>
        </#if>
    </article>
</@layout.page>
