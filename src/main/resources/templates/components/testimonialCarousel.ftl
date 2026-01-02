<#macro testimonialCarousel testimonials>
<!-- Testimonials (reviews) -->
<section class="py-3 px-4 bg-gradient-peaceful">
  <div class="max-w-6xl mx-auto">
    <div class="text-center mb-16">
      <h2 class="text-4xl font-bold text-primary-custom mb-4">Co říkají naši čtenáři</h2>
      <#--
      <div class="flex items-center justify-center gap-2 mb-6">
        <span class="text-xl text-primary-muted">Více než 1 000 spokojených čtenářů</span>
      </div>
      -->
    </div>
    
    <div class="testimonial-container">
      <div id="testimonial-carousel" class="testimonial-carousel">
        <#list testimonials as testimonial>
          <div class="testimonial-item text-center" data-index="${testimonial?index}">
            <img 
              src="/static/images/${testimonial.photo}" 
              alt="${testimonial.name}"
              class="testimonial-avatar w-20 h-20 rounded-full mx-auto object-cover shadow-peaceful mb-4"
            />
            <p class="font-semibold text-primary-custom mb-4 text-lg">${testimonial.name}</p>
            <div class="speech-bubble">
              <p class="text-foreground-muted italic text-lg text-left">"${testimonial.text}"</p>
            </div>
          </div>
        </#list>
      </div>
      
      <!-- Navigation dots -->
      <div class="flex justify-center mt-8 gap-3">
        <#assign totalPages = ((testimonials?size + 1) / 2)?floor>
        <#list 0..<totalPages as pageIndex>
          <button
            class="testimonial-dot w-3 h-3 rounded-full transition-colors duration-300 <#if pageIndex == 0>active</#if>"
            data-page="${pageIndex}"
            aria-label="Přejít na skupinu recenzí ${pageIndex + 1}"
          ></button>
        </#list>
      </div>
    </div>
  </div>
</section>

<script nonce="${_cspNonce}">
document.addEventListener('DOMContentLoaded', function() {
  const carousel = document.getElementById('testimonial-carousel');
  const dots = document.querySelectorAll('.testimonial-dot');
  const testimonialsPerPage = 2;
  let currentPage = 0;
  let autoRotateTimer;
  
  function showTestimonials(pageIndex) {
    const items = carousel.querySelectorAll('.testimonial-item');
    items.forEach((item, index) => {
      const startIndex = pageIndex * testimonialsPerPage;
      const endIndex = startIndex + testimonialsPerPage;
      
      if (index >= startIndex && index < endIndex) {
        item.style.display = 'block';
      } else {
        item.style.display = 'none';
      }
    });
    
    // Update dots
    dots.forEach((dot, index) => {
      if (index === pageIndex) {
        dot.classList.add('active');
      } else {
        dot.classList.remove('active');
      }
    });
    
    currentPage = pageIndex;
  }
  
  function startAutoRotate() {
    // Clear existing timer if any
    if (autoRotateTimer) {
      clearInterval(autoRotateTimer);
    }
    
    // Start new auto-rotate timer
    autoRotateTimer = setInterval(() => {
      const nextPage = (currentPage + 1) % dots.length;
      showTestimonials(nextPage);
    }, 30000);
  }
  
  // Add click handlers to dots
  dots.forEach((dot, index) => {
    dot.addEventListener('click', () => {
      showTestimonials(index);
      startAutoRotate(); // Reset timer on user interaction
    });
  });
  
  // Initialize
  showTestimonials(0);
  startAutoRotate();
});
</script>
</#macro>
