package org.xbery.artbeams.common.antispam.repository

import org.xbery.artbeams.common.antispam.domain.{AntispamQuiz, AntispamQuizFilter}
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._

import java.util

/**
  * Maps {@link AntispamQuiz} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class AntispamQuizMapper() extends DynamicEntityMapper[AntispamQuiz, AntispamQuizFilter] {

  private val cls = classOf[AntispamQuiz]

  override val getTableName: String = "antispam_quiz"

  override def createEntity(): AntispamQuiz = AntispamQuiz.Empty

  val questionAttr = add(Attr.ofString(cls, "question").get(e => e.question).updatedEntity((e, a) => e.copy(question = a)).primary())
  val answerAttr = add(Attr.ofString(cls, "answer").get(e => e.answer).updatedEntity((e, a) => e.copy(answer = a)))

  override def composeFilterConditions(filter: AntispamQuizFilter): util.List[Condition] = {
    val conditions = new util.ArrayList[Condition]
    conditions
  }
}

object AntispamQuizMapper {
  lazy val Instance = new AntispamQuizMapper()
}
