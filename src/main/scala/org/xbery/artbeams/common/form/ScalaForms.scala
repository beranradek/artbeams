package org.xbery.artbeams.common.form

import org.xbery.artbeams.common.form.binding.collection.ScalaCollectionBuilders
import org.xbery.artbeams.common.form.format.ScalaFormatters
import net.formio.binding.collection.{CollectionSpec, ItemsOrder}
import net.formio.format.Location
import net.formio.{Config, Forms}

/**
 * API for form definition and processing in Scala language.
 * @author Radek Beran
 */
object ScalaForms {

  lazy val DateTimePattern = "d.M.yyyy HH:mm"

  /** Returns form configuration builder with defaults for Scala language. */
  def config(): Config.Builder =
    Forms.config()
      .collectionBuilders(new ScalaCollectionBuilders)
      .defaultInstantiator(new CaseClassInstantiator)
      .accessorRegex(ScalaPropertyAccessorRegex)
      .setterRegex(ScalaPropertySetterRegex)
      .formatters(new ScalaFormatters)
      .listMappingCollection(CollectionSpec.getInstance(classOf[Seq[_]], ItemsOrder.LINEAR))

  lazy val CzConfig = ScalaForms.config()
    .location(Location.CZECH) // TODO RBe: Configure
    .build()
}
