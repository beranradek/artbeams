package org.xbery.artbeams.common.antispam.repository

import org.xbery.artbeams.common.antispam.domain.{AntispamQuiz, AntispamQuizFilter}
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper._
import org.xbery.overview.repo.Conditions

import java.util

/**
  * Maps {@link AntispamQuiz} entity to set of attributes and vice versa.
  * @author Radek Beran
  */
class AntispamQuizMapper() extends DynamicEntityMapper[AntispamQuiz, AntispamQuizFilter] {

  private val cls = classOf[AntispamQuiz]

  override val getTableName: String = "antispam_quiz"

  val questionAttr = add(Attr.ofString(cls, "question").get(e => e.question).primary())
  val answerAttr = add(Attr.ofString(cls, "answer").get(e => e.answer))

  override def createEntity(attributeSource: AttributeSource, attributes: java.util.List[Attribute[AntispamQuiz, _]], aliasPrefix: String): AntispamQuiz = {
    AntispamQuiz(
      questionAttr.getValueFromSource(attributeSource, aliasPrefix),
      answerAttr.getValueFromSource(attributeSource, aliasPrefix)
    )
  }

  override def composeFilterConditions(filter: AntispamQuizFilter): util.List[Condition] = {
    val conditions = new util.ArrayList[Condition]
    filter.question.map(question => conditions.add(Conditions.eq(this.questionAttr, question)))
    conditions
  }
}

object AntispamQuizMapper {
  lazy val Instance = new AntispamQuizMapper()
}
