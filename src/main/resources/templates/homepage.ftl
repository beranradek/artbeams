<#import "/webLayout.ftl" as layout>
<#import "/articleCard.ftl" as articleCard>
<@layout.page>

<!-- Benefits Section -->
<section class="py-5 bg-accent-custom">
  <div class="container">
    <div class="text-center mb-5">
      <h2 class="display-5 fw-bold mb-3">Proč se zaměřit na kvalitní spánek?</h2>
      <p class="lead text-secondary">Spánek není jen odpočinek – je to základní stavební kámen vašeho zdraví, který ovlivňuje každý aspekt vašeho života.</p>
    </div>
    <div class="row g-4">
      <div class="col-md-3">
        <div class="p-4 bg-white rounded-4 shadow-sm text-center h-100">
          <i class="bi bi-moon-stars fs-1 text-primary-custom mb-3"></i>
          <h3 class="h5 mb-2">Lepší kvalita spánku</h3>
          <p class="text-muted">Osvědčené metody pro hlubší a kvalitnější spánek, který vás skutečně zregeneruje.</p>
        </div>
      </div>
      <div class="col-md-3">
        <div class="p-4 bg-white rounded-4 shadow-sm text-center h-100">
          <i class="bi bi-calendar2-week fs-1 text-secondary-custom mb-3"></i>
          <h3 class="h5 mb-2">Pravidelný rytmus</h3>
          <p class="text-muted">Nastavte si zdravý denní režim, který respektuje přirozené biologické cykly vašeho těla.</p>
        </div>
      </div>
      <div class="col-md-3">
        <div class="p-4 bg-white rounded-4 shadow-sm text-center h-100">
          <i class="bi bi-person-heart fs-1 text-primary-custom mb-3"></i>
          <h3 class="h5 mb-2">Osobní přístup</h3>
          <p class="text-muted">Individuální doporučení přizpůsobená vašim potřebám a životnímu stylu.</p>
        </div>
      </div>
      <div class="col-md-3">
        <div class="p-4 bg-white rounded-4 shadow-sm text-center h-100">
          <i class="bi bi-globe2 fs-1 text-secondary-custom mb-3"></i>
          <h3 class="h5 mb-2">Holistický přístup</h3>
          <p class="text-muted">Spojujeme moderní vědecké poznatky s tradičními přístupy pro komplexní péči o vaše zdraví.</p>
        </div>
      </div>
    </div>
    <div class="row mt-5 justify-content-center">
      <div class="col-md-8">
        <div class="p-4 rounded-4 bg-gradient" style="background: linear-gradient(90deg, var(--accent), var(--secondary));">
          <div class="row align-items-center">
            <div class="col-md-8">
              <h3 class="h5 mb-2 text-primary-custom">Až 70% dospělých se potýká s problémy se spánkem</h3>
              <p class="mb-0">Nedostatek kvalitního spánku může vést k mnoha zdravotním problémům. Investice do dobrého spánku je investicí do vašeho zdraví a dlouhověkosti.</p>
            </div>
            <div class="col-md-2 text-center">
              <span class="badge badge-bright-yellow fs-4">68%</span>
              <div class="small">má problémy se spánkem</div>
            </div>
            <div class="col-md-2 text-center">
              <span class="badge badge-bright-blue fs-4">33%</span>
              <div class="small">trpí nespavostí</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- Featured Content Section -->
<section class="py-5">
  <div class="container">
    <div class="text-center mb-5">
      <h2 class="display-5 fw-bold mb-3">Naše nejnovější články</h2>
      <p class="lead text-secondary">Inspirace a odborné znalosti pro váš zdravější životní styl a lepší spánek.</p>
    </div>
    <div class="row g-4">
      <#list articles as article>
        <div class="col-md-4">
          <@articleCard.articleCard article=article />
        </div>
      </#list>
    </div>
    <div class="text-center mt-5">
      <a href="/" class="btn btn-outline-secondary btn-lg rounded-pill px-5">Zobrazit všechny články</a>
    </div>
  </div>
</section>

<!-- Testimonials Section -->
<section class="py-5 bg-accent-custom">
  <div class="container">
    <div class="text-center mb-5">
      <h2 class="display-5 fw-bold mb-3">Co říkají naši čtenáři</h2>
      <p class="lead text-secondary">Přečtěte si příběhy lidí, kterým jsme pomohli zlepšit kvalitu spánku a celkový životní styl.</p>
    </div>
    <div class="row g-4 justify-content-center">
      <div class="col-md-4">
        <div class="p-4 bg-white rounded-4 shadow-sm h-100">
          <div class="d-flex align-items-center mb-3">
            <img src="https://i.pravatar.cc/150?img=5" alt="Jana Nováková" class="rounded-circle me-3" width="48" height="48" />
            <div>
              <strong>Jana Nováková</strong><br />
              <span class="text-muted small">Učitelka, 42 let</span>
            </div>
          </div>
          <p class="fst-italic mb-0">"Díky radám z Vysněné Zdraví se mi podařilo zlepšit kvalitu spánku o 80%. Konečně se cítím odpočatá a plná energie."</p>
        </div>
      </div>
      <div class="col-md-4">
        <div class="p-4 bg-white rounded-4 shadow-sm h-100">
          <div class="d-flex align-items-center mb-3">
            <img src="https://i.pravatar.cc/150?img=12" alt="Petr Svoboda" class="rounded-circle me-3" width="48" height="48" />
            <div>
              <strong>Petr Svoboda</strong><br />
              <span class="text-muted small">IT specialista, 38 let</span>
            </div>
          </div>
          <p class="fst-italic mb-0">"Po letech problémů s usínáním jsem konečně našel způsob, jak se kvalitně vyspat. Změna je naprosto zásadní."</p>
        </div>
      </div>
      <div class="col-md-4">
        <div class="p-4 bg-white rounded-4 shadow-sm h-100">
          <div class="d-flex align-items-center mb-3">
            <img src="https://i.pravatar.cc/150?img=9" alt="Alena Dvořáková" class="rounded-circle me-3" width="48" height="48" />
            <div>
              <strong>Alena Dvořáková</strong><br />
              <span class="text-muted small">Zdravotní sestra, 45 let</span>
            </div>
          </div>
          <p class="fst-italic mb-0">"Jako zdravotník vím, jak je spánek důležitý. Vysněné Zdraví mi pomohlo nastavit ideální spánkový režim."</p>
        </div>
      </div>
    </div>
    <div class="text-center mt-5">
      <a href="https://www.vysnenezdravi.cz/" target="_blank" rel="noopener noreferrer" class="btn btn-primary-custom btn-lg rounded-pill px-5">Navštívit náš web</a>
    </div>
  </div>
</section>

</@layout.page>
