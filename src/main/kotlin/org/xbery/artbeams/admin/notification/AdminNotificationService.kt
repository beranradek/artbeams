package org.xbery.artbeams.admin.notification

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.comments.domain.Comment
import org.xbery.artbeams.common.mailer.service.MailgunMailSender
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.orders.domain.Order
import org.xbery.artbeams.prices.domain.Price
import org.xbery.artbeams.users.repository.UserRepository
import java.text.NumberFormat
import java.util.*

/**
 * Service for sending email notifications to administrators about important events.
 *
 * @author Radek Beran
 */
@Service
class AdminNotificationService(
    private val mailSender: MailgunMailSender,
    private val appConfig: AppConfig,
    private val userRepository: UserRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Send notification to admin about new order.
     */
    fun sendNewOrderNotification(order: Order) {
        try {
            val adminEmail = appConfig.findConfig("admin.notification.email")
            if (adminEmail.isNullOrBlank()) {
                logger.debug("Admin notification email not configured, skipping new order notification")
                return
            }

            val user = userRepository.findById(order.common.createdBy)
            val userName = user?.let { "${it.firstName} ${it.lastName}".trim().ifEmpty { it.login } } ?: "Unknown user"
            val userEmail = user?.email ?: "N/A"

            val totalPrice = order.items.fold(Price.ZERO) { acc, item ->
                val itemTotal = Price(item.price.price * item.quantity.toBigDecimal(), item.price.currency)
                acc + itemTotal
            }
            val priceFormatted = totalPrice.format(Locale.of("cs", "CZ"))

            val itemsHtml = order.items.joinToString("") { item ->
                val itemTotal = Price(item.price.price * item.quantity.toBigDecimal(), item.price.currency)
                val itemPrice = itemTotal.format(Locale.of("cs", "CZ"))
                """
                <tr>
                    <td style="padding: 8px; border-bottom: 1px solid #dee2e6;">Produkt ID: ${item.productId}</td>
                    <td style="padding: 8px; border-bottom: 1px solid #dee2e6; text-align: center;">${item.quantity}</td>
                    <td style="padding: 8px; border-bottom: 1px solid #dee2e6; text-align: right;">$itemPrice</td>
                </tr>
                """.trimIndent()
            }

            val baseUrl = appConfig.findConfig("web.baseUrl") ?: "http://localhost:8080"
            val orderAdminUrl = "$baseUrl/admin/orders/${order.id}"

            val htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background-color: #f8f9fa; border-left: 4px solid #007bff; padding: 20px; margin-bottom: 20px;">
                        <h2 style="margin-top: 0; color: #007bff;">Nová objednávka</h2>
                        <p style="margin-bottom: 0; font-size: 16px;">V systému byla vytvořena nová objednávka.</p>
                    </div>

                    <div style="background-color: #ffffff; padding: 20px; border: 1px solid #dee2e6; border-radius: 5px; margin-bottom: 20px;">
                        <h3 style="margin-top: 0; color: #495057;">Detaily objednávky</h3>
                        <table style="width: 100%; margin-bottom: 15px;">
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">Číslo objednávky:</td>
                                <td style="padding: 5px 0;">${order.orderNumber}</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">Stav:</td>
                                <td style="padding: 5px 0;">${order.state}</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">Celková cena:</td>
                                <td style="padding: 5px 0; font-weight: bold; color: #28a745;">$priceFormatted</td>
                            </tr>
                        </table>

                        <h3 style="margin-top: 20px; color: #495057;">Zákazník</h3>
                        <table style="width: 100%; margin-bottom: 15px;">
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">Jméno:</td>
                                <td style="padding: 5px 0;">$userName</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">Email:</td>
                                <td style="padding: 5px 0;">$userEmail</td>
                            </tr>
                        </table>

                        <h3 style="margin-top: 20px; color: #495057;">Položky objednávky</h3>
                        <table style="width: 100%; border-collapse: collapse;">
                            <thead>
                                <tr style="background-color: #f8f9fa;">
                                    <th style="padding: 8px; border-bottom: 2px solid #dee2e6; text-align: left;">Produkt</th>
                                    <th style="padding: 8px; border-bottom: 2px solid #dee2e6; text-align: center;">Množství</th>
                                    <th style="padding: 8px; border-bottom: 2px solid #dee2e6; text-align: right;">Cena</th>
                                </tr>
                            </thead>
                            <tbody>
                                $itemsHtml
                            </tbody>
                        </table>
                    </div>

                    <div style="text-align: center; margin-bottom: 20px;">
                        <a href="$orderAdminUrl" style="display: inline-block; padding: 12px 30px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold;">Zobrazit objednávku v administraci</a>
                    </div>

                    <div style="padding: 15px; background-color: #f8f9fa; border-radius: 5px; font-size: 14px; color: #6c757d;">
                        <p style="margin: 0;">Tento email byl odeslán automaticky systémem ArtBeams CMS při vytvoření nové objednávky.</p>
                    </div>
                </body>
                </html>
            """.trimIndent()

            mailSender.sendMailWithHtml(
                recipientEmail = adminEmail,
                subject = "Nová objednávka č. ${order.orderNumber}",
                htmlBody = htmlBody
            )

            logger.info("Admin notification sent for new order ${order.id} (${order.orderNumber}) to $adminEmail")
        } catch (e: Exception) {
            logger.error("Failed to send admin notification for new order ${order.id}", e)
            // Don't throw - notification failure should not break order creation
        }
    }

    /**
     * Send notification to admin about spam comment detected.
     */
    fun sendSpamDetectedNotification(comment: Comment) {
        try {
            val adminEmail = appConfig.findConfig("admin.notification.email")
            if (adminEmail.isNullOrBlank()) {
                logger.debug("Admin notification email not configured, skipping spam detection notification")
                return
            }

            val baseUrl = appConfig.findConfig("web.baseUrl") ?: "http://localhost:8080"
            val commentAdminUrl = "$baseUrl/admin/comments"

            val htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin-bottom: 20px;">
                        <h2 style="margin-top: 0; color: #856404;">⚠️ Detekován spam komentář</h2>
                        <p style="margin-bottom: 0; font-size: 16px;">Systém detekoval potenciální spam komentář vyžadující kontrolu.</p>
                    </div>

                    <div style="background-color: #ffffff; padding: 20px; border: 1px solid #dee2e6; border-radius: 5px; margin-bottom: 20px;">
                        <h3 style="margin-top: 0; color: #495057;">Detaily komentáře</h3>
                        <table style="width: 100%; margin-bottom: 15px;">
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">Stav:</td>
                                <td style="padding: 5px 0;">
                                    <span style="background-color: #ffc107; color: #000; padding: 3px 8px; border-radius: 3px; font-size: 12px; font-weight: bold;">ČEKÁ NA SCHVÁLENÍ</span>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">Jméno:</td>
                                <td style="padding: 5px 0;">${comment.userName}</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">Email:</td>
                                <td style="padding: 5px 0;">${comment.email}</td>
                            </tr>
                            <tr>
                                <td style="padding: 5px 0; font-weight: bold;">IP adresa:</td>
                                <td style="padding: 5px 0;">${comment.ip}</td>
                            </tr>
                        </table>

                        <h3 style="margin-top: 20px; color: #495057;">Obsah komentáře</h3>
                        <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; border-left: 3px solid #6c757d;">
                            <p style="margin: 0; white-space: pre-wrap;">${comment.comment}</p>
                        </div>
                    </div>

                    <div style="text-align: center; margin-bottom: 20px;">
                        <a href="$commentAdminUrl" style="display: inline-block; padding: 12px 30px; background-color: #ffc107; color: #000; text-decoration: none; border-radius: 5px; font-weight: bold;">Spravovat komentáře v administraci</a>
                    </div>

                    <div style="padding: 15px; background-color: #f8f9fa; border-radius: 5px; font-size: 14px; color: #6c757d;">
                        <p style="margin: 0;">Tento email byl odeslán automaticky systémem ArtBeams CMS při detekci potenciálního spam komentáře. Prosím zkontrolujte komentář a rozhodněte o jeho schválení nebo zamítnutí.</p>
                    </div>
                </body>
                </html>
            """.trimIndent()

            mailSender.sendMailWithHtml(
                recipientEmail = adminEmail,
                subject = "⚠️ Spam komentář detekován",
                htmlBody = htmlBody
            )

            logger.info("Admin notification sent for spam comment ${comment.id} to $adminEmail")
        } catch (e: Exception) {
            logger.error("Failed to send admin notification for spam comment ${comment.id}", e)
            // Don't throw - notification failure should not break comment processing
        }
    }
}
