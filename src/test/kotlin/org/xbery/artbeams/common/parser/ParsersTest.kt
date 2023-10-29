package org.xbery.artbeams.common.parser

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * @author Radek Beran
 */
internal class ParsersTest {

    @Test
    fun parseIntOpt() {
        assertEquals(2345, Parsers.parseIntOpt("2 345"))
        assertNull(Parsers.parseIntOpt("a b c d"))
    }

    @Test
    fun parseBoolean() {
        assertEquals(false, Parsers.parseBoolean("something"))
        assertEquals(true, Parsers.parseBoolean("on"))
        assertEquals(true, Parsers.parseBoolean("y"))
        assertEquals(true, Parsers.parseBoolean("yes"))
    }
}