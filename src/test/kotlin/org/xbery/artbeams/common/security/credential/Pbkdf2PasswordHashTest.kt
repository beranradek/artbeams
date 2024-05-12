package org.xbery.artbeams.common.security.credential

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.xbery.artbeams.common.security.SecureTokens
import java.util.Base64

/**
 * Tests for [Pbkdf2PasswordHash].
 *
 * @author Radek Beran
 */
class Pbkdf2PasswordHashTest : ShouldSpec({
    val passwordHash = Pbkdf2PasswordHash()

    context("encodeToCredential") {
        should("return a valid credential for a given raw password and iterations") {
            val rawPassword = "password123"
            val iterations = 1000
            val credential = passwordHash.encodeToCredential(rawPassword, iterations)

            credential.credentialData.algorithm shouldBe Pbkdf2PasswordHash.PBKDF2_HMAC_SHA512_ALGORITHM
            credential.credentialData.hashIterations shouldBe iterations
            Base64.getDecoder().decode(credential.secretData.value).size shouldBe Pbkdf2PasswordHash.PBKDF2_HMAC_SHA512_KEY_SIZE / 8
        }
    }

    context("encodeToSerializedCredential") {
        should("return a serialized credential for a given raw password and iterations") {
            val rawPassword = "password123"
            val iterations = 1000
            val serializedCredential = passwordHash.encodeToSerializedCredential(rawPassword, iterations)

            serializedCredential.isNotBlank() shouldBe true
        }

        should("serialize random password to JSON string") {
            val rawPassword = SecureTokens.generate()
            val serializedCredential = passwordHash.encodeToSerializedCredential(rawPassword, Pbkdf2PasswordHash.PBKDF2_HMAC_SHA512_ITERATIONS)

            serializedCredential.isNotBlank() shouldBe true
            println("Pass $rawPassword serialized to credential: $serializedCredential")
            serializedCredential shouldContain "\"credentialData\""
            serializedCredential shouldContain "\"hashIterations\""
            serializedCredential shouldContain "\"algorithm\""
            serializedCredential shouldContain "\"PBKDF2WithHmacSHA512\""
            serializedCredential shouldContain "\"secretData\""
            serializedCredential shouldContain "\"salt\""
            serializedCredential shouldContain "\"type\""
        }
    }

    context("verify") {
        should("return true when the raw password matches the stored credential") {
            val rawPassword = "passwordABC"
            val iterations = 1000
            val credential = passwordHash.encodeToCredential(rawPassword, iterations)

            passwordHash.verify(rawPassword, credential) shouldBe true
        }

        should("return false when the raw password does not match the stored credential") {
            val rawPassword = "passwordABC"
            val wrongPassword = "wrongpassword"
            val iterations = 1000
            val credential = passwordHash.encodeToCredential(rawPassword, iterations)

            passwordHash.verify(wrongPassword, credential) shouldBe false
        }
    }
})
