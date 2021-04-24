package org.xbery.artbeams.common.form.binding.collection

import net.formio.binding.collection.CollectionBuilder

import scala.jdk.CollectionConverters._

/**
 * Builds Scala sequence.
 * @author Radek Beran
 */
object SeqBuilder extends CollectionBuilder[Seq[_]] {

  override def build[I](itemClass: Class[I], items: java.util.List[I]): Seq[I] = {
    if (items == null || items.isEmpty) Seq.empty[I]
    // TODO: Traverse and convert individual items like in OptionBuilder
    else items.asScala.toSeq
  }
}
