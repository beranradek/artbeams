package org.xbery.artbeams.userproducts.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import org.xbery.artbeams.userproducts.domain.UserProductDetail
import org.xbery.artbeams.userproducts.domain.UserProductInfo
import org.xbery.artbeams.userproducts.repository.UserProductRepository
import org.xbery.artbeams.users.service.LoginService

/**
 * Service providing operations with user products.
 *
 * @author Radek Beran
 */
@Service
class UserProductService(
    private val loginService: LoginService,
    private val userProductRepository: UserProductRepository
) {

    /**
     * Adds a product to user's library.
     * @return true if the product was added, false if it was already present in the library
     */
    fun addProductToUserLibrary(userId: String, productId: String): Boolean {
        return userProductRepository.addProductToUserLibrary(userId, productId)
    }

    /**
     * Finds user products for currently logged user.
     */
    fun findUserProducts(request: HttpServletRequest): List<UserProductInfo> {
        val loggedUser = loginService.requireLoggedUser(request)
        return userProductRepository.findUserProducts(loggedUser.id)
    }

    /**
     * Finds user product for currently logged user by product slug.
     */
    fun findUserProductBySlug(productSlug: String, request: HttpServletRequest): UserProductDetail? {
        val loggedUser = loginService.requireLoggedUser(request)
        return userProductRepository.findUserProduct(loggedUser.id, productSlug)
    }
}