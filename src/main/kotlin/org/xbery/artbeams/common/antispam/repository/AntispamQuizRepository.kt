package org.xbery.artbeams.common.antispam.repository

import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Table
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.antispam.domain.AntispamQuiz
import org.xbery.artbeams.common.antispam.repository.mapper.AntispamQuizMapper
import org.xbery.artbeams.common.antispam.repository.mapper.AntispamQuizUnmapper
import org.xbery.artbeams.common.repository.AbstractMappingRepository
import org.xbery.artbeams.common.text.NormalizationHelper
import org.xbery.artbeams.jooq.schema.tables.records.AntispamQuizRecord
import org.xbery.artbeams.jooq.schema.tables.references.ANTISPAM_QUIZ

/**
 * Repository for antispam questions and answers.
 * @author Radek Beran
 */
@Repository
class AntispamQuizRepository(
    override val dsl: DSLContext,
    override val mapper: AntispamQuizMapper,
    override val unmapper: AntispamQuizUnmapper
) : AbstractMappingRepository<AntispamQuiz, AntispamQuizRecord>(dsl, mapper, unmapper) {
    override val table: Table<AntispamQuizRecord> = ANTISPAM_QUIZ
    override val idField: Field<String?> = ANTISPAM_QUIZ.QUESTION

    private val rnd = java.util.Random()
    private val normalizationHelper: NormalizationHelper = NormalizationHelper()

    @Cacheable(AntispamQuiz.CacheName)
    override fun findAll(): List<AntispamQuiz> {
        return super.findAll()
    }

    /**
     * Returns randomly selected antispam quiz.
     * @return
     */
    fun findRandom(): AntispamQuiz {
        val quizes = findAll()
        if (quizes.isEmpty()) {
            throw IllegalStateException("No antispam quizes available, please fill in some in antispam_quiz table!")
        }
        val count = quizes.size
        val randIndex = rnd.nextInt(count)
        return quizes[randIndex]
    }

    fun findByQuestion(question: String): AntispamQuiz? =
        dsl.selectFrom(table)
            .where(ANTISPAM_QUIZ.QUESTION.eq(question))
            .fetchOne(mapper)

    /**
     * Returns true if specified question has given answer.
     * @param question
     * @param answer
     * @return
     */
    fun questionHasAnswer(question: String, answer: String): Boolean {
        var answerNormalized: String = answer.trim()
        return if (answerNormalized.isEmpty()) {
            false
        } else {
            if (answerNormalized.endsWith(".")) {
                answerNormalized =
                    answerNormalized.substring(0, answerNormalized.length - 1)
            }
            val quiz = findByQuestion(question)
            if (quiz != null) {
                normalizationHelper.normalize(quiz.answer) == normalizationHelper.normalize(answerNormalized)
            } else {
                false
            }
        }
    }
}
