<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<style nonce="${_cspNonce}">
/* Member Section Styling */

:root {
    --member-night-sky: #0a1929;
    --member-deep-blue: #1a3b5c;
    --member-golden-light: #d4af37;
}

/* Main Heading */
.main-heading {
    font-size: 2.5rem;
    font-weight: 600;
    color: #000000;
    margin-bottom: 2rem;
    margin-top: 2rem;
}

.member-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 1rem;
}

.products-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 2rem;
    margin-bottom: 3rem;
}

/* Enhanced Product Cards */
.product-card {
    position: relative;
    background: #ffffff;
    border-radius: 20px;
    overflow: hidden;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(230, 230, 230, 0.8);
}

.product-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(26, 59, 92, 0.15);
    border-color: var(--member-golden-light);
}

.product-card-link {
    display: block;
    text-decoration: none;
    color: inherit;
}

.product-card-image {
    width: 100%;
    height: 240px;
    background-position: center;
    background-size: cover;
    background-repeat: no-repeat;
    position: relative;
    overflow: hidden;
}

.product-card-image::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.3) 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
}

.product-card:hover .product-card-image::after {
    opacity: 1;
}

.product-card-content {
    padding: 1.5rem;
}

.product-card-title {
    font-family: 'Crimson Text', serif;
    font-size: 1.5rem;
    font-weight: 600;
    color: var(--member-deep-blue);
    margin-bottom: 0.75rem;
    line-height: 1.3;
}

.product-card-divider {
    width: 40px;
    height: 4px;
    background: var(--member-golden-light);
    border: none;
    margin: 0.75rem 0;
    border-radius: 2px;
}

.product-card-desc-holder {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-top: 1rem;
}

.product-card-desc {
    font-family: 'Crimson Text', serif;
    font-size: 1.1rem;
    color: #555;
    line-height: 1.5;
    flex: 1;
}

.product-card-arrow {
    width: 24px;
    height: 24px;
    stroke: var(--member-golden-light);
    transition: transform 0.3s ease;
}

.product-card:hover .product-card-arrow {
    transform: translateX(4px);
}

/* Illustration Column */
.illustration-column {
    display: none;
}

@media (min-width: 1200px) {
    .products-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .illustration-column {
        display: block;
        position: sticky;
        top: 2rem;
        height: fit-content;
    }

    .illustration-image {
        width: 100%;
        height: auto;
        border-radius: 20px;
        box-shadow: 0 8px 30px rgba(26, 59, 92, 0.2);
        opacity: 0.9;
    }

    .member-layout-grid {
        display: grid;
        grid-template-columns: 2fr 1fr;
        gap: 3rem;
        align-items: start;
    }
}

/* Responsive Design */
@media (max-width: 768px) {
    .main-heading {
        font-size: 2rem;
    }

    .products-grid {
        grid-template-columns: 1fr;
        gap: 1.5rem;
    }
}

/* Font imports */
@import url('https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@400;700&family=Crimson+Text:ital,wght@0,400;0,600;1,400&display=swap');
</style>

<div class="member-container">
    <h1 class="main-heading">Moje produkty</h1>

    <div class="member-layout-grid">
        <div>
            <#if userProducts?has_content>
                <div class="products-grid">
                    <#list userProducts as userProduct>
                        <div class="product-card">
                            <a href="/clenska-sekce/${userProduct.slug}" class="product-card-link">
                                <#if userProduct.listingImage??>
                                    <div class="product-card-image" style="background-image: url('${userProduct.listingImage}');"></div>
                                <#else>
                                    <div class="product-card-image" style="background: linear-gradient(135deg, var(--member-deep-blue) 0%, var(--member-night-sky) 100%);"></div>
                                </#if>

                                <div class="product-card-content">
                                    <h3 class="product-card-title">${userProduct.title}</h3>
                                    <hr class="product-card-divider">
                                    <div class="product-card-desc-holder">
                                        <p class="product-card-desc">
                                            <#if userProduct.subtitle??>${userProduct.subtitle}<#else>Klikněte pro zobrazení detailu</#if>
                                        </p>
                                        <svg class="product-card-arrow" focusable="false" aria-hidden="true" viewBox="0 0 24 24" fill="none">
                                            <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                        </svg>
                                    </div>
                                </div>
                            </a>
                        </div>
                    </#list>
                </div>
            <#else>
                <div class="alert alert-warning" role="alert">
                    Zatím jste si od nás nepořídili žádný produkt. To nevadí, můžete si na stránkách nějaký vybrat.
                </div>
            </#if>
        </div>

        <!-- Illustration Column (visible only on larger screens) -->
        <div class="illustration-column">
            <img src="/static/images/night-tree.webp" class="illustration-image" alt="Zimní krajina v noci">
        </div>
    </div>
</div>

</@layout.page>
