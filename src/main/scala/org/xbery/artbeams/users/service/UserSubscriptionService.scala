package org.xbery.artbeams.users.service

import java.time.Instant

/**
  * @author Radek Beran
  */
trait UserSubscriptionService {
  /**
    * Subscribe user with given full name and email to newsletters and send him/her a product
    * (as a reward).
    * @param fullName
    * @param email
    * @param productId
    */
  def subscribe(fullName: Option[String], email: String, productId: String): Unit

  /**
    * Confirms user's consent with personal data processing and sending of newsletters.
    * @param fullName full name of user
    * @param email email of user
    * @param productId product ordered with the registration to newsletter
    * @return time of consent confirmation if consent was successfully confirmed and stored
    */
  def confirmConsent(fullName: Option[String], email: String, productId: String): Option[Instant]
}
