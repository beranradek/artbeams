## Responsive images design

Chtěl bych zefektivnit zobrazování obrázků, aby se pro zařízení/obrazovky různých šířek volil nebo zobrazil v přehledu článků a detailu článku nejmenší obrázek se šířkou větší nebo rovnou zobrazované oblasti 
(tj. aby byl dostatečně kvalitní, zaplnil danou oblast a přitom nebyl načítaný zase nepřiměřeně veliký obrázek).

Chtěl bych pro články vytvářet/generovat obrázky ve 3 dostatečně velkých velikostech (šířkách), 
a s tímto přiměřeným množstvím variant tak základně pokrýt různé velikosti - např. mobilní zařízení, desktop, tablet.
Jsou 3 velikosti dostatečné?

Zároveň bych chtěl mít stránku výkonově optimalizovanou pro PageSpeed Insights - aby na stránkách pro aktuální šířku obrazovky zařízení nebyla:
- Nepřiměřená velikost obrázků	→ správně napsané sizes a srcset

A aby nebyly reportovány další nedostatky:
- Neurčené rozměry obrázků	→ width + height v HTML
- CLS kvůli obrázkům	→ width a height + loading="lazy"
- Načítání příliš velkých obrázků na mobilu

Tady je příklad HTML kódu pro obrázek v detailu článku, který bude správně reagovat na šířku obrazovky zařízení, zachová poměr stran a minimalizuje CLS i varování od PageSpeed:

<picture>
  <source
    media="(max-width: 600px)"
    srcset="
      obrazek-jarnio4.jpg 480w,
      obrazek-jarnio5.jpg 800w"
  >
  <source
    media="(min-width: 601px)"
    srcset="
      obrazek-jarnio5.jpg 800w,
      obrazek-jarnio6.jpg 1200w"
  >
  <img
    src="obrazek-jarnio5.jpg"
    width="800"
    height="533"
    sizes="(max-width: 600px) 100vw, 800px"
    alt="Jarní očista duše – motiv duše a jara"
    loading="lazy"
  />
</picture>

A k tomu CSS styl:

.article-detail picture,
.article-detail img {
  width: 100%;
  height: auto;
  display: block;
}

(jedná se jen o příklad, bude třeba využít komponenty a styly v mém projektu).

Plán implementace:

Krok 1: Rozšíření konfigurace velikostí

- Přidat nové velikosti do insert_localisation.sql:
    - article.img.mobile.width=400
    - article.img.tablet.width=800
    - article.img.desktop.width=1200

Krok 2: Automatické generování variant při uploadu

- Rozšířit MediaController pro generování všech 3 variant současně
- Upravit MediaRepository.storeFile() pro batch generování
- Vytvořit helper metodu pro generování předem definovaných velikostí

Krok 3: Responzivní HTML templaty

- Převést <img> tagy na srcset a sizes ve všech šablonách
- Přidat width a height atributy pro prevenci CLS
- Implementovat loading="lazy" pro optimalizaci výkonu

Krok 4: Příklad responzivního img tagu:

<img src="/media/${article.image}?size=800"
srcset="/media/${article.image}?size=400 400w,
/media/${article.image}?size=800 800w,
/media/${article.image}?size=1200 1200w"
sizes="(max-width: 768px) 400px,
(max-width: 1024px) 800px,
1200px"
width="800" height="450"
loading="lazy"
alt="${article.title}" />

Krok 5: CSS optimalizace

- Zajistit správné aspect-ratio pro všechny velikosti
- Přidat object-fit: cover pro zachování poměru stran

Krok 6: PageSpeed optimalizace

- Testovat všechny metriky (LCP, CLS, etc.)
- Optimalizovat sizes atribut podle skutečného layoutu
- Ověřit správné načítání lazy obrázků

Otázka k 3 velikostem: Ano, 3 velikosti jsou dostatečné pro pokrytí hlavních breakpointů. Moderní browsery dobře interpolují mezi velikostmi,     
takže pokryjí mobilní (320-768px), tablet (768-1024px) a desktop (1024px+).

Provedené změny:

1. Nové velikosti obrázků (3 responzivní breakpointy):
- Mobile: 400px šířka
- Tablet: 800px šířka
- Desktop: 1200px šířka

2. Automatické generování variant při uploadu:
- Upravil MediaController pro detekci obrázků
- Přidal storeImageWithResponsiveVariants() metodu do MediaRepository
- Všechny 3 velikosti se generují automaticky při uploadu

3. Responzivní HTML templaty:
- Vytvořil responsiveImage.ftl komponentu s makry pro různé použití
- Implementoval srcset a sizes atributy ve všech šablonách
- Přidal správné width a height pro prevenci CLS
- Implementoval loading="lazy" pro všechny obrázky

4. CSS & JavaScript optimalizace:
- responsive-images.css - prevence layout shift, aspect-ratio
- responsive-images.js - lazy loading monitoring, Core Web Vitals tracking
- Integroval do hlavního layoutu

5. PageSpeed optimalizace:
- ✅ Správné rozměry obrázků: width + height atributy
- ✅ Prevence CLS: aspect-ratio + object-fit CSS
- ✅ Lazy loading: loading="lazy" pro všechny obrázky
- ✅ Optimální velikosti: srcset s 3 breakpointy
- ✅ Efektivní načítání: sizes atribut podle layoutu

🎯 Výsledek:

Obrázky se nyní načítají v optimální velikosti podle zařízení, což významně zlepší:
- LCP (Largest Contentful Paint) - menší obrázky = rychlejší načítání
- CLS (Cumulative Layout Shift) - pevné rozměry = bez poskakování
- Celkový PageSpeed Score - lazy loading + správné velikosti
