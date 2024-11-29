package org.xbery.artbeams.users.service

import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.users.domain.User

/**
 * @author Radek Beran
 */
interface UserSubscriptionService {

    /**
     * Confirms user's consent with personal data processing and sending of newsletters.
     * This may lead to new registration of user if the user with given e-mail = login does not exist yet.
     * This leads to creation of new order of given product for the user.
     *
     * @param fullName full name of user
     * @param login login = email of user
     * @param product product ordered with the registration to newsletter
     * @return user created or updated
     */
    fun createOrUpdateUserWithOrderAndConsent(
        fullName: String?,
        login: String,
        product: Product,
        orderNumber: String,
        orderState: OrderState
    ): User
}
