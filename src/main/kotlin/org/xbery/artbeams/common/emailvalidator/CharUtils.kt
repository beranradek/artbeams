package org.xbery.artbeams.common.emailvalidator

import java.io.PrintWriter
import java.io.StringWriter
import kotlin.math.min

/**
 * Created by TPa on 13.2.17.
 */
object CharUtils {
    /**
     * #
     */
    @Suppress("unused")
    fun isNumberSign(ch: Char): Boolean {
        return ch.code == 35
    }

    @Suppress("unused")
    fun isDollarSign(ch: Char): Boolean {
        return ch.code == 36
    }

    @Suppress("unused")
    fun isPercentSign(ch: Char): Boolean {
        return ch.code == 37
    }

    @Suppress("unused")
    fun isAmpersand(ch: Char): Boolean {
        return ch.code == 38
    }

    @Suppress("unused")
    fun isDomainAllowedCharacter(ch: Char): Boolean {
        return isAsciiDigit(ch) || isNumber(ch) || isDot(ch) || isHyphen(ch)
    }

    /**
     * Constructs map from given array.
     *
     * @param array array to convert. Each inner array must contain 2 elements
     * (if less, an ArrayIndexOutOfBoundsException is thrown,
     * if more, extra elements are ignored).
     * @return new map containing the same key-value pairs as given array
     */
    fun <K, V> toMap(array: Array<Array<Any>>): Map<K, V> {
        val retval: MutableMap<K, V> = HashMap()
        for (pair in array) {
            val key = pair[0] as K
            val value = pair[1] as V
            retval[key] = value
        }
        return retval
    }

    /**
     * Constructs map from given array.
     * The iterator of the map preserves ordering of elements in array.
     *
     * @param array array to convert. Each inner array must contain 2 elements
     * (if less, an ArrayIndexOutOfBoundsException is thrown,
     * if more, extra elements are ignored).
     * @return new map (LinkedHashMap implementation)
     * containing the same key-value pairs as given array
     */
    fun <K, V> toOrderedMap(array: Array<Array<Any>>): Map<K, V> {
        val retval: MutableMap<K, V> = LinkedHashMap()
        for (pair in array) {
            val key = pair[0] as K
            val value = pair[1] as V
            retval[key] = value
        }
        return retval
    }

    fun exceptionToString(e: Throwable): String {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.buffer.toString()
    }

    fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
        val len0 = lhs.length + 1
        val len1 = rhs.length + 1

        // the array of distances
        var cost = IntArray(len0)
        var newcost = IntArray(len0)

        // initial cost of skipping prefix in String s0
        for (i in 0..<len0) cost[i] = i

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (j in 1..<len1) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j

            // transformation cost for each letter in s0
            for (i in 1..<len0) {
                // matching current letters in both strings
                val match = if (lhs[i - 1] == rhs[j - 1]) 0 else 1

                // computing cost for each transformation
                val cost_replace = cost[i - 1] + match
                val cost_insert = cost[i] + 1
                val cost_delete = newcost[i - 1] + 1

                // keep minimum cost
                newcost[i] =
                    min(min(cost_insert.toDouble(), cost_delete.toDouble()), cost_replace.toDouble()).toInt()
            }

