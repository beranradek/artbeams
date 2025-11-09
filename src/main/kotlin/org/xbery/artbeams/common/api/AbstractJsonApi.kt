package org.xbery.artbeams.common.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.xbery.artbeams.common.json.ObjectMappers

/**
 * Base class for APIs.
 *
 * @author Radek Beran
 */
abstract class AbstractJsonApi(
    protected val apiName: String,
    protected val restTemplate: RestTemplate,
    protected val objectMapper: ObjectMapper = ObjectMappers.DEFAULT_MAPPER
) {

    protected val logger: Logger = LoggerFactory.getLogger(apiName)

    protected open fun createRequestEntity(requestData: Any?): HttpEntity<*> {
        val headers = HttpHeaders()
        appendHeaders(headers)
        if (requestData == null) {
            return HttpEntity(null, headers)
        }
        val value = objectMapper.writeValueAsString(requestData)
        return HttpEntity(value, headers)
    }

    /**
     * Con be overridden in subclasses for adding additional headers. Super implementation should be called.
     */
    protected open fun appendHeaders(headers: HttpHeaders) {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
    }

    /**
     * Sends HTTP request with given request data serialized to JSON by Jackson library.
     * Returns response body converted from JSON to object of given response class by Jackson library.
     *
     * @throws HttpStatusCodeException for Unauthorized, Forbidden, BadRequest, BadGateway and other situations based on unsuccessful response code
     * @throws RestClientException for a server error response, as determined via ResponseErrorHandler.hasError(ClientHttpResponse),
     * failure to decode the response, or a low level I/O error.
     */
    protected open fun <T> exchangeData(
        method: HttpMethod,
        uri: String,
        uriVariables: Map<String, Any>,
        requestData: Any?,
        responseClass: Class<T>
    ): T {
        return fromJson(exchangeEntity(method, uri, uriVariables, createRequestEntity(requestData)), responseClass)
    }

    /**
     * Sends HTTP request with given request entity.
     * Returns response body converted from JSON to object of given response class by Jackson library.
     *
     * @throws HttpStatusCodeException for Unauthorized, Forbidden, BadRequest, BadGateway and other situations based on unsuccessful response code
     * @throws RestClientException for a server error response, as determined via ResponseErrorHandler.hasError(ClientHttpResponse),
     * failure to decode the response, or a low level I/O error.
     */
    protected open fun <T> exchangeEntity(
        method: HttpMethod,
        uri: String,
        uriVariables: Map<String, Any>,
        requestEntity: HttpEntity<*>,
        responseClass: Class<T>
    ): T {
        return fromJson(exchangeEntity(method, uri, uriVariables, requestEntity), responseClass)
    }

    /**
     * Sends HTTP request with given request entity. Returns response entity.
     *
     * @throws HttpStatusCodeException for Unauthorized, Forbidden, BadRequest, BadGateway and other situations based on unsuccessful response code
     * @throws RestClientException for a server error response, as determined via ResponseErrorHandler.hasError(ClientHttpResponse),
     * failure to decode the response, or a low level I/O error.
     */
    protected open fun exchangeEntity(
        method: HttpMethod,
        uri: String,
        uriVariables: Map<String, Any>,
        requestEntity: HttpEntity<*>,
    ): ResponseEntity<String> {
        return try {
            val response = restTemplate.exchange(uri, method, requestEntity, String::class.java, uriVariables)
            logger.info("Successful call to " + uri +
                    " with status code " + response.statusCode +
                    ", response: " + response.body)
            response
        } catch (e: HttpStatusCodeException) {
            logger.error("Unsuccessful call to " + uri +
                    " with status code " + e.statusCode +
                    ", message " + e.mostSpecificCause.message +
                    ", response: " + e.responseBodyAsString,
                e.mostSpecificCause)
            throw e
        } catch (e: RestClientException) {
            logger.error("Error while calling " + uri + ": " + e.mostSpecificCause.message, e.mostSpecificCause)
            throw e
        }
    }

    /**
     * Converts HTTP response entity from JSON to provided class.
     *
     * @param <T>    type of response
     * @param entity HTTP response entity
     * @param cls    java type of the response
     * @return response converted from JSON
    </T> */
    protected open fun <T> fromJson(entity: HttpEntity<String>, cls: Class<T>): T = objectMapper.readValue(entity.body, cls)
}
