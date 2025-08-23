<#--
Responsive image macro for optimized loading across devices
Parameters:
- imageName: filename of the image
- alt: alt text for accessibility
- cssClass: CSS class for styling (optional)
- loading: loading attribute (lazy by default)
- aspectRatio: aspect ratio for CLS prevention (8:7 by default)
-->
<#macro responsiveImage imageName="" alt="" cssClass="" loading="lazy">
<#if imageName?? && imageName?length gt 0>
<img src="/media/${imageName}?size=${xlat['article.img.tablet.width']}" 
     srcset="/media/${imageName}?size=${xlat['article.img.mobile.width']} ${xlat['article.img.mobile.width']}w,
             /media/${imageName}?size=${xlat['article.img.tablet.width']} ${xlat['article.img.tablet.width']}w,
             /media/${imageName}?size=${xlat['article.img.desktop.width']} ${xlat['article.img.desktop.width']}w"
     sizes="(max-width: 768px) ${xlat['article.img.mobile.width']}px, 
            (max-width: 1024px) ${xlat['article.img.tablet.width']}px, 
            ${xlat['article.img.desktop.width']}px"
     alt="${alt}"
     class="${cssClass} responsive-image-aspect-ratio responsive-image-width-height"
     loading="${loading}" />
</#if>
</#macro>

<#--
Article card responsive image - optimized for article cards
-->
<#macro articleCardImage imageName="" alt="" cssClass="article-image">
<#if imageName?? && imageName?length gt 0>
<img src="/media/${imageName}?size=${xlat['article.img.mobile.width']}" 
     srcset="/media/${imageName}?size=${xlat['article.img.mobile.width']} ${xlat['article.img.mobile.width']}w,
             /media/${imageName}?size=${xlat['article.img.tablet.width']} ${xlat['article.img.tablet.width']}w"
     sizes="(max-width: 768px) 100vw, 400px"
     alt="${alt}"
     class="${cssClass} responsive-image-width-height"
     loading="lazy"
     width="400" height="350" />
<#else>
<img src="https://images.unsplash.com/photo-1520206183501-b80df61043c2?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80" 
     alt="${alt}"
     class="${cssClass} responsive-image-width-height"
     loading="lazy"
     width="400" height="350" />
</#if>
</#macro>

<#--
Article detail responsive image - optimized for article detail pages
-->
<#macro articleDetailImage imageName="" alt="" cssClass="article-image-detail">
<#if imageName?? && imageName?length gt 0>
<#if userAccessReport?? && userAccessReport.mobileDevice>
<img src="/media/${imageName}?size=${xlat['article.img.mobile.width']}" 
     srcset="/media/${imageName}?size=${xlat['article.img.mobile.width']} ${xlat['article.img.mobile.width']}w,
             /media/${imageName}?size=${xlat['article.img.tablet.width']} ${xlat['article.img.tablet.width']}w"
     sizes="100vw"
     alt="${alt}"
     class="${cssClass} responsive-image-width-height"
     loading="lazy"
     width="400" height="400" />
<#else>
<img src="/media/${imageName}?size=${xlat['article.img.tablet.width']}" 
     srcset="/media/${imageName}?size=${xlat['article.img.mobile.width']} ${xlat['article.img.mobile.width']}w,
             /media/${imageName}?size=${xlat['article.img.tablet.width']} ${xlat['article.img.tablet.width']}w,
             /media/${imageName}?size=${xlat['article.img.desktop.width']} ${xlat['article.img.desktop.width']}w"
     sizes="(max-width: 768px) 100vw, 800px"
     alt="${alt}"
     class="${cssClass} responsive-image-width-height"
     loading="lazy"
     width="800" height="800" />
</#if>
</#if>
</#macro>

<#--
Small thumbnail responsive image - for sidebars and small cards
-->
<#macro thumbnailImage imageName="" alt="" size="${xlat['article.img.thumbnail.width']}" cssClass="rounded">
<#if imageName?? && imageName?length gt 0>
<img src="/media/${imageName}?size=${size}" 
     alt="${alt}" 
     class="${cssClass} object-fit-cover"
     width="${size}" 
     height="${size}" 
     loading="lazy" />
<#else>
<img src="https://images.unsplash.com/photo-1520206183501-b80df61043c2?ixlib=rb-4.0.3&auto=format&fit=crop&w=${size}&q=80" 
     alt="${alt}" 
     class="${cssClass} object-fit-cover"
     width="${size}" 
     height="${size}" 
     loading="lazy" />
</#if>
</#macro>
