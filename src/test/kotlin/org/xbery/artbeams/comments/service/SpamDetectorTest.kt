package org.xbery.artbeams.comments.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import org.mockito.Mockito
import org.xbery.artbeams.config.repository.AppConfig

/**
 * Direct test class for the SpamDetector
 */
class SpamDetectorTest : StringSpec({
    // Create a mock AppConfig using Mockito
    val mockAppConfig = Mockito.mock(AppConfig::class.java)
    Mockito.`when`(mockAppConfig.findConfig(Mockito.anyString())).thenReturn(null)

    // Create a normal detector with all checks
    val spamDetector = SpamDetector(mockAppConfig)
    
    // Create a detector without language checks for certain tests
    val spamDetectorWithoutLanguageCheck = TestSpamDetector(mockAppConfig, false)

    // Tests for common spam text patterns
    "should detect spam based on common spam keywords" {
        val spamTexts = listOf(
            "Buy viagra online now!",
            "Cialis for the best price",
            "Discount levitra pills available",
            "Online pharmacy offers the best deals",
            "Get the best casino bonuses",
            "Poker tournament with big prizes",
            "Gambling site with the best odds",
            "Payday loans with no credit check",
            "Mortgage refinancing at low rates",
            "Bitcoin investment opportunity",
            "Forex trading signals",
            "Best diet pills that work fast",
            "Weight loss supplements"
        )

        spamTexts.forEach { comment ->
            spamDetector.isSpam(comment, "ValidUser", "valid@example.com") shouldBe true
        }
    }

    "should not flag legitimate text as spam" {
        val legitimateTexts = listOf(
            "Dobrý den, mám dotaz ohledně vašeho produktu.",
            "Článek je velmi zajímavý, děkuji za informace.",
            "Kdy bude další článek na toto téma?",
            "Moc se mi líbí vaše webové stránky.",
            "Děkuji za rychlou odpověď na můj předchozí komentář."
        )

        legitimateTexts.forEach { comment ->
            spamDetector.isSpam(comment, "Radek B.", "dobry@seznam.cz") shouldBe false
        }
    }

    // Test for suspicious emails using direct isSpam check
    "should detect spam based on suspicious email patterns" {
        // Test spam emails directly
        spamDetector.isSpam("Český normální komentář", "Normal User", "random123user@yahoo.com") shouldBe true
        spamDetector.isSpam("Český normální komentář", "Normal User", "test123test@hotmail.com") shouldBe true
        spamDetector.isSpam("Český normální komentář", "Normal User", "temp123@tempmail.com") shouldBe true
        spamDetector.isSpam("Český normální komentář", "Normal User", "whatever@mailinator.com") shouldBe true
        spamDetector.isSpam("Český normální komentář", "Normal User", "test@guerrillamail.com") shouldBe true
        spamDetector.isSpam("Český normální komentář", "Normal User", "user123@throwawaymail.com") shouldBe true
        
        // Gmail should not be detected as spam if also name in prefix is not suspicious
        spamDetector.isSpam("Český normální komentář", "Normal User", "jana.novotna@gmail.com") shouldBe false
    }

    "should not flag legitimate emails as spam" {
        // Test legitimate emails directly
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Normal User", "jan.novak@seznam.cz") shouldBe false
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Normal User", "pavel@firma.cz") shouldBe false
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Normal User", "marie.svobodova@company.com") shouldBe false
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Normal User", "jitka@domain.org") shouldBe false
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Normal User", "user@gmail.com") shouldBe false
    }

    // Tests for suspicious usernames
    "should detect spam based on suspicious usernames" {
        // Test suspicious usernames directly
        spamDetector.isSpam("Normal comment", "user123456", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Normal comment", "JohnDoe345", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Normal comment", "Marketing123", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Normal comment", "Promo567User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Normal comment", "TestUser789", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Normal comment", "JuniorKeera", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Normal comment", "James2023Smith", "normal@domain.org") shouldBe true
    }

    "should not flag legitimate usernames as spam" {
        // Test legitimate usernames directly 
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Jan Novák", "normal@domain.org") shouldBe false
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Marie Svobodová", "normal@domain.org") shouldBe false
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Petr", "normal@domain.org") shouldBe false
        spamDetectorWithoutLanguageCheck.isSpam("Normal comment", "Kateřina Nováková", "normal@domain.org") shouldBe false
    }

    // Tests for suspicious content patterns
    "should detect spam based on suspicious content patterns" {
        // Test suspicious content directly
        spamDetector.isSpam("Check out our special offer with 50% discount!", "Normal User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Buy now at cheap prices before the promotion ends", "Normal User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Click here to visit our website for more information", "Normal User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Limited time offer, 100% satisfaction guaranteed", "Normal User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Special promotion: buy one, get one free!", "Normal User", "normal@domain.org") shouldBe true
    }

    // Tests for suspicious links
    "should detect spam based on suspicious links" {
        // Test suspicious links directly
        spamDetector.isSpam("Check this website: https://example.com", "Normal User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("More info at www.example.com", "Normal User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Visit our website http://website.com for details", "Normal User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Link to our offer: bit.ly/2aB3cD", "Normal User", "normal@domain.org") shouldBe true
        spamDetector.isSpam("Shortened link: tinyurl.com/example", "Normal User", "normal@domain.org") shouldBe true
    }

    // Test for the specific example from the user query
    "should detect the specific spam example from the user query" {
        val spamComment = "https://canvas.instructure.com/eportfolios/3678306/home/el-mejor-sustrato-para-tu-kit-de-cultivo-indoor Cuando decidi empezar mi cultivo en casa, lo primero que pense fue: como elijo el mejor Kit de Cultivo Indoor? Vivo en Chile, y aunque hay varias opciones, no tenia idea de por donde empezar. Entre tantas tiendas online, marcas y tipos de kits, me senti abrumado. Lo que me salvo fue encontrar un blog que explicaba como elegir tu primer Kit de Cultivo Indoor segun tu espacio, tipo de planta y presupuesto. Tambien hablaban de los errores mas comunes (como comprar un kit sin buena ventilacion o con luces inadecuadas) y donde comprar en Chile sin caer en estafas o kits de baja calidad. Aprendi que no se trata solo de tener una carpa, sino de un sistema completo: iluminacion LED eficiente, extractor, timer, sustrato, fertilizantes, y hasta el tipo de macetas influye en el resultado. Gracias a esa info, mi primer cultivo fue un exito. Ahora tengo un espacio funcional, limpio y con plantas sanas. Si estas empezando, de verdad te recomiendo leer antes de comprar. Ahi entendi todo lo que necesitaba."
        val userName = "JuniorKeera"
        val email = "ghueg1wdw5r@gmail.com"

        spamDetector.isSpam(spamComment, userName, email) shouldBe true
    }

    // Test for combinations of spam signals
    "should detect spam when multiple signals appear together" {
        val comment = "Normal looking comment without spam keywords"
        val userName = "user12345"
        val email = "random123@tempmail.com"

        spamDetector.isSpam(comment, userName, email) shouldBe true
    }

    // Test for edge cases and corner cases
    "should handle empty inputs properly" {
        spamDetector.isSpam("", "", "") shouldBe false
    }

    "should handle very long inputs" {
        val longComment = "a".repeat(10000)
        val longUserName = "b".repeat(100)
        val longEmail = "c".repeat(50) + "@" + "d".repeat(50) + ".com"

        spamDetector.isSpam(longComment, longUserName, longEmail) shouldBe true
    }

    // Tests for false positives
    "should not flag legitimate content with similar patterns to spam" {
        table(
            headers("comment", "userName", "email", "expected"),
            row("Mluvili jsme o investování na poslední konferenci", "Jan", "jan@firma.cz", false),
            row("Přečetl jsem knihu o obchodování na burze", "Petr", "petr@seznam.cz", false)
        ).forAll { comment, userName, email, expected ->
            spamDetectorWithoutLanguageCheck.isSpam(comment, userName, email) shouldBe expected
        }
        
        // gmail.com should be allowed
        spamDetector.isSpam("Můj doktor mi předepsal léky na moji nemoc", "Eva", "eva@gmail.com") shouldBe false
    }
}) 
