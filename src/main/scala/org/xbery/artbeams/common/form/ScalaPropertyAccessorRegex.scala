package org.xbery.artbeams.common.form

import net.formio.binding.PropertyMethodRegex



/**
 * @author Radek Beran
 */
object ScalaPropertyAccessorRegex
  extends PropertyMethodRegex(FormConstants.SCALA_ACCESSOR_AND_SETTER_REGEX, 1)
