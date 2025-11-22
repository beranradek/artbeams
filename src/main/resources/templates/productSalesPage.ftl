<#assign pageStyles = "/static/css/articles.css">
<#assign pageStyles2 = "/static/css/sales-styles.css">
<#import "/newWebLayout.ftl" as layout>
<#import "/components/testimonialCarousel.ftl" as testimonials>
<#import "/components/benefitsGrid.ftl" as benefits>
<#import "/components/faqAccordion.ftl" as faq>
<#import "/components/imageGallery.ftl" as gallery>
<#import "/components/authorSection.ftl" as author>

<@layout.page pageStyles=pageStyles pageStyles2=pageStyles2>

<div class="min-h-screen bg-background">
  <h1>${product.title!}</h1>
  
  <!-- Hero Section -->
  <section class="sales-hero-section">
    <div class="sales-hero-container">
      <!-- E-book Cover - Left Side -->
      <div class="sales-hero-image">
        <div class="relative">
          <img 
            src="${product.image!}"
            alt="E-book ${product.title!}"
            class="w-full max-w-md h-auto"
          />
        </div>
      </div>

      <!-- Hero Content - Right Side -->
      <div class="sales-hero-content">
        <#--
        <div class="inline-block px-4 py-2 bg-gradient-peaceful text-primary font-semibold rounded-full">
          E-book pro kvalitní spánek
        </div>
        -->
        <h2>Zbavte se nekvalitního spánku a získejte zpět svou vitalitu!</h2>
        <ul class="space-y-4 text-xl text-primary-muted">
          <li>Proč se každé ráno cítíte unavení, i když spíte celých osm hodin?</p>
          <li>Stává se vám, že večer nemůžete usnout, i když jste vyčerpaní?</p>
          <li>Máte pocit, že spánek vás neosvěží a zůstáváte vyčerpaní?</p>
        </ul>
        <div class="flex flex-col sm:flex-row gap-4 centered">
          <p class="offer-highlight text-xl">Připravuji pro Vás nový e-book. Už brzy. Prozatím děkuji za strpení. Máte se na co těšit!</p>
          <#-- <a href="#order" class="btn-purchase">
            Koupit e-book &gt;&gt;
          </a>
          -->
        </div>
      </div>
    </div>
  </section>

  <!-- Parallax Image Section 1 -->
  <section 
    class="parallax-section parallax-bg-starry"
  >
    <div class="parallax-overlay"></div>
    <div class="parallax-content h-full flex items-center justify-center">
      <div class="text-center text-white mt-48">
        <h2 class="parallax-header text-4xl font-bold text-white">Najděte zpět svůj klidný spánek</h2>
        <p class="parallax-text text-xl opacity-90">Každá noc může být regenerující a osvěžující</p>
      </div>
    </div>
  </section>

  <!-- Problem Section -->
  <section class="py-20 px-4 bg-background">
    <div class="max-w-4xl mx-auto space-y-8">
      <h2>Zbavte se únavy a získejte zpět svůj život!</h2>
      <div class="text-lg text-foreground-muted space-y-6 leading-relaxed">
        <p>
        Rozumím, jaké to je – ležet v posteli a přešlapovat mezi únavou a neklidem.
        Trápit se tím, že necítím ten vytoužený klid a energii.
        I já jsem si dlouho myslel, že se s tím nedá nic dělat. Že spánek je prostě "takový, jaký je",
        a že únava je přirozenou součástí života.
        </p>
        <p>
        Ale pak jsem pochopil, že <strong>kvalita spánku není dílem náhody</strong> a častá únava opravdu není normální.
        Zjistil jsem, že malé změny v návycích, prostředí a způsobu, jakým přistupujeme ke spánku, mohou přinést velké výsledky.
        Postupně jsem si vytvořil rutinu, díky které dokážu snadno usnout, probouzet se svěží a cítit se po celý den plný energie.
        </p>
        <p>
        A co víc – tyto změny nebyly složité, ani mě nestály velké peníze nebo měsíce úsilí.
        Všechny tyto metody jsem si na vlastní kůži vyzkoušel, ověřil jejich účinnost a zjistil,
        co skutečně funguje. A vím, že to může fungovat i pro Vás. Stačí jen vědět, kde začít.
        Nemusíte žít v neustálém koloběhu únavy a podrážděnosti. Existuje způsob, jak spánek proměnit ve skutečný zdroj energie a zdraví. A já vám ukážu, jak na to.
        </p>
        <p>
          <strong>Nejste určitě sami, kdo řeší problémy s neuspokojujícím spánkem.</strong> Problémy s nekvalitním spánkem trápí až polovinu dospělé populace. Na první pohled to vypadá jako drobnost –
          prostě si lehnout a spát. Ale co když to nefunguje? Co když se každé ráno budíte unavení, vaše tělo neodpočívá, 
          a vy se ocitáte v nekonečném koloběhu stresu a únavy?
        </p>
        <p>
          Často si ani neuvědomujeme, že náš spánek řídí více než jen délka času, který strávíme v posteli. 
          Hraje zde roli to, jaké máme návyky, prostředí, ve kterém usínáme, nebo dokonce náš denní režim. 
          A pokud tyto klíčové faktory nejsou správně nastavené, nedokáže ani náš organismus využít regenerativní sílu spánku.
        </p>
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

  <section class="py-20 px-4 bg-background">
    <div class="max-w-4xl mx-auto space-y-8">
      <div class="text-lg text-foreground-muted space-y-6 leading-relaxed">
        <p class="text-xl font-semibold text-primary-custom">
          Možná si říkáte: „Spánek je přece přirozený – proč bych měl něco měnit?"
        </p>
        <p>
          A přesně v tom je problém. Dlouhodobě nekvalitní spánek vás stojí zdraví, energii i životní pohodu. 
          A přitom je řešení snazší, než si myslíte.
        </p>
        <p>
          Pokud vás už nebaví neustálý pocit vyčerpání a chcete konečně začít vstávat svěží a plní energie, čtěte dál. 
          Tento e-book vám ukáže cestu k hlubokému a kvalitnímu spánku, který si zasloužíte.
        </p>
        <p class="text-xl font-semibold text-primary-custom mt-8">
          Necháváte si ukrást zdraví kvůli nekvalitnímu spánku?
        </p>
        <p>
          Možná se ptáte, proč je spánek tak důležitý. Vždyť během něj vlastně "nic neděláme", že? 
          Opak je pravdou. Spánek je časem, kdy se vaše tělo i mysl obnovují, regenerují a připravují na další den.
        </p>
        <p>
          Když spíte špatně nebo málo, vaše tělo nemá čas na důležité procesy, jako je:
        </p>
        <ul class="text-left max-w-2xl mx-auto space-y-2">
          <li>Posílení imunitního systému</li>
          <li>Regenerace svalů a tkání</li>
          <li>Ukládání vzpomínek do dlouhodobé paměti</li>
          <li>Vylučování toxinů z mozku</li>
          <li>Regulace hormonů ovlivňujících hlad a metabolismus</li>
        </ul>
        <p class="text-xl font-semibold text-primary-custom mt-6">
          Co vás čeká, pokud nekvalitní spánek nevyřešíte?
        </p>
        <p>
          Chronický nedostatek kvalitního spánku má dlouhodobé důsledky, které se projevují nejen ráno, 
          ale ovlivňují celý Váš život:
        </p>
        <ul class="text-left max-w-2xl mx-auto space-y-2">
          <li>Oslabená imunita a častější nemoci</li>
          <li>Zhoršená koncentrace a paměť</li>
          <li>Vyšší riziko deprese a úzkosti</li>
          <li>Zvýšené riziko obezity a diabetu</li>
          <li>Problémy v partnerských vztazích</li>
          <li>Snížená produktivita v práci</li>
          <li>Předčasné stárnutí</li>
        </ul>
        <p>
          Ale není třeba se tím trápit. V e-booku vše spolu podchytíme a opečujeme tak, abyste měli dostatek energie na vše, co potřebujete a co vás baví.
        </p>
      </div>
    </div>
  </section>

  <@gallery.imageGallery [
    {"src": "sunset-gallery-1.jpg", "alt": "Západ slunce nad klidnou vodou"},
    {"src": "nature-gallery-2.jpg", "alt": "Poklidná lesní krajina v ranním světle"},
    {"src": "sleep-nature-3.jpg", "alt": "Klidný spánek v přírodě pod hvězdami"}
  ] />

  <@benefits.benefitsGrid benefits=[
    {
      "icon": "clock",
      "title": "Rychlé usínání",
      "description": "Už žádné převalování se a počítání oveček. Usnete snadno a rychle."
    },
    {
      "icon": "sun",
      "title": "Energie po celý den",
      "description": "Probudíte se svěží a plní energie, která vám vydrží až do večera."
    },
    {
      "icon": "heart",
      "title": "Lepší zdraví",
      "description": "Kvalitní spánek posiluje imunitu a zlepšuje celkové zdraví."
    }
  ] features=[
    "Konečně pořádně vyspíte a probudíte se plní energie. Už žádná ranní únava nebo pocit, že potřebujete další šálek kávy, abyste přežili den.",
    "Naučíte se, jak snadno usínat, i když máte stresující den. Získáte metody a techniky, které Vám pomohou rychle přepnout z hektického režimu do klidového stavu.",
    "Zlepšíte své zdraví a podpoříte regeneraci těla. Kvalitní spánek přispívá k účinnému imunitnímu systému, lepšímu metabolismu a zdravější pokožce.",
    "Zbavíte se nočních probouzení. Už vás nebudou rušit zbytečné myšlenky nebo špatné návyky, které kazí Váš odpočinek.",
    "Získáte jasný plán na přizpůsobení svého spánkového režimu. Všechny kroky jsou praktické a zvládnete je aplikovat bez složitých změn nebo drahých investic.",
    "Zvýšíte svou koncentraci a produktivitu během dne. Díky hlubokému spánku bude Váš mozek pracovat lépe, rychleji a efektivněji."
  ] />

  <!-- Parallax Image Section 2 -->
  <section 
    class="parallax-section parallax-bg-bedroom"
  >
    <div class="parallax-overlay"></div>
    <div class="parallax-content h-full flex items-center justify-center">
      <div class="text-center text-white mt-48">
        <h2 class="parallax-header text-4xl font-bold text-white">Každé ráno vstanete svěží</h2>
        <p class="parallax-text text-xl opacity-90">Prožijte rozdíl mezi skutečným odpočinkem a pouhým spánkem</p>
      </div>
    </div>
  </section>

  <!-- Visual Section -->
  <section class="py-20 px-4 bg-background">
    <div class="max-w-6xl mx-auto">
      <h2 class="text-4xl font-bold text-primary-custom text-center mb-16">Představte si, jaké to bude, až...</h2>
      
      <div class="grid lg:grid-cols-2 gap-12 items-center mb-16">
        <div class="space-y-6">
          <div class="flex items-start gap-4">
            <svg class="icon w-8 h-8 text-primary-custom mt-1"><use href="#icon-moon"></use></svg>
            <div>
              <h3 class="text-xl font-semibold text-primary-custom mb-2">Každé ráno vstanete svěží a odpočatí</h3>
              <p class="text-foreground-muted">
                Už žádné kruhy pod očima, nekonečné zívání nebo pocit, že noc byla krátká. Vaše tělo i mysl budou plné energie, připravené zvládnout jakýkoli den.
                Přestanete se cítit unavení a podráždění. Místo toho budete klidní, vyrovnaní a budete mít prostor věnovat se svým blízkým i svým snům s radostí a nadšením.
              </p>
            </div>
          </div>
          
          <div class="flex items-start gap-4">
            <svg class="icon w-8 h-8 text-secondary mt-1"><use href="#icon-sun"></use></svg>
            <div>
              <h3 class="text-xl font-semibold text-primary-custom mb-2">Budete konečně spát celou noc</h3>
              <p class="text-foreground-muted">
                Vaš spánek bude hluboký, regenerační a přinese vám vnitřní klid, po kterém jste vždy toužili.
              </p>
            </div>
          </div>
          
          <div class="flex items-start gap-4">
            <svg class="icon w-8 h-8 text-gentle-red mt-1"><use href="#icon-heart"></use></svg>
            <div>
              <h3 class="text-xl font-semibold text-primary-custom mb-2">Najdete zpět svou radost ze života</h3>
              <p class="text-foreground-muted">
                Když zmizí neustálá únava, otevře se vám svět plný nových příležitostí - budete mít sílu zkoušet nové věci,
                věnovat se svým koníčkům a cítit se dobře ve svém těle i mysli.
                Objevíte nový smysl pro rovnováhu a zdraví.
                Kvalitní spánek je základem zdravého těla i ducha.
                Budete se cítit silnější, zdravější a šťastnější.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <#--
  <@testimonials.testimonialCarousel [
    {
      "name": "Anna Svobodová",
      "text": "Díky tomuto e-booku konečně spím celou noc. Už tři měsíce se nebouzím unavená!",
      "photo": "testimonial-anna.jpg"
    },
    {
      "name": "Pavel Novák", 
      "text": "Praktické návody, které skutečně fungují. Můj spánek se zlepšil během týdne.",
      "photo": "testimonial-pavel.jpg"
    },
    {
      "name": "Marie Dvořáková",
      "text": "Jednoduché techniky, které lze snadno aplikovat. Energie během dne je neuvěřitelná.",
      "photo": "testimonial-marie.jpg"
    },
    {
      "name": "Tomáš Krejčí",
      "text": "Konečně mám energii na věci, které mě baví. Spánek se stal mým nejlepším přítelem.",
      "photo": "testimonial-anna.jpg"
    },
    {
      "name": "Jana Procházková",
      "text": "Myslela jsem, že špatný spánek je můj osud. Díky tomuto návodu vím, že se to dá změnit.",
      "photo": "testimonial-marie.jpg"
    }
  ] />
  -->

  <@faq.faqAccordion [
    {
      "question": "Co když tento e-book nebude fungovat i pro mě?",
      "answer": "Rozumím, že máte obavy, zda Vám informace v tomto e-booku opravdu pomohou. Techniky a návyky, které v něm najdete, jsou podložené vědeckými výzkumy, léty praxe a mým testováním. Pokud budete postupovat podle návodu, je velmi pravděpodobné, že pocítíte zlepšení. A pokud zjistíte, že Vám něco nefunguje, můžete se na mě obrátit s doplňující otázkou."
    },
    {
      "question": "Co když to nedokážu nebo se mi to nebude dařit?",
      "answer": "Vím, že změnit některé spánkové návyky může být možná náročné, ale s tímto e-bookem máte podporu krok za krokem. Každý postup je jednoduchý a praktický, takže i když to bude ze začátku někdy výzva, budete mít jasný plán, jak dosáhnout výsledků."
    },
    {
      "question": "Ale co když si najdu tyto informace zdarma jinde?",
      "answer": "Ano, řadu informací o spánku můžete najít i jinde. Ale v tomto e-booku je vše na jednom místě, konkrétní a praktické. Ušetříte si hodiny hledání, čtení článků a experimentování s různými metodami, které ve výsledku možná nebudou fungovat. Právě v tomto e-booku se podrobněji dozvíte, co funguje, a co ne. E-book vám poskytne ucelený přehled a efektivní kroky ke skutečnému zlepšení spánku. Investice do tohoto e-booku je investicí do Vaší pohody, zdraví a kvalitního spánku. A pokud byste nebyli spokojeni, vrátím Vám peníze jednoduše bez dotazů zpět."
    }
  ] />

  <@author.authorSection />

  <!-- Final CTA -->
  <#--
  <section class="py-20 px-4 bg-background" id="order">
    <div class="max-w-4xl mx-auto text-center">
      <div class="final-cta-card">
        <div class="text-center mb-8">
          <div class="price-display">${product.priceRegular.price} ${product.priceRegular.currency}</div>
          <p class="text-xl text-foreground-muted">Investice do Vašeho zdraví a pohody</p>
        </div>

        <div class="text-center text-sm text-foreground-muted space-y-2 mt-8">
          <p>30denní záruka vrácení peněz</p>
          <p>Okamžité doručení na email</p>
        </div>
      </div>
    </div>
  </section>
  -->

<section class="bg-background centered">
  <div class="centered">
    <p class="text-xl offer-highlight">
      <#-- Začněte svou cestu ke kvalitnímu spánku ještě dnes -->
      Připravuji pro Vás nový e-book. Už brzy. Prozatím děkuji za strpení. Máte se na co těšit!
    </p>
  </div>
</section>

</div>

</@layout.page>
