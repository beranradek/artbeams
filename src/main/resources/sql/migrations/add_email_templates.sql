-- Execute this script with: psql -U artbeams_user -d artbeams -f src/main/resources/sql/migrations/add_email_templates.sql
-- Transactional e-mail templates are stored locally so administrators can edit their subject and HTML body.
CREATE TABLE email_templates (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    created timestamp NOT NULL,
    created_by VARCHAR(40) NOT NULL,
    modified timestamp NOT NULL,
    modified_by VARCHAR(40) NOT NULL,
    template_key VARCHAR(80) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body_html TEXT NOT NULL,
    system_template boolean NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX idx_email_templates_key ON email_templates (template_key);

INSERT INTO email_templates (id, created, created_by, modified, modified_by, template_key, subject, body_html, system_template) VALUES
('email-password-recovery', now(), 'SYSTEM', now(), 'SYSTEM', 'password-recovery', 'Obnova hesla', '<p>Dobrý den,</p><p>pro obnovu hesla na webu {{webName}} klikněte na tento odkaz:</p><p><a href="{{tokenUrl}}">Obnovit heslo</a></p><p>{{senderName}}</p>', true),
('email-password-setup', now(), 'SYSTEM', now(), 'SYSTEM', 'password-setup', 'Nastavení hesla', '<p>Dobrý den,</p><p>pro nastavení hesla na webu {{webName}} klikněte na tento odkaz:</p><p><a href="{{tokenUrl}}">Nastavit heslo</a></p><p>{{senderName}}</p>', true),
('email-member-section-access', now(), 'SYSTEM', now(), 'SYSTEM', 'member-section-access', 'Potvrzení platby a přístup do členské sekce', '<p>Děkujeme za platbu.</p><p>Do členské sekce se přihlásíte zde: <a href="{{memberLoginUrl}}">Přihlásit se</a>.</p><p>Pokud heslo neznáte, nastavte jej zde: <a href="{{forgottenPasswordUrl}}">Nastavit heslo</a>.</p><p>{{senderName}}</p>', true)
ON CONFLICT (template_key) DO NOTHING;
