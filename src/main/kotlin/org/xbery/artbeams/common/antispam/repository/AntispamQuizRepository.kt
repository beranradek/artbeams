package org.xbery.artbeams.common.antispam.repository

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.antispam.domain.AntispamQuiz
import org.xbery.artbeams.common.antispam.domain.AntispamQuizFilter
import org.xbery.artbeams.common.repository.ExtendedSqlRepository
import org.xbery.artbeams.common.text.NormalizationHelper
import javax.sql.DataSource

/**
 * Repository for antispam questions and answers.
 * @author Radek Beran
 */
@Repository
open class AntispamQuizRepository(dataSource: DataSource) :
    ExtendedSqlRepository<AntispamQuiz, String, AntispamQuizFilter>(dataSource, AntispamQuizMapper.Instance) {
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
    open fun findRandom(): AntispamQuiz {
        val quizes = findAll()
        if (quizes.isEmpty()) {
            throw IllegalStateException("No antispam quizes available, please fill in some in antispam_quiz table!")
        }
        val count = quizes.size
        val randIndex = rnd.nextInt(count)
        return quizes[randIndex]
    }

    open fun findByQuestion(question: String): AntispamQuiz? {
        val quizes = findAll()
        return quizes.find { q -> q.question == question }
    }

    /**
     * Returns true if specified question has given answer.
     * @param question
     * @param answer
     * @return
     */
    open fun questionHasAnswer(question: String, answer: String): Boolean {
        var answerNormalized: String = answer.trim()
        return if (answerNormalized.isEmpty()) {
            false
        } else {
            if (answerNormalized.endsWith(".")) {
                answerNormalized =
                    answerNormalized.substring(0, answerNormalized.length - 1)
            }
            val qOpt: AntispamQuiz? = findByQuestion(question)
            if (qOpt != null) {
                normalizationHelper.normalize(qOpt.answer) == normalizationHelper.normalize(answerNormalized)
            } else {
                false
            }
        }
    }
}