            // swap cost/newcost arrays
            val swap = cost
            cost = newcost
            newcost = swap
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1]
    }


    //*******************************************************************************************/
    //*** detekce znaku **** http://www.ascii.cl/htmlcodes.htm **********************************/
    //*******************************************************************************************/
    /**
     * mezera/space
     */
    fun isSpace(ch: Char): Boolean {
        return ch.code == 32
    }

    /**
     * ! vykricnik ASCII 33
     */
    private fun isExclamationPoint(ch: Char): Boolean {
        return ch.code == 33
    }

    /**
     * "
     */
    fun isDoubleQuote(ch: Char): Boolean {
        return ch.code == 34
    }

    /**
     * (
     */
    private fun isOpeningParenthesis(ch: Char): Boolean {
        return ch.code == 40
    }

    /**
     * )
     */
    private fun isClosingParenthesis(ch: Char): Boolean {
        return ch.code == 41
    }

    private fun isAsterisk(ch: Char): Boolean {
        return ch.code == 42
    }

    private fun isPlusSign(ch: Char): Boolean {
        return ch.code == 43
    }

    /**
     * ,
     */
    private fun isComma(ch: Char): Boolean {
        return ch.code == 44
    }

    /**
     * Character - (minus, hyphen, spojovnik) (ASCII: 45)
     */
    fun isHyphen(ch: Char): Boolean {
        return ch.code == 45
    }

    /**
     * Character . (dot, period, full stop, tecka) (ASCII: 46)
     */
    fun isDot(ch: Char): Boolean {
        return ch.code == 46
    }

    private fun isSlash(ch: Char): Boolean {
        return ch.code == 47
    }

    /**
     * Digits 0 to 9 (ASCII: 48-57)
     */
    fun isNumber(ch: Char): Boolean {
        return ch.code in 48..57
    }

    private fun isColon(ch: Char): Boolean {
        return ch.code == 58
    }

    private fun isSemicolon(ch: Char): Boolean {
        return ch.code == 59
    }

    private fun isLessThanSign(ch: Char): Boolean {
        return ch.code == 60
    }

    private fun isEqualSign(ch: Char): Boolean {
        return ch.code == 61
    }

    private fun isGreaterThanSign(ch: Char): Boolean {
        return ch.code == 62
    }

    private fun isQuestionMark(ch: Char): Boolean {
        return ch.code == 63
    }

    fun isAt(ch: Char): Boolean {
        return ch.code == 64
    }

    /**
     * (a-z) (ASCII: 65-90)
     */
    private fun isAsciiLowerCaseDigit(ch: Char): Boolean {
        return ch.code in 65..90
    }

    private fun isOpeningBracket(ch: Char): Boolean {
        return ch.code == 91
    }

    fun isBackSlash(ch: Char): Boolean {
        return ch.code == 92
    }

    private fun isClosingBracket(ch: Char): Boolean {
        return ch.code == 93
    }

    /**
     * (A-Z) (ASCII: 97-122)
     */
    private fun isAsciiUpperCaseDigit(ch: Char): Boolean {
        return ch.code in 97..122
    }

    /**
     * (a–z, A–Z) (ASCII: 65-90, 97-122)
     */
    fun isAsciiDigit(ch: Char): Boolean {
        return isAsciiLowerCaseDigit(ch) || isAsciiUpperCaseDigit(ch)
    }


    /**
     * Characters !#$%&'*+-/=?^_`{|}~ (ASCII: 33, 35-39, 42, 43, 45, 47, 61, 63, 94-96, 123-126)
     */
    fun isNameSpecialCharacter(ch: Char): Boolean {
        return isHyphen(ch) //'-'
                || isExclamationPoint(ch) //'!'
                || (ch.code in 35..39) //#$%&' 35-39
                || isAsterisk(ch) //'*'
                || isPlusSign(ch) //'+'
                || isSlash(ch) //'/'
                || isEqualSign(ch) //'='
                || isQuestionMark(ch) //'?'
                || (ch.code in 94..96) //^_` 94-96
                || (ch.code in 123..126) //{|}~ 123-126
    }

    /**
     * space and "(),:;<>@[\]  (ASCII: 34, 40-41, 44, 58-59, 60, 62, 64, 91-93)
     */
    fun isNameQuotedSpecialCharacter(ch: Char): Boolean {
        return isSpace(ch)
                || isOpeningParenthesis(ch)
                || isClosingParenthesis(ch)
                || isComma(ch)
                || isColon(ch)
                || isSemicolon(ch)
                || isLessThanSign(ch)
                || isGreaterThanSign(ch)
                || isAt(ch)
                || isOpeningBracket(ch)
                || isBackSlash(ch)
                || isClosingBracket(ch)
    }
}
