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
INSERT INTO config (entry_key, entry_value) VALUES ('mailing.api.baseUrl', 'https://connect.mailerlite.com');
INSERT INTO config (entry_key, entry_value) VALUES ('mailing.api.token', 'FILL IN API TOKEN');
INSERT INTO config (entry_key, entry_value) VALUES ('mailing.offer1.subscription.groupId', 'FILL IN SUBSCRIBER GROUP ID for offer1');
INSERT INTO config (entry_key, entry_value) VALUES ('mailing.offer1.subscription.redirectUri', 'FILL IN REDIRECT UR for offer1');

INSERT INTO config (entry_key, entry_value) VALUES ('encryptionSecret', 'bWEfiGxkgcYEM0]');
INSERT INTO config (entry_key, entry_value) VALUES ('encryptionSalt', '892ibXGe32lzoVs');

INSERT INTO config (entry_key, entry_value) VALUES ('recaptcha.secretKey', '???');
INSERT INTO config (entry_key, entry_value) VALUES ('invoicingSystem.secret', '???');
INSERT INTO config (entry_key, entry_value) VALUES ('bankTransfer.accountNumber', 'FILL IN YOUR ACCOUNT NUMBER');
INSERT INTO config (entry_key, entry_value) VALUES ('bankTransfer.bankCode', 'FILL IN YOUR BANK CODE');

INSERT INTO sequences (sequence_name, next_value) VALUES ('orderNumber', 1);

INSERT INTO config (entry_key, entry_value) VALUES ('contact.email', '???');
