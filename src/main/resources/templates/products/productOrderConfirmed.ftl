<#import "/newWebLayout.ftl" as layout>
<@layout.page>

<h1 class="blog-post-title">${article.title!}</h1>

<div>${article.body!}</div>

<div class="container mt-5 text-center">
    <h1>QR kód pro provedení platby</h1>
    <div>
        <canvas id="qr-code"></canvas>
    </div>
</div>

<script nonce="${_cspNonce}" src="https://cdn.jsdelivr.net/npm/qrious/dist/qrious.min.js"></script>

<script nonce="${_cspNonce}">
    const accountNumber = "${accountNumber}";
    const bankCode = "${bankCode}";
    const amount = "${amount}";
    const currency = "${currency}";
    const variableSymbol = "${variableSymbol}";
    const message = "${message}";

    // Generate QR Platba formatted data
    ready(function() {
        const qrData = `SPD*1.0*ACC:${accountNumber}/${bankCode}*AM:${amount}*CC:${currency}*VS:${variableSymbol}*MSG:${message}`;
        const qr = new QRious({
            element: document.getElementById('qr-code'),
            value: qrData,
            size: 250
        });
    });
</script>

</@layout.page>
