package org.xbery.artbeams.common.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * JSON object mappers.
 *
 * @author Radek Beran
 */
class ObjectMappers {

    companion object {
        val DEFAULT_MAPPER = createObjectMapper()

        private fun createObjectMapper(): ObjectMapper {
            val objectMapper = jacksonObjectMapper()
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .registerModule(Jdk8Module())
                .registerModule(JavaTimeModule())
            return objectMapper
        }
    }
}