package org.xbery.artbeams.orders.admin.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.error.requireFound
import org.xbery.artbeams.orders.admin.CreateOrderData
import org.xbery.artbeams.orders.domain.OrderState
import org.xbery.artbeams.orders.service.OrderService
import org.xbery.artbeams.products.domain.Product
import org.xbery.artbeams.products.repository.ProductRepository
import org.xbery.artbeams.userproducts.service.UserProductService
import org.xbery.artbeams.users.domain.User
import org.xbery.artbeams.users.repository.UserRepository
import org.xbery.artbeams.users.service.UserService

/**
 * Implementation of service for creating orders from administration.
 * @author Generated
 */
@Service
class OrderCreatingAdminServiceImpl(
    private val orderService: OrderService,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val userService: UserService,
    private val userProductService: UserProductService
) : OrderCreatingAdminService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    
    override fun prepareNewOrderData(): CreateOrderData {
        return CreateOrderData()
    }

    override fun createOrder(createData: CreateOrderData): Pair<Boolean, String?> {
        try {
            val user = requireFound(userRepository.findById(createData.userId)) { 
                "User with id '${createData.userId}' not found" 
            }
            
            val product = requireFound(productRepository.findById(createData.productId)) { 
                "Product with id '${createData.productId}' not found" 
            }
            
            // Generate order number and use CREATED state
            val orderNumber = orderService.generateOrderNumber()
            val orderState = OrderState.CONFIRMED
            
            orderService.createOrderOfProduct(
                user.id, 
                product, 
                orderNumber, 
                orderState
            )
            userService.confirmConsent(user.id)

            // Add product to user's library if not already there
            userProductService.addProductToUserLibrary(user.id, product.id)
            
            return Pair(true, null)
        } catch (e: Exception) {
            return Pair(false, e.message)
        }
    }
    
    override fun findAllUsers(): List<User> {
        return userRepository.findUsers()
    }
    
    override fun findAllProducts(): List<Product> {
        return productRepository.findProducts()
    }
} 
