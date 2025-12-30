# ArtBeams Public Web Redesign Requirements (using markup and styles and behaviour of Lovable Design Components)

These requirements are written both in Czech and English language mixed. But the target code should be in English as common, texts on the web are in Czech (or translated in FreeMarker templates).

I created new design (components and styles) for my artbeams project / web, using the Lovable service and the result is in repo c:\dev\vysnenezdravi-lovable, 
and online for preview at URL https://preview--dreamy-vysnene-visions-landing.lovable.app/. 
Read the repo and fetch the URL and create comprehensive plan in REDESIGN_PLAN.md in artbeams project root, 
plan that describes how to lead and fully implement redesign. 
I like the clear component structure, but my web is not using React and for my development skill, simplicity and easy maintainability, 
I want to preserve my poor bootstrap and plain CSS and JavaScript solution if possible. 
I want to use new components (their markup, CSS styles) in public part of the project (web) only, not in administration. 
Can you do the comprehensive reseach of the provided repo, web, my project and write the comprehensive plan into REDESIGN_PLAN.md with desired structure for my artbeams project, implementation steps
filled with concrete files and components, including migration of my images, reworking my templates, current CSS styles and possibly some JavaScripts? 
JavaScript funtionality should be mainly preserved from my current web site. Write the plan so the AI programming agent can easily follow it and implement the whole redesign.

Nepřidávej žádné nové bloky a texty, kromě přesně toho, co je v novém designu a zachovávej obsahové prvky v původním webu, kromě hlavičky s obrázkem autora a ebookem, která bude předělána podle nového designu a s novým obrázkem autora. 

Pro nové komponenty, které mají potenciál být znovupoužité, vytvářej samostatné FreeMarker makro soubory. Takto můžeš zachovat větší strukturovanost komponent, jako je to v novém designu. Komponenty pak nezapomeň zařadit správně do webu. Komponenty jistě lokální pro design veřejného webu můžeš zakomponovat přímo do souboru web layoutu veřejného webu.

Nové CSS styly přidávej do nových souborů, soubory s původními styly zachovej, ale vyřaď je z nového webu, aby nebyly použity, tj. vyřaď z layoutu celý styles.css. Gradle task pro minifikaci neupravuj, opravím ho později sám. Nové soubory se styly zařaď přímo do web layoutu.

Nové komponenty a styly tvoř přesně podle kódu nového designu. Vytvářej nové freemarker soubory, nové styly a případně nové JavaScripty.

Do nových stránek použiji obrázky z adresáře: 

Do nového stylu articles.css překopíruj z původních stylů všechny potřebné styly pro tvorbu specifických elementů v článcích, například boxy, zarovnání obrázků a další prvky používané při psaní článků. Tento soubor zařadím později do nového webu po otestování podoby nového webu.

Kontrolní kroky:

- [ ] Všechen užitečný markup a všechny užitečné obsahové prvky z nového designu musí být aplikovány.
- [ ] Všechny komponenty jsou použité a zařazeny do nového webu.
- [ ] Nakonec zkompiluj a spusť aplikaci, pomocí fetch nástroje porovnej markup a styly nových stránek a poskytnutého preview Lovable designu.
  - [ ] Parse generated HTML for missing/incorrect classes, broken layout, or missing assets.
  - [ ] Check for presence of new color variables, utility classes, and correct Bootstrap structure.
- [ ] Pořiď screenshoty vzorového designu a nových stránek a zkontroluj, zda bylo vše v pořádku aplikováno. 
- [ ] V nové aplikaci zkontroluj případné chyby v javascriptové konzoli prohlížeče.
- [ ] Render HTML with different viewport sizes to check responsive classes and layout. 

Zahrň všechny tyto informace včetně výsledků vlastní podrobné rešerše do podrobného implementačního plánu.
