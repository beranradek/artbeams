<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<style nonce="${_cspNonce}">
/* Product Detail Page Styling - Inspired by Calm Winter Nights */

:root {
    --product-dark-navy: #0f1e2e;
    --product-white: white;
    --product-deep-blue: #1a3b5c;
    --product-frost: #e8f1f8;
    --product-ice: #c8dde8;
    --product-silver: #a8b8c8;
    --product-gold: #d4af37;
    --product-warm-glow: #ffd89b;
    --product-moonlight: #f0f4f8;
}

.product-detail-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem 1rem;
}

/* Hero Section with Product Image */
.product-hero {
    position: relative;
    /* background: linear-gradient(135deg, var(--product-white) 0%, var(--product-white) 100%); */
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(15, 30, 46, 0.4);
    margin-bottom: 3rem;
}

.product-hero-inner {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 3rem;
    padding: 3rem;
    align-items: center;
    position: relative;
    z-index: 2;
}

.product-image-wrapper {
    position: relative;
}

@keyframes pulse-glow {
    0%, 100% { opacity: 0.2; }
    50% { opacity: 0.4; }
}

.product-content {
    color: var(--product-frost);
}

.product-title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 3.5rem;
    font-weight: 700;
    line-height: 1.1;
    margin-bottom: 1rem;
    color: black;
    text-shadow: 0 2px 2px rgba(0, 0, 0, 0.3);
    letter-spacing: -0.02em;
}

.product-subtitle {
    font-family: 'Crimson Text', serif;
    font-size: 1.5rem;
    font-weight: 400;
    line-height: 1.5;
    color: var(--product-deep-blue);
    margin-bottom: 2rem;
    font-style: italic;
}

.product-divider {
    width: 80px;
    height: 4px;
    background: linear-gradient(90deg, var(--product-gold) 0%, var(--product-warm-glow) 100%);
    border: none;
    margin: 2rem 0;
    border-radius: 2px;
}

.product-download-section {
    margin-top: 2.5rem;
}

.btn-download-product {
    display: inline-flex;
    align-items: center;
    gap: 0.75rem;
    padding: 1rem 2.5rem;
    font-family: 'Crimson Text', serif;
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--product-dark-navy);
    background: linear-gradient(135deg, var(--product-gold) 0%, var(--product-warm-glow) 100%);
    border: none;
    border-radius: 50px;
    text-decoration: none;
    box-shadow: 0 8px 24px rgba(212, 175, 55, 0.4);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.btn-download-product::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    transform: translate(-50%, -50%);
    transition: width 0.6s ease, height 0.6s ease;
}

.btn-download-product:hover::before {
    width: 300px;
    height: 300px;
}

.btn-download-product:hover {
    transform: translateY(-3px);
    box-shadow: 0 12px 32px rgba(212, 175, 55, 0.6);
    color: var(--product-dark-navy);
}

.btn-download-product svg {
    width: 24px;
    height: 24px;
    position: relative;
    z-index: 1;
}

.btn-download-product span {
    position: relative;
    z-index: 1;
}

/* Floating particles background effect */
.product-particles {
    position: absolute;
    inset: 0;
    overflow: hidden;
    pointer-events: none;
    z-index: 1;
}

.particle {
    position: absolute;
    width: 3px;
    height: 3px;
    background: var(--product-ice);
    border-radius: 50%;
    opacity: 0.4;
    animation: float-particle 8s linear infinite;
}

.particle:nth-child(2n) {
    width: 2px;
    height: 2px;
    animation-duration: 12s;
    animation-delay: -4s;
}

.particle:nth-child(3n) {
    opacity: 0.2;
    animation-duration: 15s;
    animation-delay: -8s;
}

@keyframes float-particle {
    0% {
        transform: translateY(100vh) translateX(0) rotate(0deg);
        opacity: 0;
    }
    10% {
        opacity: 0.4;
    }
    90% {
        opacity: 0.4;
    }
    100% {
        transform: translateY(-100px) translateX(100px) rotate(360deg);
        opacity: 0;
    }
}

/* Responsive Design */
@media (max-width: 968px) {
    .product-hero-inner {
        grid-template-columns: 1fr;
        gap: 2rem;
        padding: 2rem;
    }

    .product-title {
        font-size: 2.5rem;
    }

    .product-subtitle {
        font-size: 1.25rem;
    }
}

@media (max-width: 576px) {
    .product-hero-inner {
        padding: 1.5rem;
    }

    .product-title {
        font-size: 2rem;
    }

    .product-subtitle {
        font-size: 1.1rem;
    }

    .btn-download-product {
        padding: 0.875rem 2rem;
        font-size: 1.1rem;
    }
}

/* Font imports */
@import url('https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@400;700&family=Crimson+Text:ital,wght@0,400;0,600;1,400&display=swap');
</style>

<div class="product-detail-container">
    <div class="product-hero">
        <!-- Floating particles background -->
        <div class="product-particles">
            <div class="particle" style="left: 10%; animation-delay: 0s;"></div>
            <div class="particle" style="left: 25%; animation-delay: -3s;"></div>
            <div class="particle" style="left: 40%; animation-delay: -6s;"></div>
            <div class="particle" style="left: 55%; animation-delay: -2s;"></div>
            <div class="particle" style="left: 70%; animation-delay: -5s;"></div>
            <div class="particle" style="left: 85%; animation-delay: -8s;"></div>
            <div class="particle" style="left: 15%; animation-delay: -10s;"></div>
            <div class="particle" style="left: 50%; animation-delay: -7s;"></div>
            <div class="particle" style="left: 80%; animation-delay: -4s;"></div>
            <div class="particle" style="left: 35%; animation-delay: -9s;"></div>
        </div>

        <div class="product-hero-inner">
            <#if userProduct.image??>
                <div class="product-image-wrapper">
                    <img src="${userProduct.image}" alt="${userProduct.title}">
                </div>
            </#if>

            <div class="product-content">
                <h1 class="product-title">${userProduct.title}</h1>

                <#if userProduct.subtitle??>
                    <h2 class="product-subtitle">${userProduct.subtitle}</h2>
                </#if>

                <hr class="product-divider">

                <div class="product-download-section">
                    <a href="/clenska-sekce/${userProduct.slug}/download" class="btn-download-product">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 3V16M12 16L16 11.625M12 16L8 11.625M21 16V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V16"
                                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                        <span>Stáhnout produkt</span>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

</@layout.page>
