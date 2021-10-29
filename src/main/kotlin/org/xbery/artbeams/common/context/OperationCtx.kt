package org.xbery.artbeams.common.context

import org.xbery.artbeams.users.domain.User

/**
 * Operation context.
 *
 * @author Radek Beran
 */
data class OperationCtx(val loggedUser: User?)
