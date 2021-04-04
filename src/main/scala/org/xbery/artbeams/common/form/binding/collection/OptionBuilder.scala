package org.xbery.artbeams.common.form.binding.collection

import net.formio.binding.collection.CollectionBuilder

/**
 * Scala Option "collection" builder.
 * @author Radek Beran
 */
object OptionBuilder extends CollectionBuilder[Option[_]] {

  override def build[I](itemClass: Class[I], items: java.util.List[I]): Option[I] = {
    if (items == null || items.isEmpty) None
    else if (items.size() > 1) throw new IllegalStateException("Cannot convert more than one item to Option")
    else if (items.get(0) == null) None
    else {
      // this transparently converts also primitive Java type to corresponding Scala type:
      val value: I = items.get(0)
      Option(value)
    }
  }
}
