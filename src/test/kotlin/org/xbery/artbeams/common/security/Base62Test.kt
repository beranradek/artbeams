package org.xbery.artbeams.common.security

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Tests for Base62 encoder and decoder.
 *
 * @author Sebastian Ruhleder, sebastian@seruco.io (original author)
 */
class Base62Test {
    private val standardEncoder = Base62.createInstance()

    private val encoders = arrayOf(
        Base62.createInstanceWithGmpCharacterSet(),
        Base62.createInstanceWithInvertedCharacterSet()
    )

    @Test
    @DisplayName("should preserve identity of simple byte arrays")
    fun preservesIdentity() {
        for (message in Base62TestInputs.rawInputs) {
            for (encoder in encoders) {
                val encoded = encoder.encode(message)
                val decoded = encoder.decode(encoded)

                Assertions.assertArrayEquals(message, decoded)
            }
        }
    }

    @Test
    @DisplayName("should produce encodings that only contain alphanumeric characters")
    fun alphaNumericOutput() {
        for (message in Base62TestInputs.rawInputs) {
            for (encoder in encoders) {
                val encoded = encoder.encode(message)
                val encodedStr = String(encoded)

                Assertions.assertTrue(isAlphaNumeric(encodedStr))
            }
        }
    }

    @Test
    @DisplayName("should be able to handle empty inputs")
    fun emptyInputs() {
        val empty = ByteArray(0)

        for (encoder in encoders) {
            val encoded = encoder.encode(empty)
            Assertions.assertArrayEquals(empty, encoded)

            val decoded = encoder.decode(empty)
            Assertions.assertArrayEquals(empty, decoded)
        }
    }

    @Test
    @DisplayName("should behave correctly on naive test set")
    fun naiveTestSet() {
        for ((key, value) in Base62TestInputs.naiveTestSet.entries) {
            Assertions.assertEquals(encode(key), value)
        }
    }

    @Test
    @DisplayName("should throw exception when input is not encoded correctly")
    fun wrongEncoding() {
        for (input in Base62TestInputs.wrongEncoding) {
            Assertions.assertThrows(
                IllegalArgumentException::class.java
            ) { standardEncoder.decode(input) }
        }
    }

    @Test
    @DisplayName("should check encoding correctly")
    fun checkEncoding() {
        Assertions.assertTrue(standardEncoder.isBase62Encoding("0123456789".toByteArray()))
        Assertions.assertTrue(standardEncoder.isBase62Encoding("abcdefghijklmnopqrstuvwxzy".toByteArray()))
        Assertions.assertTrue(standardEncoder.isBase62Encoding("ABCDEFGHIJKLMNOPQRSTUVWXZY".toByteArray()))

        Assertions.assertFalse(standardEncoder.isBase62Encoding("!".toByteArray()))
        Assertions.assertFalse(standardEncoder.isBase62Encoding("@".toByteArray()))
        Assertions.assertFalse(standardEncoder.isBase62Encoding("<>".toByteArray()))
        Assertions.assertFalse(standardEncoder.isBase62Encoding("abcd%".toByteArray()))
        Assertions.assertFalse(standardEncoder.isBase62Encoding("😱".toByteArray()))
    }

    private fun encode(input: String): String {
        return String(standardEncoder.encode(input.toByteArray()))
    }

    private fun isAlphaNumeric(str: String): Boolean {
        return str.matches("^[a-zA-Z0-9]+$".toRegex())
    }
}
