package org.xbery.artbeams.courses

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File

class SeedScriptContentTest :
    FunSpec({
        test("seed script exists and contains required tokens") {
            val file = File("scripts/seed_courses_e2e.sql")
            file.exists() shouldBe true
            val content = file.readText()
            content.contains("testadmin") shouldBe true
            content.contains("testmember") shouldBe true
            content.contains("zdrave-stravovani") shouldBe true
            content.contains("Kurz zdravého stravování") shouldBe true
        }
    })
