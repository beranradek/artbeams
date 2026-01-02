<#macro faqAccordion faqItems>
<!-- FAQ Section -->
<section class="py-4 px-4 bg-background">
  <div class="max-w-4xl mx-auto">
    <h2 class="text-4xl font-bold text-primary-custom text-center mb-16">Nejčastější dotazy</h2>
    
    <div class="faq-accordion space-y-4">
      <#list faqItems as item>
        <div class="faq-item border border-border rounded-lg overflow-hidden">
          <button class="faq-question w-full text-left px-6 py-4 bg-background hover:bg-muted transition-colors duration-200" 
                  data-faq-toggle="${item?index}">
            <div class="flex justify-between items-center">
              <span class="text-lg font-semibold text-primary-custom">"${item.question}"</span>
              &nbsp;<svg class="icon faq-chevron text-primary-custom transition-transform duration-200"><use href="#icon-chevron-down"></use></svg>
            </div>
          </button>
          <div class="faq-answer px-6 py-0 max-h-0 overflow-hidden transition-all duration-300" 
               id="faq-answer-${item?index}">
            <div class="pb-4">
              <p class="text-foreground-muted leading-relaxed pt-2">${item.answer}</p>
            </div>
          </div>
        </div>
      </#list>
    </div>
  </div>
</section>

<script nonce="${_cspNonce}">
document.addEventListener('DOMContentLoaded', function() {
  const faqToggles = document.querySelectorAll('[data-faq-toggle]');
  
  faqToggles.forEach(toggle => {
    toggle.addEventListener('click', function() {
      const index = this.getAttribute('data-faq-toggle');
      const answer = document.getElementById('faq-answer-' + index);
      const chevron = this.querySelector('.faq-chevron');
      const isOpen = answer.style.maxHeight && answer.style.maxHeight !== '0px';
      
      // Close all other FAQ items
      faqToggles.forEach(otherToggle => {
        if (otherToggle !== this) {
          const otherIndex = otherToggle.getAttribute('data-faq-toggle');
          const otherAnswer = document.getElementById('faq-answer-' + otherIndex);
          const otherChevron = otherToggle.querySelector('.faq-chevron');
          
          otherAnswer.style.maxHeight = '0px';
          otherChevron.style.transform = 'rotate(0deg)';
        }
      });
      
      // Toggle current FAQ item
      if (isOpen) {
        answer.style.maxHeight = '0px';
        chevron.style.transform = 'rotate(0deg)';
      } else {
        answer.style.maxHeight = answer.scrollHeight + 'px';
        chevron.style.transform = 'rotate(180deg)';
      }
    });
  });
});
</script>
</#macro>
