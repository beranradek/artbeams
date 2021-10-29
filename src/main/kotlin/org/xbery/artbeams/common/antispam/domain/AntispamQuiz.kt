package org.xbery.artbeams.common.antispam.domain

import java.io.Serializable

/**
 * Antispam quiz.
 *
 * @author Radek Beran
 */
data class AntispamQuiz(
    val question: String,
    val answer: String
) : Serializable {

    companion object {
        const val CacheName = "antispam_quizes"

        val Empty = AntispamQuiz(
                "",
                ""
        )
    }
}
