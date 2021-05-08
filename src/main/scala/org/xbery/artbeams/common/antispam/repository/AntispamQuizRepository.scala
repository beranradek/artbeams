package org.xbery.artbeams.common.antispam.repository

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.antispam.domain.{AntispamQuiz, AntispamQuizFilter}
import org.xbery.artbeams.common.repository.ScalaSqlRepository
import org.xbery.artbeams.common.text.NormalizationHelper

import javax.inject.Inject
import javax.sql.DataSource
import scala.util.Random

/**
  * Repository for antispam questions and answers.
  * @author Radek Beran
  */
@Repository
class AntispamQuizRepository @Inject()(dataSource: DataSource) extends ScalaSqlRepository[AntispamQuiz, String, AntispamQuizFilter](dataSource, AntispamQuizMapper.Instance) {
  private val rnd = new Random()
  private val normalizationHelper = new NormalizationHelper()

  @Cacheable(Array(AntispamQuiz.CacheName))
  override def findAllAsSeq(): Seq[AntispamQuiz] = super.findAllAsSeq()

  /**
    * Returns randomly selected antispam quiz.
    * @return
    */
  def findRandom(): AntispamQuiz = {
    val quizes = findAllAsSeq()
    if (quizes.isEmpty) {
      throw new IllegalStateException("No antispam quizes available, please fill in some in antispam_quiz table!")
    }
    val count = quizes.size
    val randIndex = rnd.nextInt(count)
    quizes(randIndex)
  }

  def findByQuestion(question: String): Option[AntispamQuiz] = {
    val quizes = findAllAsSeq()
    quizes.find(q => q.question == question)
  }

  /**
    * Returns true if specified question has given answer.
    * @param question
    * @param answer
    * @return
    */
  def questionHasAnswer(question: String, answer: String): Boolean = {
    var answerNormalized = answer.trim
    if (answerNormalized.isEmpty) {
      false
    } else {
      if (answerNormalized.endsWith(".")) {
        answerNormalized = answerNormalized.substring(0, answerNormalized.length - 1)
      }
      val qOpt = findByQuestion(question)
      qOpt match {
        case Some(q) => normalizationHelper.normalize(q.answer) == normalizationHelper.normalize(answerNormalized)
        case None => false
      }
    }
  }
}
