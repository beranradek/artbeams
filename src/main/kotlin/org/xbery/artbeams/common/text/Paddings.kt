package org.xbery.artbeams.common.text

/**
 * Padding of strings to given maximum length.
 *
 * @author Radek Beran
 */
object Paddings {
    private const val PADDING_CHAR_NONE = '\u0000'

    /**
     * Applies padding to given string up to specified number of characters.
     * If given string is shorter or same as maxPaddingLength, it will just return the original string.
     * Otherwise, it would be padded with "\0" character to have at least "maxPaddingLength" characters
     *
     * @param rawString raw string
     * @param maxPaddingLength max padding length
     * @return padded output
     */
    fun padding(rawString: String, maxPaddingLength: Int): String {
        if (rawString.length < maxPaddingLength) {
            val nPad = maxPaddingLength - rawString.length
            val result = StringBuilder(rawString)
            repeat(nPad) { result.append(PADDING_CHAR_NONE) }
            return result.toString()
        } else return rawString
    }
}
