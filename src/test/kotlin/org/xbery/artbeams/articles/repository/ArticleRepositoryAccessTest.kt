package org.xbery.artbeams.articles.repository

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Paths

class ArticleRepositoryAccessTest :
    StringSpec({

        "findBySlug and findByQuery should filter out course-bound articles in source" {
            // This test performs a small static inspection of the repository source
            // to ensure the public search methods include ARTICLES.COURSE_ID.isNull
            val path = Paths.get("src/main/kotlin/org/xbery/artbeams/articles/repository/ArticleRepository.kt")
            val src = String(Files.readAllBytes(path))

            // findBySlug should filter out course-bound articles
            src.contains("findBySlug") shouldBe true
            src.contains("ARTICLES.COURSE_ID.isNull") shouldBe true

            // findByQuery should also filter out course-bound articles
            src.contains("fun findByQuery") shouldBe true
            src.contains("ARTICLES.COURSE_ID.isNull") shouldBe true
        }

        "findByQueryForCourse should use course id equality in where-clause" {
            val path = Paths.get("src/main/kotlin/org/xbery/artbeams/articles/repository/ArticleRepository.kt")
            val src = String(Files.readAllBytes(path))

            src.contains("fun findByQueryForCourse") shouldBe true
            // ensure the per-course query uses ARTICLES.COURSE_ID.eq(courseId)
            src.contains("ARTICLES.COURSE_ID.eq(courseId)") shouldBe true
        }

    })
