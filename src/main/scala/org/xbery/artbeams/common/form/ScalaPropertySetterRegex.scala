package org.xbery.artbeams.common.form

import net.formio.binding.PropertyMethodRegex

/**
 * @author Radek Beran
 */
object ScalaPropertySetterRegex
  extends PropertyMethodRegex("([_a-zA-Z][_a-zA-Z0-9]*)", 1)
