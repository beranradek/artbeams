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
