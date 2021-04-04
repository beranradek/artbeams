package org.xbery.artbeams.categories.admin

import java.util.Date

import net.formio.{Field, FormMapping, Forms}
import org.xbery.artbeams.categories.domain.EditedCategory
import org.xbery.artbeams.common.form.ScalaForms

/**
  * Category edit form.
  * @author Radek Beran
  */
class CategoryForm {
  lazy val definition: FormMapping[EditedCategory] = {
    Forms.basic(classOf[EditedCategory], "category")
      .field("id", Field.HIDDEN)
      .field("slug", Field.TEXT)
      .field("title", Field.TEXT)
      .field("description", Field.TEXT)
      .field(Forms.field[Date]("validFrom", Field.DATE_TIME).pattern(ScalaForms.DateTimePattern).build())
      .field(Forms.field[Date]("validTo", Field.DATE_TIME).pattern(ScalaForms.DateTimePattern).build())
      .build(ScalaForms.CzConfig)
  }

}
