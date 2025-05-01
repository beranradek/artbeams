package org.xbery.artbeams.orders.admin.service

import org.xbery.artbeams.orders.admin.CreateOrderData
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.users.domain.User

/**
 * Service for creating orders from administration.
 * @author Generated
 */
interface OrderCreatingAdminService {
    /**
     * Prepares default data for new order creation form.
     * @return data for the form
     */
    fun prepareNewOrderData(): CreateOrderData

    /**
     * Creates an order from administrator UI based on the form data.
     * @param createData order creation data from form
     * @return tuple with result (success/failure) and error message if failed
     */
    fun createOrder(createData: CreateOrderData): Pair<Boolean, String?>
    
    /**
     * Gets list of all users for the form.
     * @return list of users
     */
    fun findAllUsers(): List<User>
    
    /**
     * Gets list of all products for the form.
     * @return list of products
     */
    fun findAllProducts(): List<Product>
} 