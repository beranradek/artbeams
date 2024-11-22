package org.xbery.artbeams.common.antispam.domain

import org.xbery.artbeams.common.repository.IdentifiedEntity
import java.io.Serializable

/**
 * Antispam quiz.
 *
 * @author Radek Beran
 */
data class AntispamQuiz(
    val question: String,
    val answer: String
) : IdentifiedEntity, Serializable {

    override val id: String
        get() = question

    companion object {
        const val CacheName = "antispam_quizes"

        val Empty = AntispamQuiz(
                "",
                ""
        )
    }
}
