package org.xbery.artbeams.users.service

import java.time.Instant

import org.xbery.artbeams.common.context.OperationCtx
import org.xbery.artbeams.users.domain.{EditedUser, User}

/**
  * Operations with user.
  * @author Radek Beran
  */
trait UserService {
  def saveUser(edited: EditedUser)(implicit ctx: OperationCtx): Option[User]

  def findByEmail(email: String): Option[User]

  def findById(userId: String): Option[User]

  def updateUser(user: User): User

  /**
    * Confirms user's consent with personal data processing and sending of newsletters.
    * @param email email of user
    * @return time of consent confirmation if consent was successfully confirmed and stored
    */
  def confirmConsent(email: String): Option[Instant]
}
