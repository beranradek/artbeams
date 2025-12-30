# Manage templates 

Visit https://app.eu.mailgun.com/mg/sending/mailgun.<your-domain>/templates/ (or similar URL for US region)
and prepare these templates:

## Template password.setup

Description: Mail with link and token for initial password setup.
Body created using Visual Builder:

Vytvoření účtu

Krásný den,

na webu {{webName}} Vám byl vytvořen nový účet. Ke svému účtu můžete nastavit heslo a přihlásit se zde:

|Nastavit heslo| with URL {{tokenUrl}}

S pozdravem
{{senderName}}
<Site URL>
<Address>

## Template password.recovery

Description: Password recovery mail with URL and token to set password.
Body created using Visual Builder:

Obnova hesla

Krásný den,

ze stránek {{webName}} byla odeslána žádost o obnovu zapomenutého hesla. Pokud jste žádost neodesílali, nemusíte podnikat žádnou akci. Heslo k účtu si můžete nastavit zde:

|Nastavit nové heslo| with URL {{tokenUrl}}

S pozdravem
{{senderName}}
<Site URL>
<Address>

## Template member.section

Description: Mail to inform member about new product available in his/her member section.

Nový produkt ve Vaší členské sekci

Krásný den,

v členské sekci webu {{webName}} máte k dispozici nový produkt:

|Přihlásit se do členské sekce| with URL {{memberLoginUrl}}

Pokud jste zapomněli své přihlašovací údaje, můžete si je obnovit zde:

|Nastavit nové heslo| with URL {{forgottenPasswordUrl}}

S pozdravem
{{senderName}}
<Site URL>
<Address>
