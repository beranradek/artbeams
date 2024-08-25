package org.xbery.artbeams.common.security

import java.io.ByteArrayOutputStream
import kotlin.math.ceil
import kotlin.math.ln

/**
 * Base62 encoder and decoder.
 *
 * @author Sebastian Ruhleder, sebastian@seruco.io (original author)
 */
class Base62 private constructor(private val alphabet: ByteArray) {
    private var lookup: ByteArray

    init {
        lookup = createLookupTable(alphabet)
    }

    /**
     * Encodes a sequence of bytes in Base62 encoding.
     *
     * @param message a byte sequence.
     * @return a sequence of Base62-encoded bytes.
     */
    fun encode(message: ByteArray): ByteArray {
        val indices = convert(message, STANDARD_BASE, TARGET_BASE)

        return translate(indices, alphabet)
    }

    /**
     * Decodes a sequence of Base62-encoded bytes.
     *
     * @param encoded a sequence of Base62-encoded bytes.
     * @return a byte sequence.
     * @throws IllegalArgumentException when `encoded` is not encoded over the Base62 alphabet.
     */
    fun decode(encoded: ByteArray): ByteArray {
        require(isBase62Encoding(encoded)) { "Input is not encoded correctly" }

        val prepared = translate(encoded, lookup)

        return convert(prepared, TARGET_BASE, STANDARD_BASE)
    }

    /**
     * Checks whether a sequence of bytes is encoded over a Base62 alphabet.
     *
     * @param bytes a sequence of bytes.
     * @return `true` when the bytes are encoded over a Base62 alphabet, `false` otherwise.
     */
    @Suppress("NestedBlockDepth", "ReturnCount")
    fun isBase62Encoding(bytes: ByteArray?): Boolean {
        if (bytes == null) {
            return false
        }

        for (e in bytes) {
            if ('0'.code.toByte() > e || '9'.code.toByte() < e) {
                if ('a'.code.toByte() > e || 'z'.code.toByte() < e) {
                    if ('A'.code.toByte() > e || 'Z'.code.toByte() < e) {
                        return false
                    }
                }
            }
        }

        return true
    }

    /**
     * Uses the elements of a byte array as indices to a dictionary and returns the corresponding values
     * in form of a byte array.
     */
    private fun translate(indices: ByteArray, dictionary: ByteArray): ByteArray {
        val translation = ByteArray(indices.size)

        for (i in indices.indices) {
            translation[i] = dictionary[indices[i].toInt()]
        }

        return translation
    }

    /**
     * Converts a byte array from a source base to a target base using the alphabet.
     */
    @Suppress("MagicNumber")
    private fun convert(message: ByteArray, sourceBase: Int, targetBase: Int): ByteArray {
        /**
         * This algorithm is inspired by: http://codegolf.stackexchange.com/a/21672
         */

        val estimatedLength = estimateOutputLength(message.size, sourceBase, targetBase)

        val out = ByteArrayOutputStream(estimatedLength)

        var source = message

        while (source.isNotEmpty()) {
            val quotient = ByteArrayOutputStream(source.size)

            var remainder = 0

            for (i in source.indices) {
                val accumulator = (source[i].toInt() and 0xFF) + remainder * sourceBase
                val digit = (accumulator - (accumulator % targetBase)) / targetBase

                remainder = accumulator % targetBase

                if (quotient.size() > 0 || digit > 0) {
                    quotient.write(digit)
                }
            }

            out.write(remainder)

            source = quotient.toByteArray()
        }

        // pad output with zeroes corresponding to the number of leading zeroes in the message
        var i = 0
        while (i < message.size - 1 && message[i].toInt() == 0) {
            out.write(0)
            i++
        }

        return reverse(out.toByteArray())
    }

    /**
     * Estimates the length of the output in bytes.
     */
    private fun estimateOutputLength(inputLength: Int, sourceBase: Int, targetBase: Int): Int {
        return ceil((ln(sourceBase.toDouble()) / ln(targetBase.toDouble())) * inputLength)
            .toInt()
    }

    /**
     * Reverses a byte array.
     */
    private fun reverse(arr: ByteArray): ByteArray {
        val length = arr.size

        val reversed = ByteArray(length)

        for (i in 0 until length) {
            reversed[length - i - 1] = arr[i]
        }

        return reversed
    }

    /**
     * Creates the lookup table from character to index of character in character set.
     */
    @Suppress("MagicNumber")
    private fun createLookupTable(alphabet: ByteArray): ByteArray {
        val lookupTable = ByteArray(256)

        for (i in alphabet.indices) {
            lookupTable[alphabet[i].toInt()] = (i and 0xFF).toByte()
        }
        return lookupTable
    }

