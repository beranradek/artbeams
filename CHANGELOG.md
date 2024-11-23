# Changelog

1.5.0 (12.10.2024)

```sql
ALTER TABLE products ADD COLUMN subtitle VARCHAR(256) DEFAULT NULL;
ALTER TABLE products ADD listing_image VARCHAR(128) DEFAULT NULL;
ALTER TABLE products ADD image VARCHAR(128) DEFAULT NULL;
```

1.4.1 (13.9.2024)
* Common web base URL:
```sql
INSERT INTO config (entry_key, entry_value) VALUES ('web.baseUrl', 'http://localhost:8080');
INSERT INTO config (entry_key, entry_value) VALUES ('web.name', 'FILL IN YOUR WEB NAME');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.sender.name', 'Radek Beran');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.password.recovery.subject', 'Obnova hesla');
INSERT INTO config (entry_key, entry_value) VALUES ('mailer.password.setup.subject', 'Nastavení hesla');
INSERT INTO localisation (entry_key, entry_value) VALUES ('passwordRecovery.header', 'Zapomenuté heslo');
INSERT INTO localisation (entry_key, entry_value) VALUES ('passwordRecovery.instructions', 'Zadejte prosím Váš email, na který Vám zašleme odkaz pro obnovení hesla.');
INSERT INTO localisation (entry_key, entry_value) VALUES ('passwordRecovery.submit', 'Požádat o obnovu hesla');
INSERT INTO localisation (entry_key, entry_value) VALUES ('passwordRecovery.sent', 'Na zadaný email byl odeslán odkaz pro obnovení hesla. Zkontrolujte prosím svou emailovou schránku a pokračujte podle instrukcí v emailu.');
INSERT INTO localisation (entry_key, entry_value) VALUES ('user', 'Uživatel');
INSERT INTO localisation (entry_key, entry_value) VALUES ('email', 'E-mail');
```

1.4.0 (23.8.2024)
* Changed algorithm to store passwords in database (Pbkdf2PasswordHash)!
Increased size of password column:
```sql
ALTER TABLE users
ALTER COLUMN password TYPE VARCHAR(500),
ALTER COLUMN password SET DEFAULT NULL;
```

* Reusable forms Freemarker macros.
* Translations of validation messages: constraints.Email.message
```sql
INSERT INTO localisation (entry_key, entry_value) VALUES ('constraints.Email.message', 'Nevalidní email.');
```

* reCaptcha V3 settings:
```sql
INSERT INTO localisation (entry_key, entry_value) VALUES ('captcha.invalid', 'Nevalidní captcha (ochrana proti robotům). Zkuste prosím hýbat myší více jako člověk :-), nebo mě kontaktujte emailem.');
INSERT INTO localisation (entry_key, entry_value) VALUES ('form-processing.error', 'Chyba při zpracování formuláře. Zkuste to prosím později nebo mě kontaktujte emailem.');
INSERT INTO localisation (entry_key, entry_value) VALUES ('recaptcha.siteKey', '???');
INSERT INTO config (entry_key, entry_value) VALUES ('recaptcha.secretKey', '???');
```

1.3.0 (4.2.2024)
* Synchronization of articles with Google Documents.

1.2.0 (12.11.2023)
* Image transformations in media gallery, article image upload.

1.1.0 (24.10.2021)
* Semi-automatic migration from Scala to Kotlin.
* Support of Kotlin classes in Formio forms.

1.0.5 (1.8.2021)
* Storing width and height for uploaded images.
* Nearest available image size served: /media/<image-file-name>?size=<width>
* Article has only one image file name (which can have various sizes in media gallery).

1.0.4 (8.5.2021)
* Antispam quiz for comments

1.0.3 (24.4.2021)
* Spring Boot: 2.1.1.RELEASE -> 2.4.5
* Scala 2.12.8 -> 2.13.5

1.0.2 (10.4.2021)
* Caching articles, categories, comments, user access counts.
* Running queries on public web pages in parallel.

1.0.1 (4.4.2021)
* First version.