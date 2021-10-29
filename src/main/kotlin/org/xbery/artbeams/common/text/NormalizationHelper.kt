package org.xbery.artbeams.common.text

import java.nio.charset.StandardCharsets
import java.text.Normalizer

/**
 * @author Radek Beran
 */
open class NormalizationHelper {
    fun normalize(str: String): String {
        if (str == null || str.isEmpty()) return str
        val sb = StringBuilder()
        for (c: Char in str.toCharArray()) {
            if (Character.isLetterOrDigit(c)) sb.append(c)
        }
        return toAscii(removeDiacriticalMarks(sb.toString())).lowercase()
    }

    fun toSlug(str: String): String {
        if (str == null || str.isEmpty()) return str
        val sb = StringBuilder()
        for (c: Char in str.toCharArray()) {
            if (' ' == c) sb.append('-') else if (Character.isLetterOrDigit(c)) sb.append(c)
        }
        return toAscii(removeDiacriticalMarks(sb.toString())).lowercase()
    }

    fun normalizeForFilesystem(str: String): String {
        if (str == null || str.isEmpty()) return str
        val str2: String = str.replace(SpecialFilesystemCharsRegex,"")
        return removeDiacriticalMarks(str2)
    }

    fun toAscii(str: String): String {
        val b = str.toByteArray(StandardCharsets.US_ASCII)
        return String(b, StandardCharsets.UTF_8)
    }

    fun removeDiacriticalMarks(text: String): String {
        val s: String = Normalizer.normalize(text, Normalizer.Form.NFD)
        return s.replace(DiacriticalMarksRegex, "")
    }

    companion object {
        val SpecialFilesystemCharsRegex = Regex.fromLiteral("[<>:\"/\\\\\\|\\?\\*]")
        val DiacriticalMarksRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    }
}
