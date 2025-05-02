package org.xbery.artbeams.comments.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.mockito.Mockito
import org.xbery.artbeams.config.repository.AppConfig

/**
 * Test class to focus specifically on language detection in SpamDetector
 */
class SpamDetectorLanguageTest : StringSpec({
    // Create a mock AppConfig using Mockito
    val mockAppConfig = Mockito.mock(AppConfig::class.java)
    Mockito.`when`(mockAppConfig.findConfig(Mockito.anyString())).thenReturn(null)
    
    // Create a specialized detector for this test
    val detector = SpamDetector(mockAppConfig)

    "should detect non-Czech comments as spam" {
        // These are long non-Czech texts that should be detected
        val longNonCzechTexts = listOf(
            "This is a long English comment without any Czech characters. It should be detected as non-Czech language content. The comment is intentionally long to trigger the language detection heuristic that requires a minimum length.",
            "Este es un comentario largo en español sin ningún carácter checo. Debería ser detectado como contenido en un idioma que no es checo. El comentario es intencionalmente largo para activar la heurística de detección de idioma que requiere una longitud mínima.",
            "https://canvas.instructure.com/eportfolios/3678306/home/el-mejor-sustrato-para-tu-kit-de-cultivo-indoor Cuando decidi empezar mi cultivo en casa, lo primero que pense fue: como elijo el mejor Kit de Cultivo Indoor? Vivo en Chile, y aunque hay varias opciones, no tenia idea de por donde empezar. Entre tantas tiendas online, marcas y tipos de kits, me senti abrumado. Lo que me salvo fue encontrar un blog que explicaba como elegir tu primer Kit de Cultivo Indoor segun tu espacio, tipo de planta y presupuesto. Tambien hablaban de los errores mas comunes (como comprar un kit sin buena ventilacion o con luces inadecuadas) y donde comprar en Chile sin caer en estafas o kits de baja calidad. Aprendi que no se trata solo de tener una carpa, sino de un sistema completo: iluminacion LED eficiente, extractor, timer, sustrato, fertilizantes, y hasta el tipo de macetas influye en el resultado. Gracias a esa info, mi primer cultivo fue un exito. Ahora tengo un espacio funcional, limpio y con plantas sanas. Si estas empezando, de verdad te recomiendo leer antes de comprar. Ahi entendi todo lo que necesitaba."
        )
        
        // We need to test the whole spam detection since we can't separate language detection only
        longNonCzechTexts.forEach { comment ->
            // Force the content to be considered spam only due to language
            val isSpam = detector.isSpam(
                comment = comment, 
                userName = "ValidUserName", 
                email = "valid@domain.org"
            )
            
            isSpam shouldBe true
        }
    }

    "should not detect Czech comments as spam based on language" {
        // These are Czech texts with diacritical marks that should pass the language check
        val czechTexts = listOf(
            "Toto je dlouhý český komentář s dostatkem českých znaků. Měl by být rozpoznán jako obsah v českém jazyce. Komentář je záměrně dlouhý, aby spustil heuristiku detekce jazyka, která vyžaduje minimální délku. Obsahuje znaky jako á, č, ď, é, ě, í, ň, ó, ř, š, ť, ú, ů, ý, ž.",
            "Dobrý den, chtěl bych se zeptat na váš produkt. Jaké jsou jeho hlavní výhody? Používám podobné výrobky již několik let a zajímalo by mě, v čem je ten váš lepší. Děkuji za odpověď a přeji hezký den. S pozdravem, Karel Novák"
        )
        
        // Short comment that shouldn't trigger language detection
        val shortText = "Hello"
        
        // For Czech texts, we need to ensure they're not mistakenly flagged
        // We need to use a mock with disabled other checks
        val czechDetector = TestSpamDetector(mockAppConfig, true)
        
        czechTexts.forEach { comment ->
            // We use a custom detector that only does language checks
            val isSpam = czechDetector.isSpamLanguageOnly(comment)
            isSpam shouldBe false
        }
        
        // Short comment should pass regardless of language
        czechDetector.isSpamLanguageOnly(shortText) shouldBe false
    }

    "should handle edge cases in language detection" {
        // Mixed language - if there are enough Czech characters, it should not be flagged
        val mixedText = "This is a mix of English and Czech. Obsahuje české znaky jako ř, č a ž. The text has both languages. " +
                        "Tento text má dostatek českých znaků, aby nebyl označen jako nevhodný. Nicméně obsahuje i anglické věty."

        // Rich text with HTML - the content should be analyzed properly
        val richText = "<p>Toto je <strong>formátovaný</strong> český text s dostatkem českých znaků jako ř, č, ž, ě, š, ď.</p>" +
                      "<p>Další odstavec s českými znaky.</p>"
                      
        // For edge cases, we also need a custom detector
        val czechDetector = TestSpamDetector(mockAppConfig, true)

        czechDetector.isSpamLanguageOnly(mixedText) shouldBe false
        czechDetector.isSpamLanguageOnly(richText) shouldBe false
    }
})

/**
 * Helper extension function to test only language detection
 */
private fun TestSpamDetector.isSpamLanguageOnly(comment: String): Boolean {
    return this.isNonCzechContent(comment)
} 