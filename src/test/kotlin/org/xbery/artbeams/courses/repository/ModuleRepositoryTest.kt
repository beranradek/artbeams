package org.xbery.artbeams.courses.repository

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.courses.admin.EditedModule
import org.xbery.artbeams.courses.repository.mapper.ModuleMapper

/**
 * Unit tests for ModuleRepository.resolveId (src/main/kotlin/org/xbery/artbeams/courses/repository/ModuleRepository.kt).
 *
 * Regression coverage: the "New Module" link in moduleList.ftl always requests
 * ".../modules/0/edit" (AssetAttributes.EMPTY_ID), so a module id of "0" can reach save()
 * either via a normal request or via a stale/repeated form submission that bypasses the
 * ModuleAdminController.editForm sentinel check. resolveId must never treat "0" (or a
 * blank/null id) as an existing module id, otherwise every "new" module would collide on
 * the same literal id and each subsequent save would silently overwrite the previous one
 * instead of inserting a new row.
 */
class ModuleRepositoryTest :
    StringSpec({
        val repository = ModuleRepository(mockk(relaxed = true), mockk<ModuleMapper>(relaxed = true))

        "resolveId generates a fresh id for a null id" {
            repository.resolveId(EditedModule(null, "Title", null, null, null)) shouldNotBe null
        }

        "resolveId generates a fresh id for a blank id" {
            val generated = repository.resolveId(EditedModule("", "Title", null, null, null))
            generated shouldNotBe ""
        }

        "resolveId generates a fresh id for the EMPTY_ID sentinel '0'" {
            val generated = repository.resolveId(EditedModule(AssetAttributes.EMPTY_ID, "Title", null, null, null))
            generated shouldNotBe AssetAttributes.EMPTY_ID
        }

        "resolveId two calls with EMPTY_ID never collide" {
            val first = repository.resolveId(EditedModule(AssetAttributes.EMPTY_ID, "Title", null, null, null))
            val second = repository.resolveId(EditedModule(AssetAttributes.EMPTY_ID, "Title", null, null, null))
            first shouldNotBe second
        }

        "resolveId preserves an existing non-sentinel id" {
            repository.resolveId(EditedModule("m1", "Title", null, null, null)) shouldBe "m1"
        }
    })
