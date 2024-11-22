package org.xbery.artbeams.common.antispam.repository.mapper

import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.antispam.domain.AntispamQuiz
import org.xbery.artbeams.jooq.schema.tables.records.AntispamQuizRecord

/**
 * @author Radek Beran
 */
@Component
class AntispamQuizMapper : RecordMapper<AntispamQuizRecord, AntispamQuiz> {

    override fun map(record: AntispamQuizRecord): AntispamQuiz {
        return AntispamQuiz(
            question = requireNotNull(record.question),
            answer = requireNotNull(record.answer)
        )
    }
}
