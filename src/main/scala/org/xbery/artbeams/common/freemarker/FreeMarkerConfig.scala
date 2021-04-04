package org.xbery.artbeams.common.freemarker

import org.springframework.context.annotation.Configuration

/**
 * FreeMarker configuration.
 * @author Radek Beran
 */
@Configuration
class FreemarkerConfig(val configuration: freemarker.template.Configuration) {
  // Sets the object wrapper used to wrap objects to TemplateModel(s).
  configuration.setObjectWrapper(new ScalaObjectWrapper())
  // This ensures <#import "/spring.ftl" as spring/> for all templates (internationalization using messages from properties bundle)
  configuration.setSetting("auto_import", "spring.ftl as spring") // e.g.: 'spring.ftl as spring,layout/application.ftl as l,/macros/meh.ftl as meh'
}
