package org.xbery.artbeams.common.freemarker

import freemarker.template.Configuration


/**
 * FreeMarker configuration.
 * @author Radek Beran
 */
@org.springframework.context.annotation.Configuration
open class FreemarkerConfig(public val configuration: Configuration) {
    init {
        // Sets the object wrapper used to wrap objects to TemplateModel(s).
        configuration.objectWrapper = Java8ObjectWrapper(Configuration.VERSION_2_3_31)

        // This ensures <#import "/spring.ftl" as spring/> for all templates (internationalization using messages from properties bundle)
        configuration.setSetting(
            "auto_import",
            "spring.ftl as spring"
        ) // e.g.: 'spring.ftl as spring,layout/application.ftl as l,/macros/meh.ftl as meh'
    }
}
