package org.xbery.artbeams.products.admin

import net.formio.{Field, FormMapping, Forms}
import org.xbery.artbeams.common.form.ScalaForms
import org.xbery.artbeams.products.domain.EditedProduct

/**
  * Product edit form.
  * @author Radek Beran
  */
class ProductForm {
  lazy val definition: FormMapping[EditedProduct] = {
    Forms.basic(classOf[EditedProduct], "product")
      .field("id", Field.HIDDEN)
      .field("slug", Field.TEXT)
      .field("title", Field.TEXT)
      .field("fileName", Field.TEXT)
      .build(ScalaForms.CzConfig)
  }

}
