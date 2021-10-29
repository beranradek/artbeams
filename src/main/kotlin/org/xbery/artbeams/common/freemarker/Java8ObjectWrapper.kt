package org.xbery.artbeams.common.freemarker

import freemarker.template.DefaultObjectWrapper
import freemarker.template.TemplateModel
import freemarker.template.Version
import java.time.Instant
import java.util.*

/**
 * Freemarker ObjectWrapper that extends the DefaultObjectWrapper with support for new java.time API.
 * @author Radek Beran
 */
class Java8ObjectWrapper : DefaultObjectWrapper {
    constructor(incompatibleImprovements: Version?) : super(incompatibleImprovements)

    override fun handleUnknownType(obj: Any?): TemplateModel {
        return if (obj is Instant) {
            super.wrap(Date.from(obj))
        } else {
            super.handleUnknownType(obj)
        }
    }
}
