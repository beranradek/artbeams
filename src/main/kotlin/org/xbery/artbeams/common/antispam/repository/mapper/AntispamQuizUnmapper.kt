package org.xbery.artbeams.common.antispam.repository.mapper

import org.jooq.RecordUnmapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.antispam.domain.AntispamQuiz
import org.xbery.artbeams.jooq.schema.tables.records.AntispamQuizRecord
import org.xbery.artbeams.jooq.schema.tables.references.ANTISPAM_QUIZ

/**
 * @author Radek Beran
 */
@Component
class AntispamQuizUnmapper : RecordUnmapper<AntispamQuiz, AntispamQuizRecord> {

    override fun unmap(quiz: AntispamQuiz): AntispamQuizRecord {
        val record = ANTISPAM_QUIZ.newRecord()
        record.question = quiz.question
        record.answer = quiz.answer
        return record
    }
}
