package org.xbery.artbeams.common.seo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.prices.domain.Price
import org.xbery.artbeams.products.domain.Product
import java.math.BigDecimal

class StructuredDataGeneratorTest :
    StringSpec({
        "generateProductJsonLd should include merchant return policy and shipping details for offers" {
            val product = Product(
                common = AssetAttributes.EMPTY,
                slug = "prakticky-pruvodce-hlubokym-spankem",
                title = "Prakticky pruvodce hlubokym spankem",
                subtitle = "Digitalni produkt",
                fileName = "ebook.pdf",
                listingImage = null,
                image = null,
                confirmationMailingGroupId = null,
                mailingGroupId = null,
                priceRegular = Price(BigDecimal("490"), "CZK"),
                priceDiscounted = null,
                simpleShopProductId = null
            )

            val jsonLd = StructuredDataGenerator.generateProductJsonLd(
                product = product,
                pageUrl = "https://www.vysnenezdravi.cz/produkt/${product.slug}",
                description = "Prakticky pruvodce ke spanku",
                imageUrl = null,
                siteName = "Vysnene Zdravi",
                logoUrl = "https://www.vysnenezdravi.cz/media/logo.png"
            )

            jsonLd shouldContain "\"shippingDetails\""
            jsonLd shouldContain "\"@type\": \"OfferShippingDetails\""
            jsonLd shouldContain "\"addressCountry\": \"CZ\""
            jsonLd shouldContain "\"value\": 0"
            jsonLd shouldContain "\"hasMerchantReturnPolicy\""
            jsonLd shouldContain "\"@type\": \"MerchantReturnPolicy\""
            jsonLd shouldContain "\"returnPolicyCategory\": \"https://schema.org/MerchantReturnNotPermitted\""
        }
    })
