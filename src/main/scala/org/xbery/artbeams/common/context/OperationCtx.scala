package org.xbery.artbeams.common.context

import org.xbery.artbeams.users.domain.User

/**
  * Operation context.
  * @author Radek Beran
  */
case class OperationCtx(
  loggedUser: Option[User]
)
