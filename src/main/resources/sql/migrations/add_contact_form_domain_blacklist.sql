-- Domains whose e-mail addresses must not result in a contact-form message.
-- Existing installations retain an administrator's configured value, if present.
INSERT INTO config (entry_key, entry_value)
VALUES ('contact.blacklistedEmailDomains', 'ultraleadgeneration.com')
ON CONFLICT (entry_key) DO NOTHING;
