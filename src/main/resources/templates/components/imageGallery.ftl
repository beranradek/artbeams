<#macro imageGallery galleryImages>
<!-- Image Gallery Section -->
<section class="py-0 px-0 bg-background">
  <div class="w-full">
    <div class="image-gallery-grid gap-1 bg-white">
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

<!-- Bedroom Gallery Section -->
<section class="py-20 px-4 bg-gradient-peaceful">
  <div class="max-w-6xl mx-auto">
    <div class="grid md:grid-cols-2 gap-8 mb-16">
      <img 
        src="/static/images/cozy-bedroom.jpg" 
        alt="Útulná ložnice s kvalitním spánkem"
        class="rounded-2xl shadow-peaceful w-full"
      />
      <img 
        src="/static/images/luxury-bedding.jpg" 
        alt="Luxusní ložní prádlo pro kvalitní spánek"
        class="rounded-2xl shadow-peaceful w-full"
      />
    </div>
  </div>
</section>
</#macro>