    private object CharacterSets {
        val GMP: ByteArray = byteArrayOf(
            '0'.code.toByte(),
            '1'.code.toByte(),
            '2'.code.toByte(),
            '3'.code.toByte(),
            '4'.code.toByte(),
            '5'.code.toByte(),
            '6'.code.toByte(),
            '7'.code.toByte(),
            '8'.code.toByte(),
            '9'.code.toByte(),
            'A'.code.toByte(),
            'B'.code.toByte(),
            'C'.code.toByte(),
            'D'.code.toByte(),
            'E'.code.toByte(),
            'F'.code.toByte(),
            'G'.code.toByte(),
            'H'.code.toByte(),
            'I'.code.toByte(),
            'J'.code.toByte(),
            'K'.code.toByte(),
            'L'.code.toByte(),
            'M'.code.toByte(),
            'N'.code.toByte(),
            'O'.code.toByte(),
            'P'.code.toByte(),
            'Q'.code.toByte(),
            'R'.code.toByte(),
            'S'.code.toByte(),
            'T'.code.toByte(),
            'U'.code.toByte(),
            'V'.code.toByte(),
            'W'.code.toByte(),
            'X'.code.toByte(),
            'Y'.code.toByte(),
            'Z'.code.toByte(),
            'a'.code.toByte(),
            'b'.code.toByte(),
            'c'.code.toByte(),
            'd'.code.toByte(),
            'e'.code.toByte(),
            'f'.code.toByte(),
            'g'.code.toByte(),
            'h'.code.toByte(),
            'i'.code.toByte(),
            'j'.code.toByte(),
            'k'.code.toByte(),
            'l'.code.toByte(),
            'm'.code.toByte(),
            'n'.code.toByte(),
            'o'.code.toByte(),
            'p'.code.toByte(),
            'q'.code.toByte(),
            'r'.code.toByte(),
            's'.code.toByte(),
            't'.code.toByte(),
            'u'.code.toByte(),
            'v'.code.toByte(),
            'w'.code.toByte(),
            'x'.code.toByte(),
            'y'.code.toByte(),
            'z'.code.toByte()
        )

        val INVERTED: ByteArray = byteArrayOf(
            '0'.code.toByte(),
            '1'.code.toByte(),
            '2'.code.toByte(),
            '3'.code.toByte(),
            '4'.code.toByte(),
            '5'.code.toByte(),
            '6'.code.toByte(),
            '7'.code.toByte(),
            '8'.code.toByte(),
            '9'.code.toByte(),
            'a'.code.toByte(),
            'b'.code.toByte(),
            'c'.code.toByte(),
            'd'.code.toByte(),
            'e'.code.toByte(),
            'f'.code.toByte(),
            'g'.code.toByte(),
            'h'.code.toByte(),
            'i'.code.toByte(),
            'j'.code.toByte(),
            'k'.code.toByte(),
            'l'.code.toByte(),
            'm'.code.toByte(),
            'n'.code.toByte(),
            'o'.code.toByte(),
            'p'.code.toByte(),
            'q'.code.toByte(),
            'r'.code.toByte(),
            's'.code.toByte(),
            't'.code.toByte(),
            'u'.code.toByte(),
            'v'.code.toByte(),
            'w'.code.toByte(),
            'x'.code.toByte(),
            'y'.code.toByte(),
            'z'.code.toByte(),
            'A'.code.toByte(),
            'B'.code.toByte(),
            'C'.code.toByte(),
            'D'.code.toByte(),
            'E'.code.toByte(),
            'F'.code.toByte(),
            'G'.code.toByte(),
            'H'.code.toByte(),
            'I'.code.toByte(),
            'J'.code.toByte(),
            'K'.code.toByte(),
            'L'.code.toByte(),
            'M'.code.toByte(),
            'N'.code.toByte(),
            'O'.code.toByte(),
            'P'.code.toByte(),
            'Q'.code.toByte(),
            'R'.code.toByte(),
            'S'.code.toByte(),
            'T'.code.toByte(),
            'U'.code.toByte(),
            'V'.code.toByte(),
            'W'.code.toByte(),
            'X'.code.toByte(),
            'Y'.code.toByte(),
            'Z'.code.toByte()
        )
    }

    companion object {
        private const val STANDARD_BASE = 256

        private const val TARGET_BASE = 62

        /**
         * Creates a [Base62] instance. Defaults to the GMP-style character set.
         *
         * @return a [Base62] instance.
         */
        fun createInstance(): Base62 {
            return createInstanceWithGmpCharacterSet()
        }

        /**
         * Creates a [Base62] instance using the GMP-style character set.
         *
         * @return a [Base62] instance.
         */
        fun createInstanceWithGmpCharacterSet(): Base62 {
            return Base62(CharacterSets.GMP)
        }

        /**
         * Creates a [Base62] instance using the inverted character set.
         *
         * @return a [Base62] instance.
         */
        fun createInstanceWithInvertedCharacterSet(): Base62 {
            return Base62(CharacterSets.INVERTED)
        }
    }
}
