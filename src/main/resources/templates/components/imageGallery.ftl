<#macro imageGallery galleryImages>
<!-- Image Gallery Section -->
<section class="py-0 px-0 bg-background">
  <div class="w-full">
    <div class="image-gallery-grid gap-3 bg-white">
      <#list galleryImages as image>
        <div class="relative overflow-hidden group">
          <img 
            src="/static/images/${image.src}" 
            alt="${image.alt}"
            class="w-full h-64 object-cover transition-transform duration-300 group-hover:scale-105"
          />
        </div>
      </#list>
    </div>
  </div>
</section>
</#macro>
