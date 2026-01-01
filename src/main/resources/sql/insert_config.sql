INSERT INTO config (entry_key, entry_value) VALUES ('evernote.developer-token', '???');
INSERT INTO config (entry_key, entry_value) VALUES ('google.oauth.client.json', '{"installed":{"client_id":"FILL IN VALUE","project_id":"FILL IN VALUE","auth_uri":"https://accounts.google.com/o/oauth2/auth","token_uri":"https://oauth2.googleapis.com/token","auth_provider_x509_cert_url":"https://www.googleapis.com/oauth2/v1/certs","client_secret":"FILL IN VALUE","redirect_uris":["http://localhost"]}}');
INSERT INTO config (entry_key, entry_value) VALUES ('google.application-name', 'FILL IN APPLICATION NAME');
INSERT INTO config (entry_key, entry_value) VALUES ('web.baseUrl', 'http://localhost:8080');
INSERT INTO config (entry_key, entry_value) VALUES ('web.name', 'FILL IN YOUR WEB NAME');

INSERT INTO config (entry_key, entry_value) VALUES ('mailer.api.baseUrl', 'https://api.eu.mailgun.net/');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.api.key', '???');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.api.domain', '???');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.from', 'info@???');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.replyTo', 'info@???');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.sender.name', 'FILL IN YOUR NAME');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.password.recovery.subject', 'Obnova hesla');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.password.recovery.template', 'password.recovery');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.password.setup.subject', 'Nastavení hesla');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.password.setup.template', 'password.setup');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.member.section.subject', 'Potvrzení platby a přístup do členské sekce');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.member.section.template', 'member.section');
INSERT INTO config (entry_key, entry_value) VALUES ('mailing.api.baseUrl', 'https://connect.mailerlite.com');
INSERT INTO config (entry_key, entry_value) VALUES ('mailing.api.token', 'FILL IN API TOKEN');

INSERT INTO config (entry_key, entry_value) VALUES ('news.subscription.confirmation.groupId', 'FILL IN NEWSLETTER SUBSCRIPTION CONFIRMATION GROUP ID');
INSERT INTO config (entry_key, entry_value) VALUES ('news.subscription.groupId', 'FILL IN NEWSLETTER SUBSCRIPTION GROUP ID');

INSERT INTO config (entry_key, entry_value) VALUES ('encryptionSecret', 'bWEfiGxkgcYEM0]');
INSERT INTO config (entry_key, entry_value) VALUES ('encryptionSalt', '892ibXGe32lzoVs');

INSERT INTO config (entry_key, entry_value) VALUES ('recaptcha.secretKey', '???');
INSERT INTO config (entry_key, entry_value) VALUES ('invoicingSystem.secret', '???');
INSERT INTO config (entry_key, entry_value) VALUES ('bankTransfer.accountNumber', 'FILL IN YOUR ACCOUNT NUMBER');
INSERT INTO config (entry_key, entry_value) VALUES ('bankTransfer.bankCode', 'FILL IN YOUR BANK CODE');

INSERT INTO sequences (sequence_name, next_value) VALUES ('orderNumber', 1);

INSERT INTO config (entry_key, entry_value) VALUES ('contact.email', '???');

-- SimpleShop.cz API integration
INSERT INTO config (entry_key, entry_value) VALUES ('simpleshop.api.baseUrl', 'https://api.simpleshop.cz/v2');
INSERT INTO config (entry_key, entry_value) VALUES ('simpleshop.api.email', 'FILL IN YOUR SIMPLESHOP EMAIL');
INSERT INTO config (entry_key, entry_value) VALUES ('simpleshop.api.key', 'FILL IN YOUR SIMPLESHOP API KEY');

-- Admin notification email to new orders
INSERT INTO config (entry_key, entry_value) VALUES ('admin.notification.email', 'info@???');

-- Optional Google Analytics Tracking ID
-- INSERT INTO config (entry_key, entry_value) VALUES ('ga.tracking.id', 'UA-XXXXXX-X');

-- System prompt for the article editing agent (in Czech)
-- This prompt instructs the AI on how to assist with article editing
INSERT INTO config (entry_key, entry_value)
VALUES (
           'article.editing.agent.system.prompt',
           'Jsi pomocník pro editaci článků v redakčním systému ArtBeams.

       Tvým úkolem je pomáhat autorům s tvorbou a úpravou článků. Články jsou psány v českém jazyce
       a používají CommonMark markup jazyk pro formátování.

       Dokumentace CommonMark formátování je dostupná zde: https://commonmark.org/help/

       Když uživatel požádá o vytvoření nebo úpravu článku:
       1. Analyzuj současnou verzi článku uloženou v CMS (pokud je uvedena)
       2. Proveď požadované změny nebo vytvoř nový obsah
       3. Vrať kompletní upravený text článku ve formátu CommonMark
       4. Celý text článku obal do trojitých zpětných apostrofů (```)

       Příklad výstupu:
       ```
       # Nadpis článku

       Text článku s **tučným** a *kurzívou* textem.

       - Položka seznamu 1
       - Položka seznamu 2
       ```

       Vždy se snaž být nápomocný, konkrétní a dodržuj pravidla českého jazyka.'
       )
    ON CONFLICT (entry_key) DO UPDATE SET entry_value = EXCLUDED.entry_value;

-- Note: The AI agent requires OPENAI_API_KEY environment variable to be set
