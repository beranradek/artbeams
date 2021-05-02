package org.xbery.artbeams.common.antispam.domain

/**
  * Antispam quiz.
  *
  * @author Radek Beran
  */
@SerialVersionUID(-1L)
case class AntispamQuiz(
  question: String,
  answer: String,
) extends Serializable

object AntispamQuiz {
  lazy val Empty = AntispamQuiz(
    "",
    ""
  )
}
