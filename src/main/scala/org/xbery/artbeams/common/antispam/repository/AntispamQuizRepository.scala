package org.xbery.artbeams.common.antispam.repository

import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.antispam.domain.{AntispamQuiz, AntispamQuizFilter}
import org.xbery.artbeams.common.repository.ScalaSqlRepository
import org.xbery.overview.{Overview, Pagination}

import java.util.Collections
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

  def findRandom(): AntispamQuiz = {
    val filter = AntispamQuizFilter()
    val count = countByFilter(filter, Collections.emptyList())
    val randOffset = rnd.nextInt(count)
    val pagination = new Pagination(randOffset, 1)
    val overview = new Overview(filter, Collections.emptyList(), pagination)
    val quizes = findByOverviewAsSeq(overview)
    if (quizes.isEmpty) {
      throw new IllegalStateException("No antispam quizes available")
    }
    quizes.head
  }
}
