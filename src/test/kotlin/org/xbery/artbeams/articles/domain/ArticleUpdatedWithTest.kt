package org.xbery.artbeams.articles.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.common.assets.domain.Validity
import java.util.Date

class ArticleUpdatedWithTest :
    StringSpec({

        fun article() = Article(
            common = AssetAttributes.EMPTY,
            validity = Validity.Empty,
            externalId = null,
            slug = "slug",
            title = "Title",
            image = null,
            perex = "",
            bodyEdited = "",
            body = "",
            keywords = "",
            showOnBlog = true,
            draft = false,
            editor = "markdown",
            courseId = null,
            moduleId = null
        )

        fun edited(courseId: String?, moduleId: String?) = EditedArticle(
            id = "a1",
            externalId = null,
            slug = "slug",
            title = "Title",
            image = null,
            file = null,
            perex = "",
            bodyEdited = "",
            editor = "markdown",
            validFrom = Date(),
            validTo = null,
            keywords = "",
            showOnBlog = true,
            draft = false,
            courseId = courseId,
            moduleId = moduleId,
            categories = emptyList()
        )

        "should keep assigned course and module" {
            val updated = article().updatedWith(edited("c1", "m1"), "<p></p>", "u1")

            updated.courseId shouldBe "c1"
            updated.moduleId shouldBe "m1"
        }

        "should store blank course and module from empty dropdown option as null" {
            val updated = article().updatedWith(edited("", " "), "<p></p>", "u1")

            updated.courseId shouldBe null
            updated.moduleId shouldBe null
        }

        "should keep null course and module" {
            val updated = article().updatedWith(edited(null, null), "<p></p>", "u1")

            updated.courseId shouldBe null
            updated.moduleId shouldBe null
        }
    })
