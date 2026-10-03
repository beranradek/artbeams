# Žádosti o vrácení objednávky

## Cíl

Umožnit přihlášenému zákazníkovi podat a potvrdit žádost o vrácení své elektronické objednávky.
Žádost vytvoří dohledatelný pracovní úkol pro administrátora; peníze se vrací ručně mimo aplikaci.

## Produktové a bezpečnostní hranice

- Zákazník může žádat pouze o svou vlastní zaplacenou objednávku. Nelze tak měnit cizí objednávku
  ani podávat druhou otevřenou žádost ke stejné objednávce.
- Potvrzený požadavek **nemění** stav objednávky ani přístup k produktu. Administrátor nejdříve
  ověří vrácení peněz mimo aplikaci a až poté nastaví stav `REFUNDED`.
- Stav `REFUNDED` odebere uživateli přístup k objednaným produktům pro stahování, ale historie
  objednávek dál ukazuje, co bylo objednáno a vráceno.
- Volitelný důvod se velikostně omezí, zobrazí jako prostý text a musí být HTML-escaped v e-mailu.
  Do systémové události se neukládá celý důvod ani e-mail zákazníka.
- E-mail administrátorovi je best-effort notifikace; neúspěšné odeslání nesmí ztratit uloženou
  žádost a existující mechanismus zaloguje chybu odeslání.

## Datový model a migrace

1. Přidat `refund_requests` do `create_tables.sql` a idempotentní migraci:
   `id`, `order_id`, `user_id`, `reason`, `status`, `requested_at`, `resolved_at`, auditní sloupce.
2. Udržet právě jednu otevřenou žádost (`REQUESTED`) na objednávku pomocí částečného unikátního
   indexu; přidat indexy pro administrátorský přehled a historii uživatele.
3. Vygenerovat jOOQ artefakty a doplnit doménu, mapper/unmapper, repository a service.

## Uživatelský tok

1. Rozšířit existující `/clenska-sekce/moje-objednavky`: ukázat položky, stav a pro způsobilou
   objednávku odkaz „Požádat o vrácení“.
2. Detail/formulář musí ověřit vlastníka objednávky na serveru, přijmout volitelný důvod a zobrazit
   potvrzovací krok; POST je chráněný CSRF.
3. Po uložení zobrazit stav žádosti. U `REFUNDED` zachovat řádek objednávky, ale jasně označit,
   že položky byly vráceny a nejsou ke stažení.

## Administrace a notifikace

1. Do administrace objednávek doplnit výrazný odkaz s počtem otevřených žádostí; vede na samostatný
   seznam `/admin/refund-requests`.
2. Seznam a detail zobrazí objednávku, zákazníka, čas, volitelný důvod a stav. Pouze administrátor
   může označit požadavek jako vyřízený vrácením peněz (`REFUNDED`); tato akce atomicky nastaví
   stav objednávky a odebere odpovídající `user_product` přístupy.
3. Při podání zapíše systém `REFUND_REQUEST_SUBMITTED` se závažností `WARN` a pošle best-effort
   transakční e-mail na `admin.notification.email` s bezpečným odkazem do administrace.

## Ověření

- Repository/service testy: vlastník, nezpůsobilý stav, duplicitní otevřená žádost, auditní data a
  atomické dokončení/refund.
- Controller testy: nepřihlášený/cizí uživatel, CSRF potvrzení, validace délky důvodu a modely
  uživatelské i administrátorské stránky.
- Testy přístupu: po `REFUNDED` produkt zmizí z knihovny/stahování, ale zůstane v historii.
- Testy administrátorského indikátoru, systémové události a notifikace bez nezabezpečeného HTML.
- `./gradlew generateJooq test ktlintCheck` a relevantní integrační/migrační testy.
