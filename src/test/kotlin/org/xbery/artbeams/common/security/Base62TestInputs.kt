package org.xbery.artbeams.common.security

/**
 * @author Sebastian Ruhleder, sebastian@seruco.io (original author)
 */
object Base62TestInputs {
    val rawInputs: Array<ByteArray>
        get() = arrayOf(
            createIncreasingByteArray(),
            createZeroesByteArray(1),
            createZeroesByteArray(512),
            createPseudoRandomByteArray(0xAB, 40),
            createPseudoRandomByteArray(0x1C, 40),
            createPseudoRandomByteArray(0xF2, 40)
        )

    val naiveTestSet: Map<String, String>
        get() {
            val testSet: MutableMap<String, String> = HashMap()

            testSet[""] = ""
            testSet["a"] = "1Z"
            testSet["Hello"] = "5TP3P3v"
            testSet["Hello world!"] = "T8dgcjRGuYUueWht"
            testSet["Just a test"] = "7G0iTmJjQFG2t6K"
            testSet["!!!!!!!!!!!!!!!!!"] = "4A7f43EVXQoS6Am897ZKbAn"
            testSet["0123456789"] = "18XU2xYejWO9d3"
            testSet["The quick brown fox jumps over the lazy dog"] = "83UM8dOjD4xrzASgmqLOXTgTagvV1jPegUJ39mcYnwHwTlzpdfKXvpp4RL"
            testSet["Sphinx of black quartz, judge my vow"] = "1Ul5yQGNM8YFBp3sz19dYj1kTp95OW7jI8pTcTP5JhYjIaFmx"

            return testSet
        }

    val wrongEncoding: Array<ByteArray>
        get() {
            return arrayOf(
                "&".toByteArray(),
                "abcde$".toByteArray(),
                "()".toByteArray(),
                "\uD83D\uDE31".toByteArray()
            )
        }

    private fun createIncreasingByteArray(): ByteArray {
        val arr = ByteArray(256)
        for (i in 0..255) {
            arr[i] = (i and 0xFF).toByte()
        }
        return arr
    }

    private fun createZeroesByteArray(size: Int): ByteArray {
        return ByteArray(size)
    }

    private fun createPseudoRandomByteArray(seed: Int, size: Int): ByteArray {
        val arr: ByteArray = ByteArray(size)
        var state: Int = seed
        var i: Int = 0
        while (i < size) {
            state = xorshift(state)
            var j: Int = 0
            while (j < 4 && i + j < size) {
                arr[i + j] = ((state shr j) and 0xFF).toByte()
                j++
            }
            i += 4
        }
        return arr
    }

    private fun xorshift(x: Int): Int {
        var xx: Int = x
        xx = xx xor (xx shl 13)
        xx = xx xor (xx shr 17)
        xx = xx xor (xx shl 5)
        return xx
    }
}
