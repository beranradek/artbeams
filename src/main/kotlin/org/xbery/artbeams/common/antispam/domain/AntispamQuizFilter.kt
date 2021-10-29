package org.xbery.artbeams.common.antispam.domain

/**
 * @author Radek Beran
 */
data class AntispamQuizFilter(val question: String?) {
    companion object {
        val Empty: AntispamQuizFilter = AntispamQuizFilter(null)
    }
}
