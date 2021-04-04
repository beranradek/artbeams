package org.xbery.artbeams.articles.admin

import java.util.Date

import net.formio.{Field, FormMapping, Forms}
import org.xbery.artbeams.articles.domain.EditedArticle
import org.xbery.artbeams.common.form.ScalaForms

/**
  * Article edit form.
  * @author Radek Beran
  */
class ArticleForm {
  lazy val definition: FormMapping[EditedArticle] = {
    Forms.basic(classOf[EditedArticle], "article")
      .field("id", Field.HIDDEN)
      .field("externalId", Field.TEXT)
      .field("slug", Field.TEXT)
      .field("title", Field.TEXT)
      .field("image", Field.TEXT)
      .field("imageDetail", Field.TEXT)
      .field("perex", Field.TEXT_AREA)
      .field("bodyMarkdown", Field.TEXT_AREA)
      .field(Forms.field[Date]("validFrom", Field.DATE_TIME).pattern(ScalaForms.DateTimePattern).build())
      .field(Forms.field[Date]("validTo", Field.DATE_TIME).pattern(ScalaForms.DateTimePattern).build())
      .field("keywords", Field.TEXT)
      .field("showOnBlog", Field.CHECK_BOX)
      .field("categories", Field.DROP_DOWN_CHOICE)
      .build(ScalaForms.CzConfig)
  }

}
