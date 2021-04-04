package org.xbery.artbeams.common.mailer.service

import javax.inject.Inject
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.xbery.artbeams.common.mailer.config.MailerConfig

/**
  * Mailer for sending emails.
  * @author Radek Beran
  */
@Component
class Mailer @Inject() (mailerConfig: MailerConfig) {

  private val Logger = LoggerFactory.getLogger(this.getClass)

  def sendMail(subject: String, body: String, to: String): Unit = {
    Logger.info(s"Sending email ${subject} to ${to}")
    val mailerApiUrl = s"https://api:${mailerConfig.apiKey}@api.mailgun.net/v2/${mailerConfig.domain}/messages"

    // Fluent API of HTTP client
    // See also https://hc.apache.org/httpcomponents-client-ga/quickstart.html

    val httpClient = HttpClients.createDefault()
    try {
      val httpPost = new HttpPost(mailerApiUrl)

      val params = new java.util.ArrayList[NameValuePair]
      params.add(new BasicNameValuePair("from", mailerConfig.from))
      params.add(new BasicNameValuePair("to", to))
      params.add(new BasicNameValuePair("subject", subject))
      params.add(new BasicNameValuePair("text", body))
      params.add(new BasicNameValuePair("html", body))

      httpPost.setEntity(new UrlEncodedFormEntity(params))

      // The underlying HTTP connection is still held by the response object
      // to allow the response content to be streamed directly from the network socket.
      // In order to ensure correct de-allocation of system resources
      // the user MUST call CloseableHttpResponse#close() from a finally clause.
      // Please note that if response content is not fully consumed the underlying
      // connection cannot be safely re-used and will be shut down and discarded
      // by the connection manager.
      val response = httpClient.execute(httpPost)
      try {
        val statusLine = response.getStatusLine()
        val status = statusLine.getStatusCode()
        val responseEntity = response.getEntity()
        Logger.info(s"Response status: ${statusLine}")

        if (status >= 200 && status < 300) {
          EntityUtils.consume(responseEntity)
          Logger.info(s"Email ${subject} to ${to} was successfully sent.")
        } else {
          val response = if (responseEntity != null) EntityUtils.toString(responseEntity) else "";
          Logger.error(s"Error while sending email ${subject} to ${to}. Unexpected response status ${status} with response ${response}")
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
