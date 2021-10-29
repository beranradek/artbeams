package org.xbery.artbeams.common.mailer.service

import org.apache.http.HttpEntity
import org.apache.http.NameValuePair
import org.apache.http.StatusLine
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.mailer.config.MailerConfig

/**
 * Mailer for sending e-mails.
 * @author Radek Beran
 */
@Service
open class Mailer(private val mailerConfig: MailerConfig) {
    private val Logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun sendMail(subject: String, body: String, to: String): Unit {
        Logger.info("Sending email ${subject} to ${to}")
        val mailerApiUrl =
            "https://api:${mailerConfig.apiKey}@api.mailgun.net/v2/${mailerConfig.domain}/messages"
        val httpClient: CloseableHttpClient = HttpClients.createDefault()
        try {
            val httpPost = HttpPost(mailerApiUrl)
            val params = mutableListOf<NameValuePair>()
            params.add(BasicNameValuePair("from", mailerConfig.from))
            params.add(BasicNameValuePair("to", to))
            params.add(BasicNameValuePair("subject", subject))
            params.add(BasicNameValuePair("text", body))
            params.add(BasicNameValuePair("html", body))
            httpPost.entity = UrlEncodedFormEntity(params)

            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct de-allocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            val response = httpClient.execute(httpPost)
            try {
                val statusLine: StatusLine = response.statusLine
                val status: Int = statusLine.statusCode
                val responseEntity: HttpEntity = response.entity
                Logger.info("Response status: ${statusLine}")
                if (status >= 200 && status < 300 ) {
                    EntityUtils.consume(responseEntity)
                    Logger.info("Email ${subject} to ${to} was successfully sent.")
                } else {
                    val response: String =
                        if (responseEntity != null) EntityUtils.toString(responseEntity) else ""
                            Logger.error("Error while sending email ${subject} to ${to}. Unexpected response status ${status} with response ${response}")
                }
            } finally {
                if (response != null) {
                    response.close()
                }
            }
        } finally {
            httpClient.close()
        }
    }
}
