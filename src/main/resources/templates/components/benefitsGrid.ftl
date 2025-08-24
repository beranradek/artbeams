<#macro benefitsGrid benefits features>
<!-- Benefits Section -->
<section class="py-20 px-4 bg-background">
  <div class="max-w-6xl mx-auto">
    <div class="text-center mb-16">
      <h2 class="text-4xl font-bold text-primary mb-6">Co vám e-book přinese</h2>
      <p class="text-xl text-primary-muted">Konkrétní kroky k lepšímu spánku a kvalitnějšímu životu</p>
    </div>
    
    <div class="grid md:grid-cols-3 gap-8 mb-16">
      <#list benefits as benefit>
        <div class="benefit-card p-8 text-center hover:shadow-peaceful transition-shadow duration-300">
          <div class="benefit-icon-wrapper flex justify-center mb-4">
            <i class="${benefit.icon} text-4xl text-primary"></i>
          </div>
          <h3 class="text-2xl font-semibold text-primary mb-4">${benefit.title}</h3>
          <p class="text-foreground-muted">${benefit.description}</p>
        </div>
      </#list>
    </div>

    <div class="features-card p-8">
      <h3 class="text-2xl font-bold text-primary mb-6">Co konkrétně získáte:</h3>
      <div class="grid md:grid-cols-2 gap-4">
        <#list features as feature>
          <div class="flex items-start gap-3">
            <i class="bi bi-check-circle text-2xl text-calm-green mt-1"></i>
            <p class="text-foreground-muted">${feature}</p>
          </div>
        </#list>
      </div>
    </div>
  </div>
</section>
</#macro>